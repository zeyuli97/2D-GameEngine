package components;

import jade.Camera;
import jade.GameObject;
import jade.Window;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import physics2d.Physics2D;
import physics2d.components.Rigidbody2D;
import util.AssetPool;

public class TurtleAI extends Component {
  private transient boolean goingRight = false;
  private transient Rigidbody2D rigidbody2D;
  private transient float walkSpeed = 0.6f;
  private transient Vector2f velocity = new Vector2f();
  private transient Vector2f acceleration = new Vector2f();
  private transient Vector2f terminalVelocity = new Vector2f(2.1f, 3.1f);
  private transient boolean onGround = false;
  private transient boolean isDead = false;
  private transient boolean isMoving = false;
  private transient StateMachine stateMachine;
  private float movingDebounce = 0.32f;

  @Override
  public void start() {
    this.stateMachine = this.gameObject.getComponent(StateMachine.class);
    this.rigidbody2D = this.gameObject.getComponent(Rigidbody2D.class);
    this.acceleration.y = Window.getPhysics().getGravity().y * 0.7f;
  }

  @Override
  public void update(float dt) {
    movingDebounce -= dt;
    Camera camera = Window.getCurrentScene().getCamera();
    if (this.gameObject.transform.position.x >
            camera.position.x + camera.getProjectionSize().x * camera.getZoom()) {
      return;
    }

    if (!isDead || isMoving) {
      if (goingRight) {
        gameObject.transform.scale.x = -0.25f;
        velocity.x = walkSpeed;
        acceleration.x = 0;
      } else {
        gameObject.transform.scale.x = 0.25f;
        velocity.x = -walkSpeed;
        acceleration.x = 0;
      }
    } else {
      velocity.x = 0;
    }

    checkOnGround();
    if (onGround) {
      this.acceleration.y = 0;
      this.velocity.y = 0;
    } else {
      this.acceleration.y = Window.getPhysics().getGravity().y * 0.7f;
    }
    this.velocity.y += this.acceleration.y * dt;
    this.velocity.y = Math.max(Math.min(this.velocity.y, this.terminalVelocity.y), -terminalVelocity.y);
    this.rigidbody2D.setVelocity(velocity);

    if (this.gameObject.transform.position.x <
            Window.getCurrentScene().getCamera().position.x - 0.5f) {// ||
      //this.gameObject.transform.position.y < 0.0f) {
      //this.gameObject.destroy();
    }
  }

  public void checkOnGround() {
    float innerPlayerWidth = 0.25f * 0.7f;
    float yVal = -0.2f;
    onGround = Physics2D.checkOnGround(this.gameObject, innerPlayerWidth, yVal);
  }

  public void stomp() {
    this.isDead = true;
    this.isMoving = false;
    this.velocity.zero();
    this.rigidbody2D.setVelocity(this.velocity);
    this.rigidbody2D.setAngularVelocity(0.0f);
    this.rigidbody2D.setGravityScale(0.0f);
    this.stateMachine.trigger("squashMe");
    AssetPool.getSound("assets/sounds/bump.ogg").playSound();
  }

  @Override
  public void preSolve(GameObject obj, Contact contact, Vector2f contactNormal) {
    GoombaAI goombaAI = obj.getComponent(GoombaAI.class);
    if (isDead && isMoving && goombaAI != null) {
      goombaAI.stomp();
      contact.setEnabled(false);
      AssetPool.getSound("assets/sounds/kick.ogg").playSound();
    }

    PlayerController playerController = obj.getComponent(PlayerController.class);
    if (playerController != null) {
      if (!isDead && !playerController.getIsDead() &&
              !playerController.isHurtInvincible() &&
              contactNormal.y > 0.58f) {
        playerController.enemyBounce();
        stomp();
        walkSpeed *= 3.0f;
      } else if (movingDebounce < 0 && !playerController.getIsDead() &&
              !playerController.isHurtInvincible() &&
              (isMoving || !isDead) && contactNormal.y < 0.58f) {
        playerController.die();
        if (!playerController.getIsDead()) {
          contact.setEnabled(false);
        }
      } else if (!playerController.getIsDead() && !playerController.isHurtInvincible()) {
        if (isDead && contactNormal.y > 0.58f) {
          playerController.enemyBounce();
          isMoving = !isMoving;
          goingRight = contactNormal.x < 0;
        } else if (isDead && !isMoving) {
          isMoving = true;
          goingRight = contactNormal.x < 0;
          movingDebounce = 0.32f;
        }
      } else if (!playerController.getIsDead() && playerController.isHurtInvincible()) {
        contact.setEnabled(false);
      }
    } else if (Math.abs(contactNormal.y) < 0.1f && !obj.isDead() && obj.getComponent(MushroomAI.class) == null) {
      goingRight = contactNormal.x < 0;
      if (isMoving && isDead) {
        AssetPool.getSound("assets/sounds/bump.ogg").playSound();
      }
    }

    if (obj.getComponent(Fireball.class) != null) {
      stomp();
      if (!isDead) {
        walkSpeed = walkSpeed * 3f;
        stomp();
      } else {
        isMoving = !isMoving;
        goingRight = contactNormal.x < 0;
      }
      obj.getComponent(Fireball.class).disappear();
      contact.setEnabled(false);
    }
  }

}
