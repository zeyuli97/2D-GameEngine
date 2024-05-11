package components;

import jade.Direction;
import jade.GameObject;
import jade.KeyListerner;
import jade.Window;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import util.AssetPool;

import static org.lwjgl.glfw.GLFW.*;

public class Pipe extends Component {
  private Direction direction;
  private String connectionPipeName = "";
  private boolean isEntrance = false;
  private transient GameObject connectingPipe = null;
  private transient float entranceTolerance = 0.6f;
  private transient PlayerController collidingPlayerController = null;

  public Pipe(Direction direction) {
    this.direction = direction;
  }

  @Override
  public void start() {
    connectingPipe = Window.getCurrentScene().getGameObject(connectionPipeName);
  }

  @Override
  public void update(float dt) {
    if (connectingPipe == null) {
      return;
    }

    if (collidingPlayerController != null) {
      boolean playerEntering = false;
      switch (direction) {
        case Up:
          if ((KeyListerner.isKeyPressed(GLFW_KEY_DOWN)
                  || KeyListerner.isKeyPressed(GLFW_KEY_S))
                  && isEntrance
                  && playerAtEntrance()) {
            playerEntering = true;
          }
          break;
        case Left:
          if ((KeyListerner.isKeyPressed(GLFW_KEY_RIGHT)
                  || KeyListerner.isKeyPressed(GLFW_KEY_D))
                  && isEntrance
                  && playerAtEntrance()) {
            playerEntering = true;
          }
          break;
        case Right:
          if ((KeyListerner.isKeyPressed(GLFW_KEY_LEFT)
                  || KeyListerner.isKeyPressed(GLFW_KEY_A))
                  && isEntrance
                  && playerAtEntrance()) {
            playerEntering = true;
          }
          break;
        case Down:
          if ((KeyListerner.isKeyPressed(GLFW_KEY_UP)
                  || KeyListerner.isKeyPressed(GLFW_KEY_W))
                  && isEntrance
                  && playerAtEntrance()) {
            playerEntering = true;
          }
          break;
      }
      if (playerEntering) {
        collidingPlayerController.setPosition(getPlayerPosition(connectingPipe));
        AssetPool.getSound("assets/sounds/pipe.ogg").playSound();
      }
    }
  }

  public boolean playerAtEntrance() {
    if (collidingPlayerController == null) {
      return false;
    }

    Vector2f min = new Vector2f(gameObject.transform.position).
            sub(new Vector2f(gameObject.transform.scale).mul(0.5f));
    Vector2f max = new Vector2f(gameObject.transform.position).
            add(new Vector2f(gameObject.transform.scale).mul(0.5f));
    Vector2f playerMax = new Vector2f(collidingPlayerController.gameObject.transform.position).
            add(new Vector2f(collidingPlayerController.gameObject.transform.scale).mul(0.5f));
    Vector2f playerMin = new Vector2f(collidingPlayerController.gameObject.transform.position).
            sub(new Vector2f(collidingPlayerController.gameObject.transform.scale).mul(0.5f));

    switch (direction) {
      case Up:
        return playerMin.y >= max.y &&
                playerMax.x > min.x &&
                playerMin.x < max.x;
      case Down:
        return playerMax.y <= min.y &&
                playerMax.x > min.x &&
                playerMin.x < max.x;
      case Right:
        return playerMin.x >= max.x &&
                playerMax.y > min.y &&
                playerMin.y < max.y;
      case Left:
        return playerMin.x <= min.x &&
                playerMax.y > min.y &&
                playerMin.y < max.y;
    }

    return false;

  }

  @Override
  public void beginCollision(GameObject collidingObject, Contact contact, Vector2f contactNormal) {
    PlayerController playerController = collidingObject.getComponent(PlayerController.class);
    if (playerController != null) {
      collidingPlayerController = playerController;
    }
  }

  @Override
  public void endCollision(GameObject collidingObject, Contact contact, Vector2f contactNormal) {
    PlayerController playerController = collidingObject.getComponent(PlayerController.class);
    if (playerController != null) {
      collidingPlayerController = null;
    }
  }


  private Vector2f getPlayerPosition(GameObject pipe) {
    Pipe pipeComponent = pipe.getComponent(Pipe.class);
    switch (pipeComponent.direction) {
      case Up:
        return new Vector2f(pipe.transform.position).add(0.0f, 0.5f);
      case Left:
        return new Vector2f(pipe.transform.position).add(-0.5f, 0.0f);
      case Right:
        return new Vector2f(pipe.transform.position).add(0.5f, 0.0f);
      case Down:
        return new Vector2f(pipe.transform.position).add(0.0f, -0.5f);
    }

    return new Vector2f();
  }

}
