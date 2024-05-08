package editor;

import Scene.Scene;
import components.NonActiveGameObjectClass;
import imgui.ImGui;
import jade.GameObject;
import jade.KeyListerner;
import jade.MouseListener;
import physics2d.components.Box2DCollider;
import physics2d.components.CircleCollider;
import physics2d.components.RigidBody2D;
import renders.PickingTexture;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_P;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class WindowProperties {
  private GameObject activeGameObject = null;
  private PickingTexture pickingTexture;

  private float debounceTimer = 0.02f;
  private float deBounce = debounceTimer;

  public WindowProperties(PickingTexture pickingTexture) {
    this.pickingTexture = pickingTexture;
  }

  public void update(double dt, Scene currentScene) {
    deBounce -= dt;


    /**
     * The issue is the isDragging is not correctly working for macbook track pad.
     *
     *
     * There is dragging happening:
     * When dragging occur, we should not update current active game object
     * There is active do nothing.
     * There is no active do nothing
     *
     *
     * There is no dragging happening.
     * Cases1 : there is active game object and no dragging we can switch freely.
     * */
    if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && deBounce < 0) {
      int x = (int) MouseListener.getScreenX();
      int y = (int) MouseListener.getScreenY();
      int gameObjectID = pickingTexture.readPixel(x, y);
      System.out.println(gameObjectID);
      GameObject picked = currentScene.getGameObject(gameObjectID);
      if (picked != null && picked.getComponent(NonActiveGameObjectClass.class) == null) {
        activeGameObject = picked;
      }  else if (picked == null) {
        activeGameObject = null;
      }
      this.deBounce = debounceTimer;
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
