package components;

import jade.GameObject;
import jade.MouseListener;
import jade.Window;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class MouseControl extends Component {
  GameObject holdingObj = null;


  public void pickupObject(GameObject gameObject) {
    holdingObj = gameObject;
    Window.getCurrentScene().addGameObjectToScene(gameObject);
  }

  public void dropObject() {
    this.holdingObj = null;
  }

  public void update(double dt) {
    if (holdingObj != null) {
      holdingObj.getTransform().setPositionX(MouseListener.getOrthoX() - 16);
      holdingObj.getTransform().setPositionY(MouseListener.getOrthoY() - 16);

      if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
        this.dropObject();
      }
    }
  }
}
