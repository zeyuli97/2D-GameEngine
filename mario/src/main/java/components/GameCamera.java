package components;

import jade.Camera;
import jade.GameObject;
import jade.Window;
import org.joml.Vector4f;

public class GameCamera extends Component {
  private transient GameObject player;
  private transient Camera gameCamera;
  private transient float xBoundary = Float.MIN_VALUE;
  private transient float undergroundYBoundary = 0f;
  private transient float cameraBuffer = 1.5f; // Which is 6 grids
  private transient float playerBuffer = 0.25f;

  private Vector4f skyColor = new Vector4f(92/255f, 148/255f, 252/255f, 1.0f);
  private Vector4f underGroundColor = new Vector4f(0,0,0,1);

  public GameCamera(Camera camera) {
    this.gameCamera = camera;
  }

  @Override
  public void start() {
    this.player = Window.getCurrentScene().getGameObjectWithClass(PlayerController.class);
    this.gameCamera.clearColor.set(skyColor);
    this.undergroundYBoundary = this.gameCamera.getPosition().y - this.gameCamera.getProjectionSize().y - this.cameraBuffer;

  }

  @Override
  public void update(float dt) {
    if (player != null && !player.getComponent(PlayerController.class).hasWon()) {
      gameCamera.position.x = Math.max(player.transform.position.x - 2.5f, xBoundary);
      xBoundary = Math.max(xBoundary, gameCamera.position.x);

      if (player.transform.position.y < -playerBuffer) {
        this.gameCamera.position.y = undergroundYBoundary;
        this.gameCamera.clearColor.set(underGroundColor);
      } else if (player.transform.position.y > 0f) {
        this.gameCamera.position.y = 0f;
        this.gameCamera.clearColor.set(skyColor);
      }
    }
  }

}
