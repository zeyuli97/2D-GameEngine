package components;

import jade.GameObject;
import jade.KeyListerner;
import jade.MouseListener;
import jade.Window;
import org.joml.Vector4f;
import util.Settings;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class MouseControl extends Component {
  private GameObject holdingObj = null;

  private float debounceTime = 0.02f;

  private float debounce = debounceTime;


  public void pickupObject(GameObject gameObject) {
    if (this.holdingObj != null) {
      this.holdingObj.destroy();
    }
    holdingObj = gameObject;
    this.holdingObj.getComponent(SpriteRender.class).setColor(new Vector4f(0.8f, 0.8f, 0.8f, 0.5f));
    this.holdingObj.addComponent(new NonActiveGameObjectClass());
    Window.getCurrentScene().addGameObjectToScene(gameObject);
  }

  public void place() {
    GameObject go = this.holdingObj.copy();
    go.getComponent(SpriteRender.class).setColor(new Vector4f(1,1,1,1));
    go.removeComponent(NonActiveGameObjectClass.class);
    Window.getCurrentScene().addGameObjectToScene(go);

    this.holdingObj.destroy();
    this.holdingObj = null;
  }

  public void editorUpdate(double dt) {
    debounce -= dt;
    if (holdingObj != null && debounce <= 0f) {

      holdingObj.getTransform().setPositionX(MouseListener.getWorldCoordX());
      holdingObj.getTransform().setPositionY(MouseListener.getWorldCoordY());

      holdingObj.getTransform().setPositionX(((int) Math.floor(holdingObj.getTransform().getPosition().x / Settings.Grid_Width)) * Settings.Grid_Width + Settings.Grid_Width / 2f);
      holdingObj.getTransform().setPositionY(((int) Math.floor(holdingObj.getTransform().getPosition().y / Settings.Grid_Height)) * Settings.Grid_Height + Settings.Grid_Height / 2f);

      if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
        place();
        debounce = debounceTime;
      }

      if (KeyListerner.isKeyPressed(GLFW_KEY_ESCAPE)) {
        holdingObj.destroy();
        holdingObj = null;
        debounce = debounceTime;
      }
    }
  }
}
