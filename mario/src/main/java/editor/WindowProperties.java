package editor;

import Scene.Scene;
import components.NonActiveGameObjectClass;
import imgui.ImGui;
import jade.GameObject;
import jade.MouseListener;
import physics2d.components.Box2DCollider;
import physics2d.components.CircleCollider;
import physics2d.components.RigidBody2D;
import renders.PickingTexture;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class WindowProperties {
  private GameObject activeGameObject = null;
  private PickingTexture pickingTexture;

  private float deBounce = 0.005f;

  public WindowProperties(PickingTexture pickingTexture) {
    this.pickingTexture = pickingTexture;
  }

  public void update(double dt, Scene currentScene) {
    deBounce -= dt;

    if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && deBounce < 0) {
      int x = (int) MouseListener.getScreenX();
      int y = (int) MouseListener.getScreenY();
      int gameObjectID = pickingTexture.readPixel(x, y);
      System.out.println(gameObjectID);
      GameObject picked = currentScene.getGameObject(gameObjectID);
      if (picked != null && picked.getComponent(NonActiveGameObjectClass.class) == null) {
        activeGameObject = picked;
      } else if (gameObjectID == -1) {
        activeGameObject = null;
      } else if (picked == null && !MouseListener.getIsDragging()) {
        activeGameObject = null;
      }
      this.deBounce = 0.005f;
    }
  }

  public void imgui() {
    if (activeGameObject != null) {
      ImGui.begin("Property Window");

      if (ImGui.beginPopupContextWindow("ComponentAdder")) {
        if (ImGui.menuItem("Add RigidBody")) {
          if (activeGameObject.getComponent(RigidBody2D.class) == null) {
            activeGameObject.addComponent(new RigidBody2D());
          }
        }
        if (ImGui.menuItem("Add BoxCollider")) {
          if (activeGameObject.getComponent(Box2DCollider.class) == null
                  && activeGameObject.getComponent(CircleCollider.class) == null) {
            activeGameObject.addComponent(new Box2DCollider());
          }
        }

        if (ImGui.menuItem("Add CircleCollider")) {
          if (activeGameObject.getComponent(CircleCollider.class) == null
          && activeGameObject.getComponent(Box2DCollider.class) == null) {
            activeGameObject.addComponent(new CircleCollider());
          }
        }

        ImGui.endPopup();
      }

      activeGameObject.imgui();
      ImGui.end();
    }
  }

  public GameObject getActiveGameObject() {
    return activeGameObject;
  }

  public void setActiveGameObject(GameObject activeGameObject) {
    this.activeGameObject = activeGameObject;
  }
}
