package components;

import editor.WindowProperties;
import jade.GameObject;
import jade.KeyListerner;
import jade.Window;
import util.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.*;

public class KeyControl extends Component {

  private float debounceTime = 0.2f;
  private float debounce = 0f;

  @Override
  public void editorUpdate(float dt) {
    WindowProperties windowProperties = Window.getImGuiLayer().getWindowProperties();
    GameObject activeGameObject = windowProperties.getActiveGameObject();
    List<GameObject> activeGameObjects = windowProperties.getActiveGoGroup();

    if (activeGameObject == null) {
      return;
    }

    float mutiplier = 1;
    if (KeyListerner.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
      mutiplier = 0.1f;
    }

    debounce -= dt;


    if (KeyListerner.isKeyPressed(GLFW_KEY_LEFT_CONTROL) && KeyListerner.isKeyFirstPressed(GLFW_KEY_D)
            && activeGameObject != null &&activeGameObjects.size() <= 1) {
      GameObject go = activeGameObject.copy();
      Window.getCurrentScene().addGameObjectToScene(go);
      go.transform.position.add(Settings.Grid_Width,0f); // slide off for dragging.
      windowProperties.setActiveGameObject(go);
//      if (go.getComponent(StateMachine.class) != null) {
//        go.getComponent(StateMachine.class).refreshTextures();
//      }

    } else if (KeyListerner.isKeyPressed(GLFW_KEY_BACKSPACE)) {
      for (GameObject go : activeGameObjects) {
        go.destroy();
      }
      windowProperties.clearSelected();

    } else if (KeyListerner.isKeyPressed(GLFW_KEY_LEFT_CONTROL) && KeyListerner.isKeyFirstPressed(GLFW_KEY_V) && activeGameObject != null) {
      GameObject go = activeGameObject.copy();
      Window.getCurrentScene().addGameObjectToScene(go);
      go.transform.position.add(0f,0.25f); // slide off for dragging.
      windowProperties.setActiveGameObject(go);

    } else if (KeyListerner.isKeyPressed(GLFW_KEY_LEFT_CONTROL) && activeGameObjects.size() > 1 && KeyListerner.isKeyPressed(GLFW_KEY_D)) {
      List<GameObject> activeCopys = new ArrayList<>(activeGameObjects);
      windowProperties.clearSelected();
      for (GameObject go : activeCopys) {
        GameObject copy = go.copy();
        Window.getCurrentScene().addGameObjectToScene(copy);
        windowProperties.addActiveGameObject(copy);
//        if (copy.getComponent(StateMachine.class) != null) {
//          copy.getComponent(StateMachine.class).refreshTextures();
//        }
      }
    } else if (KeyListerner.isKeyPressed(GLFW_KEY_F) && debounce < 0) {
      debounce = debounceTime;
      for (GameObject go : activeGameObjects) {
        go.transform.zIndex++;
      }
    } else if (KeyListerner.isKeyPressed(GLFW_KEY_B) && debounce < 0) {
      debounce = debounceTime;
      for (GameObject go : activeGameObjects) {
        go.transform.zIndex--;
      }
    } else if (KeyListerner.isKeyPressed(GLFW_KEY_UP) && debounce < 0) {
      debounce = debounceTime;
      for (GameObject go : activeGameObjects) {
        go.transform.position.y += Settings.Grid_Height * mutiplier;
      }
    } else if (KeyListerner.isKeyPressed(GLFW_KEY_DOWN) && debounce < 0) {
      debounce = debounceTime;
      for (GameObject go : activeGameObjects) {
        go.transform.position.y -= Settings.Grid_Height * mutiplier;
      }
    } else if (KeyListerner.isKeyPressed(GLFW_KEY_LEFT) && debounce < 0) {
      debounce = debounceTime;
      for (GameObject go : activeGameObjects) {
        go.transform.position.x -= Settings.Grid_Width * mutiplier;
      }
    } else if (KeyListerner.isKeyPressed(GLFW_KEY_RIGHT) && debounce < 0) {
      debounce = debounceTime;
      for (GameObject go : activeGameObjects) {
        go.transform.position.x += Settings.Grid_Width * mutiplier;
      }
    }
  }
}
