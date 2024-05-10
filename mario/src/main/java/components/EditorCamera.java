package components;

import jade.Camera;
import jade.KeyListerner;
import jade.MouseListener;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;


/**
 * Because I am using trackpad, I used slightly different key/mouse binding.
 * Right click and hold for camera movement.
 * Press c to re-center the camera and zoom back to ratio 1.0f.
 * Press z + scroll in Y direction will zoom in or out.
 * */
public class EditorCamera extends Component {

  private float dragDebounce = 0.02f;
  private float dragSensitivity = 10f;
  private float scrollSensitivity = 0.05f;
  private boolean reset = false;
  private float lerpTime = 0;


  private Camera levelEditorCamera;
  private Vector2f clickOrigin;

  public EditorCamera(Camera levelEditorCamera) {
    this.levelEditorCamera = levelEditorCamera;
    this.clickOrigin = new Vector2f();
  }


  @Override
  public void editorUpdate(float dt) {
    if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_RIGHT) && dragDebounce > 0) {
      this.clickOrigin = MouseListener.getWorldCoord();
      dragDebounce -=  dt;
      return;
    } else if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_RIGHT)) {
      Vector2f mousePos = MouseListener.getWorldCoord();
      Vector2f delta = new Vector2f(mousePos).sub(clickOrigin);
      levelEditorCamera.setPosition(levelEditorCamera.getPosition().sub(delta.mul(dt).mul(dragSensitivity)));
      this.clickOrigin.lerp(mousePos, dt);
    }

    // This is crucial to make the next camera move smooth.
    if (dragDebounce <= 0 && !MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_RIGHT)) {
      dragDebounce = 0.02f;
    }

    if (MouseListener.getScrollY() != 0 && KeyListerner.isKeyPressed(GLFW_KEY_Z)) {

      //System.out.println(MouseListener.getScrollY());
      float addValue = (float) Math.pow(Math.abs(MouseListener.getScrollY() * scrollSensitivity), 1 / (levelEditorCamera.getZoom()));

      addValue *= (float) (Math.signum(MouseListener.getScrollY()));
      levelEditorCamera.addZoom(addValue);
    }

    if (KeyListerner.isKeyPressed(GLFW_KEY_C)) {
      reset = true;
    }

    if (reset) {
      levelEditorCamera.setPosition(levelEditorCamera.getPosition().lerp(new Vector2f(0, 0), lerpTime));
      levelEditorCamera.setZoom(this.levelEditorCamera.getZoom() + (1 - this.levelEditorCamera.getZoom()) * lerpTime);
      this.lerpTime += 0.1f * dt;

      if (Math.abs(levelEditorCamera.getPosition().x) <= 1.0f && Math.abs(levelEditorCamera.getPosition().y) <= 1.0f) {
        this.lerpTime = 0;
        levelEditorCamera.setPosition(new Vector2f(0,0));
        this.levelEditorCamera.setZoom(1.0f);
        reset = false;
      }
    }
  }

}
