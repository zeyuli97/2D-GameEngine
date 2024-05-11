package components;

import Scene.LevelEditorSceneInitializer;
import Scene.LevelSceneInitializer;
import jade.GameObject;
import jade.KeyListerner;
import jade.Prefabs;
import jade.Window;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import org.joml.Vector4f;
import physics2d.Physics2D;
import physics2d.RaycastInfo;
import physics2d.components.Rigidbody2D;
import physics2d.enums.BodyType;
import renders.DebugDraw;
import util.AssetPool;

import java.util.Objects;
import java.util.Vector;

import static org.lwjgl.glfw.GLFW.*;

public class PlayerController extends Component {
  private enum PlayerState {
    Small,
    Big,
    Fire,
    Invincible
  }

  private transient float hurtInVincibilityTimer = 1.4f;
  private transient float hurtInvincibilityTimeLeft = 0;
  private transient float deadMaxHeight = 0;
  private transient float deadMinHeight = 0;
  private transient boolean deadGoingUp = true;
  private transient float blinkTime = 0f;
  private transient SpriteRender spr;

  public float walkSpeed = 1.9f;
  public float jumpBoost = .5f;
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
  private transient boolean won = false;
  private transient float timeToCastle = 4.5f;
  private transient float walkTime = 2.2f;


  @Override
  public void start() {
    this.rb = gameObject.getComponent(Rigidbody2D.class);
    this.stateMachine = gameObject.getComponent(StateMachine.class);
    this.rb.setGravityScale(0.0f); // Will control the mario gravity related by ourselves not box2D.
    this.spr = gameObject.getComponent(SpriteRender.class);
  }

  @Override
  public void update(float dt) {
    if (won) {
      checkOnGround();
      if (!onGround) {
        gameObject.transform.scale.x = -0.25f;
        gameObject.transform.position.y -= dt;
        stateMachine.trigger("stopRunning");
        stateMachine.trigger("stopJumping");
      } else {
        if (this.walkTime > 0) {
          gameObject.transform.scale.x = 0.25f;
          gameObject.transform.position.x += dt;
          stateMachine.trigger("startRunning");
        }
        if (!AssetPool.getSound("assets/sounds/stage_clear.ogg").isPlaying()) {
          AssetPool.getSound("assets/sounds/stage_clear.ogg").playSound();
        }
        timeToCastle -= dt;
        walkTime -= dt;

        if (timeToCastle <= 0) {
          Window.setRunTimePlaying(false);
          Window.changeScene(new LevelEditorSceneInitializer());
        }
      }


      return;
    }

    if (isDead) {
      if (this.gameObject.transform.position.y < deadMaxHeight && deadGoingUp) {
        this.gameObject.transform.position.y += dt * walkSpeed / 2.0f;
      } else if (this.gameObject.transform.position.y >= deadMaxHeight && deadGoingUp) {
        deadGoingUp = false;
      } else if (!deadGoingUp && gameObject.transform.position.y > deadMinHeight) {
        this.rb.setBodyType(BodyType.Kinematic);
        this.acceleration.y = Window.getPhysics().getGravity().y * 0.7f;
        this.velocity.y += this.acceleration.y * dt;
        this.velocity.y = Math.max(Math.min(this.velocity.y, this.velocityCap.y), -this.velocityCap.y);
        this.rb.setVelocity(this.velocity);
        this.rb.setAngularVelocity(0);
      } else if (!deadGoingUp && gameObject.transform.position.y <= deadMinHeight) {
        Window.changeScene(new LevelSceneInitializer());
      }
      return;
    }

    if (hurtInvincibilityTimeLeft > 0) {
      hurtInvincibilityTimeLeft -= dt;
      blinkTime -= dt;

      if (blinkTime <= 0) {
        blinkTime = 0.2f;
        if (spr.getColor().w == 1) {
          spr.setColor(new Vector4f(1, 1, 1, 0));
        } else {
          spr.setColor(new Vector4f(1, 1, 1, 1));
        }
      } else {
        if (spr.getColor().w == 0) {
          spr.setColor(new Vector4f(1, 1, 1, 1));
        }
      }
    }

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

    if (KeyListerner.isKeyFirstPressed(GLFW_KEY_E) && playerState == PlayerState.Fire && Fireball.canSpawn()) {
      Vector2f fireballPosition = new Vector2f(gameObject.transform.position.x, gameObject.transform.position.y);
      if (this.gameObject.transform.scale.x > 0) {
        fireballPosition.add(new Vector2f(0.26f, 0));
      } else {
        fireballPosition.add(new Vector2f(-0.26f, 0));
      }

      GameObject fireball  = Prefabs.generateFireball(fireballPosition);
      fireball.getComponent(Fireball.class).goingRight = (this.gameObject.transform.scale.x > 0);
      Window.getCurrentScene().addGameObjectToScene(fireball);
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
    } else if (enemyBounce > 0) {
      enemyBounce--;
      this.velocity.y = ((enemyBounce / 2.2f) * jumpBoost);
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

    float innerPlayerWidth = this.playerWidth * 0.6f;
    float yVal;
    if (playerState == PlayerState.Small) {
      yVal = -0.14f;
    } else {
      yVal = -0.24f;
    }
    onGround = Physics2D.checkOnGround(this.gameObject, innerPlayerWidth, yVal);
  }

  public void playWinAnimation(GameObject flagpole) {
    if (!won) {
      won = true;
      velocity.set(0,0);
      acceleration.set(0, 0);
      rb.setVelocity(velocity);
      rb.setIsSensor();
      rb.setBodyType(BodyType.Static);
      gameObject.transform.position.x = flagpole.transform.position.x;
      gameObject.transform.position.y = flagpole.transform.position.y;
      AssetPool.getSound("assets/sounds/main-theme-overworld.ogg").stopSound();
      AssetPool.getSound("assets/sounds/flagpole.ogg").playSound();
    }
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
        pb.setHeight(0.42f);
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

  public boolean getIsDead() {
    return isDead;
  }

  public boolean isHurtInvincible() {
    return this.hurtInvincibilityTimeLeft > 0 || won;
  }

  public boolean isInvincible() {
    return this.playerState == PlayerState.Invincible || isHurtInvincible();
  }

  public void enemyBounce() {
    this.enemyBounce = 8;
  }

  public void die() {
    this.stateMachine.trigger("die");
    if (this.playerState == PlayerState.Small) {
      this.velocity.set(0,0);
      this.acceleration.set(0,0);
      this.rb.setVelocity(this.velocity);
      this.isDead = true;
      this.rb.setIsSensor();
      AssetPool.getSound("assets/sounds/main-theme-overworld.ogg").stopSound();
      AssetPool.getSound("assets/sounds/mario_die.ogg").playSound();
      deadMaxHeight = this.gameObject.transform.position.y + 0.3f;
      this.rb.setBodyType(BodyType.Static);
      if (gameObject.transform.position.y > 0) {
        deadMinHeight = -0.25f;
      }
    } else if (this.playerState == PlayerState.Big) {
      this.playerState = PlayerState.Small;
      gameObject.transform.scale.y = 0.25f;
      PillboxCollider pb = gameObject.getComponent(PillboxCollider.class);
      if (pb != null) {
        jumpBoost /= bigJumpBoostFactor;
        walkSpeed *= bigJumpBoostFactor;
        pb.setHeight(0.25f);
      }
      hurtInvincibilityTimeLeft = hurtInVincibilityTimer;
      AssetPool.getSound("assets/sounds/pipe.ogg").playSound();
    } else if (this.playerState == PlayerState.Fire) {
      this.playerState = PlayerState.Big;
      hurtInvincibilityTimeLeft = hurtInVincibilityTimer;
      AssetPool.getSound("assets/sounds/pipe.ogg").playSound();
    }
  }

  public void setPosition(Vector2f position) {
    this.gameObject.transform.position.set(position);
    this.rb.setPosition(position);
  }

  public boolean hasWon() {
    return false;
  }

}
