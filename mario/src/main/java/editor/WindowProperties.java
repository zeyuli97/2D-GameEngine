package editor;

import Scene.Scene;
import imgui.ImGui;
import jade.GameObject;
import jade.MouseListener;
import renders.PickingTexture;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class WindowProperties {
  private GameObject activeGameObject = null;
  private PickingTexture pickingTexture;

  public WindowProperties(PickingTexture pickingTexture) {
    this.pickingTexture = pickingTexture;
  }

  public void update(double dt, Scene currentScene) {
    if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
      int x = (int) MouseListener.getScreenX();
      int y = (int) MouseListener.getScreenY();
      int gameObjectID = pickingTexture.readPixel(x, y);
      //System.out.println(gameObjectID);
      activeGameObject = currentScene.getGameObject(gameObjectID);
      //System.out.println("Active GameObject: " + activeGameObject.getUid());
    }
  }

  public void imgui() {
    if (activeGameObject != null) {
      ImGui.begin("Property Window");
      activeGameObject.imgui();
      ImGui.end();
    }
  }
}
