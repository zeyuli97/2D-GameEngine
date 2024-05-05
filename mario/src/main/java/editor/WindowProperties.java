package editor;

import Scene.Scene;
import components.NonActiveGameObjectClass;
import components.TranslateGizmo;
import imgui.ImGui;
import jade.GameObject;
import jade.MouseListener;
import renders.PickingTexture;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class WindowProperties {
  private GameObject activeGameObject = null;
  private PickingTexture pickingTexture;

  private float deBounce = 0.2f;

  public WindowProperties(PickingTexture pickingTexture) {
    this.pickingTexture = pickingTexture;
  }

  public void update(double dt, Scene currentScene) {
    deBounce -= dt;

    if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && deBounce < 0) {
      int x = (int) MouseListener.getScreenX();
      int y = (int) MouseListener.getScreenY();
      int gameObjectID = pickingTexture.readPixel(x, y);
      GameObject picked = currentScene.getGameObject(gameObjectID);
      if (picked != null && picked.getComponent(NonActiveGameObjectClass.class) == null) {
        activeGameObject = picked;
      } else if (picked == null && !MouseListener.getIsDragging()) {
        activeGameObject = null;
      }
      this.deBounce = 0.2f;
    }
  }

  public void imgui() {
    if (activeGameObject != null) {
      ImGui.begin("Property Window");
      activeGameObject.imgui();
      ImGui.end();
    }
  }

  public GameObject getActiveGameObject() {
    return activeGameObject;
  }
}
