package components;

import jade.GameObject;
import jade.KeyListerner;
import jade.Window;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import org.joml.Vector3f;
import physics2d.RaycastInfo;
import physics2d.components.Rigidbody2D;
import renders.DebugDraw;
import util.AssetPool;

import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;

public class PlayerController extends Component {
  private enum PlayerState {
    Small,
    Big,
    Fire,
    Invincible
  }

  public float walkSpeed = 1.9f;
  public float jumpBoost = 1.0f;
  public float jumpImpulse = 3.0f;
  public float slowDownForce = 0.05f; // The speed reduced due to Mario turn direction.
  public Vector2f velocityCap = new Vector2f(2.1f, 3.1f);

  private PlayerState playerState = PlayerState.Small;
  public transient boolean onGround = false;
  private transient float groundDebounce = 0.0f;
  private transient float groundDebounceTime = 0.1f; // use for grace time user can jump at the edge of the block.
  private transient Rigidbody2D rb;
  private transient StateMachine stateMachine;
  private transient float bigJumpBoostFactor = 1.05f;
  private transient float playerWidth = 0.25f;
  private transient int jumpTime = 0; // How long user hold the jump - space.
  private transient Vector2f acceleration = new Vector2f();
  private transient Vector2f velocity = new Vector2f();
  private transient boolean isDead = false;
  private transient int enemyBounce = 0;

  @Override
  public void start() {
    this.rb = gameObject.getComponent(Rigidbody2D.class);
    this.stateMachine = gameObject.getComponent(StateMachine.class);
    this.rb.setGravityScale(0.0f); // Will control the mario gravity related by ourselves not box2D.
  }

  @Override
  public void update(float dt) {
    if (KeyListerner.isKeyPressed(GLFW_KEY_RIGHT) || KeyListerner.isKeyPressed(GLFW_KEY_D)) {
      this.gameObject.transform.scale.x = playerWidth;
      this.acceleration.x = walkSpeed;

      if (this.velocity.x < 0) {
        this.stateMachine.trigger("switchDirection");
        this.velocity.x += slowDownForce;
      } else {
        this.stateMachine.trigger("startRunning");
      }
    } else if (KeyListerner.isKeyPressed(GLFW_KEY_LEFT) || KeyListerner.isKeyPressed(GLFW_KEY_A)) {
      this.gameObject.transform.scale.x = -playerWidth;
      this.acceleration.x = -walkSpeed;

      if (this.velocity.x > 0) {
        this.stateMachine.trigger("switchDirection");
        this.velocity.x -= slowDownForce;
      } else {
        this.stateMachine.trigger("startRunning");
      }
    } else {
      this.acceleration.x = 0;
      if (this.velocity.x > 0) {
        this.velocity.x = Math.max(0, this.velocity.x - slowDownForce);
      } else if (this.velocity.x < 0) {
        this.velocity.x = Math.min(0, this.velocity.x + slowDownForce);
      }

      if (this.velocity.x == 0) {
        this.stateMachine.trigger("stopRunning");
      }
    }

    checkOnGround();
    if (KeyListerner.isKeyPressed(GLFW_KEY_SPACE) && (jumpTime > 0 || onGround || groundDebounce > 0)) {
      if ((onGround || groundDebounce > 0) && jumpTime == 0) {
        Objects.requireNonNull(AssetPool.getSound("assets/sounds/jump-small.ogg")).playSound();
        jumpTime = 28;
        this.velocity.y = jumpImpulse;
      } else if (jumpTime > 0) {
        jumpTime--;
        this.velocity.y = ((jumpTime / 2.2f) * jumpBoost);
      } else {
        this.velocity.y = 0;
      }
      groundDebounce = 0;
    } else if (!onGround) {
      if (this.jumpTime > 0) {
        this.velocity.y *= 0.35f;
        this.jumpTime = 0;
      }
      groundDebounce -= dt;
      this.acceleration.y = Window.getPhysics().getGravity().y * 0.7f;
    } else {
      this.velocity.y = 0;
      this.acceleration.y = 0;
      groundDebounce = groundDebounceTime;
    }

    this.velocity.x += this.acceleration.x * dt;
    this.velocity.y += this.acceleration.y * dt;
    this.velocity.x = Math.max(Math.min(this.velocity.x, this.velocityCap.x), -this.velocityCap.x);
    this.velocity.y = Math.max(Math.min(this.velocity.y, this.velocityCap.y), -this.velocityCap.y);
    this.rb.setVelocity(this.velocity);
    this.rb.setAngularVelocity(0);
  }

  public void checkOnGround() {
    Vector2f raycastBegin = new Vector2f(this.gameObject.transform.position);
    float innerPlayerWidth = this.playerWidth * 0.6f;
    raycastBegin.sub(innerPlayerWidth / 2.0f, 0.0f);
    float yVal;
    if (playerState == PlayerState.Small) {
      yVal = -0.14f;
    } else {
      yVal = -0.24f;
    }
    Vector2f raycastEnd = new Vector2f(raycastBegin).add(0.0f, yVal);

    RaycastInfo info = Window.getPhysics().raycast(gameObject, raycastBegin, raycastEnd);

    Vector2f raycast2Begin = new Vector2f(raycastBegin).add(innerPlayerWidth, 0.0f);
    Vector2f raycast2End = new Vector2f(raycastEnd).add(innerPlayerWidth, 0.0f);
    RaycastInfo info2 = Window.getPhysics().raycast(gameObject, raycast2Begin, raycast2End);

    onGround = (info.hit && info.hitObject != null && info.hitObject.getComponent(Ground.class) != null) ||
            (info2.hit && info2.hitObject != null && info2.hitObject.getComponent(Ground.class) != null);

    DebugDraw.addLine2D(raycastBegin, raycastEnd, new Vector3f(1, 0, 0));
    DebugDraw.addLine2D(raycast2Begin, raycast2End, new Vector3f(1, 0, 0));
  }

  @Override
  public void beginCollision(GameObject collidingObject, Contact contact, Vector2f contactNormal) {
    if (isDead) return;

    if (collidingObject.getComponent(Ground.class) != null) {
      if (Math.abs(contactNormal.x) > 0.8f) {
        this.velocity.x = 0;
      } else if (contactNormal.y > 0.8f) {
        this.velocity.y = 0;
        this.acceleration.y = 0;
        this.jumpTime = 0;
      }
    }
  }

  public void powerup() {
    if (playerState == PlayerState.Small) {
      playerState = PlayerState.Big;
      AssetPool.getSound("assets/sounds/powerup.ogg").playSound();
      gameObject.transform.scale.y = 0.42f;
      PillboxCollider pb = gameObject.getComponent(PillboxCollider.class);
      if (pb != null) {
        jumpBoost *= bigJumpBoostFactor;
        walkSpeed *= bigJumpBoostFactor;
        pb.setHeight(0.63f);
      }
    } else if (playerState == PlayerState.Big) {
      playerState = PlayerState.Fire;
      AssetPool.getSound("assets/sounds/powerup.ogg").playSound();
    }

    stateMachine.trigger("powerup");
  }

  public boolean isSmall() {
    return this.playerState == PlayerState.Small;
  }

  public boolean isBig() {
    return this.playerState == PlayerState.Big;
  }

  public boolean isInvulnerable() {
    return this.playerState == PlayerState.Invincible;
  }
}
