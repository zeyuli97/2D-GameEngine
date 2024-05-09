package editor;

import Scene.Scene;
import components.NonActiveGameObjectClass;
import components.SpriteRender;
import components.SpriteSheet;
import imgui.ImGui;
import jade.GameObject;
import jade.KeyListerner;
import jade.MouseListener;
import org.joml.Vector4f;
import physics2d.components.Box2DCollider;
import physics2d.components.CircleCollider;
import physics2d.components.Rigidbody2D;
import renders.PickingTexture;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_P;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class WindowProperties {

  private List<Vector4f> activeGameObjectOriginalColor = new ArrayList<>();
  private GameObject activeGameObject = null;
  private List<GameObject> activeGoGroup;

  private PickingTexture pickingTexture;

//  private float debounceTimer = 0.02f;
//  private float deBounce = debounceTimer;

  public WindowProperties(PickingTexture pickingTexture) {
    this.pickingTexture = pickingTexture;
    this.activeGoGroup = new ArrayList<>();
  }

//  public void update(double dt, Scene currentScene) {
//    deBounce -= dt;
//
//
//    /**
//     * The issue is the isDragging is not correctly working for macbook track pad.
//     *
//     *
//     * There is dragging happening:
//     * When dragging occur, we should not update current active game object
//     * There is active do nothing.
//     * There is no active do nothing
//     *
//     *
//     * There is no dragging happening.
//     * Cases1 : there is active game object and no dragging we can switch freely.
//     * */
//    if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && deBounce < 0) {
//      int x = (int) MouseListener.getScreenX();
//      int y = (int) MouseListener.getScreenY();
//      int gameObjectID = pickingTexture.readPixel(x, y);
//      System.out.println(gameObjectID);
//      GameObject picked = currentScene.getGameObject(gameObjectID);
//      if (picked != null && picked.getComponent(NonActiveGameObjectClass.class) == null) {
//        setActiveGameObject(picked);
//      }  else if (picked == null) {
//        setActiveGameObject(null);
//      }
//      this.deBounce = debounceTimer;
//    }
//  }

  public void imgui() {
    if (activeGoGroup.size() == 1 && activeGoGroup.get(0) != null) {
      activeGameObject = activeGoGroup.get(0);
      ImGui.begin("Property Window");

      if (ImGui.beginPopupContextWindow("ComponentAdder")) {
        if (ImGui.menuItem("Add RigidBody")) {
          if (activeGameObject.getComponent(Rigidbody2D.class) == null) {
            activeGameObject.addComponent(new Rigidbody2D());
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
    if (activeGoGroup.size() == 1) {
      return activeGoGroup.getFirst();
    }
    return null;
  }

  public void setActiveGameObject(GameObject activeGameObject) {
    clearSelected();
    this.activeGoGroup.add(activeGameObject);
  }

  public void clearSelected() {
    if (activeGameObjectOriginalColor.size() > 0) {
      int i = 0;
      for (GameObject go : activeGoGroup) {
        SpriteRender spriteRender = go.getComponent(SpriteRender.class);
        if (spriteRender != null) {
          spriteRender.setColor(activeGameObjectOriginalColor.get(i));
        }
        i++;
      }
    }
    this.activeGoGroup.clear();
    this.activeGameObjectOriginalColor.clear();
  }

  public List<GameObject> getActiveGoGroup() {
    return activeGoGroup;
  }

  public void addActiveGameObject(GameObject gameObject) {
    SpriteRender spriteRender= gameObject.getComponent(SpriteRender.class);
    if (spriteRender != null) {
      this.activeGameObjectOriginalColor.add(new Vector4f(spriteRender.getColor()));
      spriteRender.setColor(new Vector4f(.8f, .8f, 0.0f, .8f));
    } else {
      this.activeGameObjectOriginalColor.add(new Vector4f());
    }
    this.activeGoGroup.add(gameObject);
  }

  public PickingTexture getPickingTexture() {
    return pickingTexture;
  }
}
