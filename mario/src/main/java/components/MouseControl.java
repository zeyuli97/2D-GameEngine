package components;

import Scene.Scene;
import editor.WindowProperties;
import jade.GameObject;
import jade.KeyListerner;
import jade.MouseListener;
import jade.Window;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;
import renders.DebugDraw;
import renders.PickingTexture;
import util.Settings;


import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class MouseControl extends Component {
  private GameObject holdingObj = null;

  private float debounceTime = 0.2f;

  private float debounce = debounceTime;

  private boolean boxSelectSet = false;

  private Vector2f boxSelectStart = new Vector2f();
  private Vector2f boxSelectEnd = new Vector2f();


  public void pickupObject(GameObject gameObject) {
    if (this.holdingObj != null) {
      this.holdingObj.destroy();
    }
    holdingObj = gameObject;
    //holdingObj.addComponent(new HoldingObj());
    holdingObj.setNoSerialize();
    this.holdingObj.getComponent(SpriteRender.class).setColor(new Vector4f(0.8f, 0.8f, 0.8f, 0.5f));
    this.holdingObj.addComponent(new NonActiveGameObjectClass());
    Window.getCurrentScene().addGameObjectToScene(gameObject);
  }

  public void place() {
    //holdingObj.removeComponent(HoldingObj.class);
    GameObject go = this.holdingObj.copy();
    go.setSERIALIZATION();
    go.getComponent(SpriteRender.class).setColor(new Vector4f(1,1,1,1));
    go.removeComponent(NonActiveGameObjectClass.class);
    Window.getCurrentScene().addGameObjectToScene(go);

    //this.holdingObj.destroy();
    //this.holdingObj = null;
  }

  @Override
  public void editorUpdate(float dt) {
    debounce -= dt;
    PickingTexture pickingTexture = Window.getImGuiLayer().getWindowProperties().getPickingTexture();
    Scene currentScene = Window.getCurrentScene();

    if (holdingObj != null) {
      float x = MouseListener.getWorldCoordX();
      float y = MouseListener.getWorldCoordY();
      holdingObj.transform.position.x = ((int)Math.floor(x / Settings.Grid_Width) * Settings.Grid_Width) + Settings.Grid_Width / 2.0f;
      holdingObj.transform.position.y = ((int)Math.floor(y / Settings.Grid_Height) * Settings.Grid_Height) + Settings.Grid_Height / 2.0f;

      if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
        float halfWidth = Settings.Grid_Width / 2.0f;
        float halfHeight = Settings.Grid_Height / 2.0f;
        if (MouseListener.getIsDragging() &&
                !blockInSquare(holdingObj.transform.position.x - halfWidth,
                        holdingObj.transform.position.y - halfHeight)) {
          place();
        } else if (!MouseListener.getIsDragging() && debounce < 0) {
          place();
          debounce = debounceTime;
        }
      }

      if (KeyListerner.isKeyPressed(GLFW_KEY_ESCAPE)) {
        holdingObj.destroy();
        holdingObj = null;
      }
    } else if (!MouseListener.getIsDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && debounce < 0) {
      int x = (int)MouseListener.getScreenX();
      int y = (int)MouseListener.getScreenY();
      int gameObjectId = pickingTexture.readPixel(x, y);
      GameObject pickedObj = currentScene.getGameObject(gameObjectId);
      if (pickedObj != null && pickedObj.getComponent(NonActiveGameObjectClass.class) == null) {
        Window.getImGuiLayer().getWindowProperties().setActiveGameObject(pickedObj);
      } else if (pickedObj == null && !MouseListener.getIsDragging()) {
        Window.getImGuiLayer().getWindowProperties().clearSelected();
      }
      this.debounce = 0.2f;
    } else if (MouseListener.getIsDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
      if (!boxSelectSet) {
        Window.getImGuiLayer().getWindowProperties().clearSelected();
        boxSelectStart = MouseListener.getScreen();
        boxSelectSet = true;
      }
      boxSelectEnd = MouseListener.getScreen();
      Vector2f boxSelectStartWorld = MouseListener.screenToWorld(boxSelectStart);
      Vector2f boxSelectEndWorld = MouseListener.screenToWorld(boxSelectEnd);
      Vector2f halfSize =
              (new Vector2f(boxSelectEndWorld).sub(boxSelectStartWorld)).mul(0.5f);
      DebugDraw.addBox2D(
              (new Vector2f(boxSelectStartWorld)).add(halfSize),
              new Vector2f(halfSize).mul(2.0f),
              0.0f);
    } else if (boxSelectSet) {
      boxSelectSet = false;
      int screenStartX = (int)boxSelectStart.x;
      int screenStartY = (int)boxSelectStart.y;
      int screenEndX = (int)boxSelectEnd.x;
      int screenEndY = (int)boxSelectEnd.y;
      boxSelectStart.zero();
      boxSelectEnd.zero();

      if (screenEndX < screenStartX) {
        int tmp = screenStartX;
        screenStartX = screenEndX;
        screenEndX = tmp;
      }
      if (screenEndY < screenStartY) {
        int tmp = screenStartY;
        screenStartY = screenEndY;
        screenEndY = tmp;
      }

      float[] gameObjectIds = pickingTexture.readMulPixels(
              new Vector2i(screenStartX, screenStartY),
              new Vector2i(screenEndX, screenEndY)
      );
      Set<Integer> uniqueGameObjectIds = new HashSet<>();
      for (float objId : gameObjectIds) {
        uniqueGameObjectIds.add((int)objId);
      }

      for (Integer gameObjectId : uniqueGameObjectIds) {
        GameObject pickedObj = Window.getCurrentScene().getGameObject(gameObjectId);
        if (pickedObj != null && pickedObj.getComponent(NonActiveGameObjectClass.class) == null) {
          Window.getImGuiLayer().getWindowProperties().addActiveGameObject(pickedObj);
        }
      }
    }
  }

  private boolean blockInSquare(float x, float y) {
    WindowProperties propertiesWindow = Window.getImGuiLayer().getWindowProperties();
    Vector2f start = new Vector2f(x, y);
    Vector2f end = new Vector2f(start).add(new Vector2f(Settings.Grid_Width, Settings.Grid_Height));
    Vector2f startScreenf = MouseListener.worldToScreen(start);
    Vector2f endScreenf = MouseListener.worldToScreen(end);
    Vector2i startScreen = new Vector2i((int)startScreenf.x + 2, (int)startScreenf.y + 2);
    Vector2i endScreen = new Vector2i((int)endScreenf.x - 2, (int)endScreenf.y - 2);
    float[] gameObjectIds = propertiesWindow.getPickingTexture().readMulPixels(startScreen, endScreen);

    for (int i = 0; i < gameObjectIds.length; i++) {
      if (gameObjectIds[i] >= 0) {
        GameObject pickedObj = Window.getCurrentScene().getGameObject((int)gameObjectIds[i]);
        if (pickedObj.getComponent(NonActiveGameObjectClass.class) == null) {
          return true;
        }
      }
    }

    return false;
  }
}
