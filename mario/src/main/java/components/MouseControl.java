package components;

import jade.GameObject;
import jade.MouseListener;
import jade.Window;
import util.Settings;

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

      holdingObj.getTransform().setPositionX(MouseListener.getOrthoX());
      holdingObj.getTransform().setPositionY(MouseListener.getOrthoY());

      System.out.println("This is X: " + MouseListener.getOrthoX() + "and this is Y" + MouseListener.getOrthoY());
      holdingObj.getTransform().setPositionX((int) Math.floor(holdingObj.getTransform().getPosition().x / Settings.Grid_Width) * Settings.Grid_Width);
      holdingObj.getTransform().setPositionY((int) Math.floor(holdingObj.getTransform().getPosition().y / Settings.Grid_Height) * Settings.Grid_Height);
      System.out.println((int) (-20/32));
      System.out.println("This is X after rounding " + ((int) (holdingObj.getTransform().getPosition().x / Settings.Grid_Width)) * Settings.Grid_Width);

      if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
        this.dropObject();
      }
    }
  }
}
