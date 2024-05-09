package jade;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4d;
import org.joml.Vector4f;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener {
  private static MouseListener instance = null; // singular of MouseListener.
  private double scrollX;
  private double scrollY;
  private double xPos, yPos, lastX, lastY;
  private boolean[] mouseButtonPressed = new boolean[9]; // Default value is false.
  private boolean isDragging;
  private int mouseButtonDownCounter = 0;
  private Vector2f gameViewportPos = new Vector2f();
  private Vector2f gameViewportSize = new Vector2f();
  private double lastWorldX, lastWorldY;

  private MouseListener() {
    this.scrollX = 0.0;
    this.scrollY = 0.0;
    this.xPos = 0.0;
    this.yPos = 0.0;
    this.lastX = 0.0;
    this.lastY = 0.0;
  }

  public static MouseListener get() {
    if (instance == null) {
      instance = new MouseListener();
    }
    return instance;
  }

  public static void mousePosCallback(long window, double xPos, double yPos) {
    if (!Window.getImGuiLayer().getGameViewWindow().getWantCaptureMouse()) {
      clear();
    }

    if (get().mouseButtonDownCounter > 0) {
      get().isDragging = true;
    }

    get().lastX = get().xPos;
    get().lastY = get().yPos;
    get().lastWorldX = MouseListener.getWorldCoordX();
    get().lastWorldY = MouseListener.getWorldCoordY();
    get().xPos = xPos;
    get().yPos = yPos;
  }

  public static void mouseButtonCallback(long window, int button, int action, int mods) {
    if (action == GLFW_PRESS) {
      get().mouseButtonDownCounter++;
      if (button < get().mouseButtonPressed.length) {
        get().mouseButtonPressed[button] = true;
      }
    } else if (action == GLFW_RELEASE) {
      get().mouseButtonDownCounter--;
      if (button < get().mouseButtonPressed.length) {
        get().mouseButtonPressed[button] = false;
        get().isDragging = false;
      }
    }
  }

  public static void mouseScrollCallback(long window, double xOffset, double yOffset) {
    get().scrollX = xOffset;
    get().scrollY = yOffset;
  }

  public static void endFrame() {
    get().scrollX = 0;
    get().scrollY = 0;
    get().lastX = get().xPos;
    get().lastY = get().yPos;
    get().lastWorldX = MouseListener.getWorldCoordX();
    get().lastWorldY = MouseListener.getWorldCoordY();
  }

  public static void clear() {
    get().scrollX = 0.0;
    get().scrollY = 0.0;
    get().xPos = 0.0;
    get().yPos = 0.0;
    get().lastX = 0.0;
    get().lastY = 0.0;
    get().mouseButtonDownCounter = 0;
    get().isDragging = false;
    Arrays.fill(get().mouseButtonPressed, false);
  }

  public static float getxPos() {
    return (float) get().xPos;
  }

  public static float getyPos() {
    return (float) get().yPos;
  }

  public static float getWorldCoordX() {
    return getWorldCoord().x;
  }

  public static float getWorldCoordY() {
    return getWorldCoord().y;
  }

  public static Vector2f getWorldCoord() {
    float currentX = getxPos() - get().gameViewportPos.x;
    currentX = (2.0f * (currentX / get().gameViewportSize.x)) - 1.0f;
    float currentY = (getyPos() - get().gameViewportPos.y);
    currentY = (2.0f * (1.0f - (currentY / get().gameViewportSize.y))) - 1;

    Camera camera = Window.getCurrentScene().getCamera();
    Vector4f tmp = new Vector4f(currentX, currentY, 0, 1);

    Matrix4f inverseView = new Matrix4f(camera.getInverseViewMatrix());
    Matrix4f inverseProjection = new Matrix4f(camera.getInverseProjectionMatrix());
    tmp.mul(inverseView.mul(inverseProjection));

    return new Vector2f(tmp.x, tmp.y);
  }

  public static double getDx() {
    return (get().lastX - get().xPos);
  }

  public static double getDy() {
    return (get().lastY - get().yPos);
  }

  public static float getWorldDeltaX() {
    return (float) (get().lastWorldX - MouseListener.getWorldCoordX());
  }

  public static float getWorldDeltaY() {
    return (float) (get().lastWorldY - MouseListener.getWorldCoordY());
  }

  public static double getScrollX() {
    return get().scrollX;
  }

  public static double getScrollY() {
    return get().scrollY;
  }

  public static boolean getIsDragging() {
    return get().isDragging;
  }

  public static boolean mouseButtonDown(int button) {
    if (button < get().mouseButtonPressed.length) {
      return get().mouseButtonPressed[button];
    } else {
      return false;
    }
  }

  public static Vector2f getScreen() {
    return new Vector2f(getScreenX(), getScreenY());
  }

  public static float getScreenX() {
    float currentX = getxPos() - get().gameViewportPos.x;
    currentX = (currentX / get().gameViewportSize.x) * 3456.f;

    return currentX;
  }

  public static float getScreenY() {
    float currentY = getyPos() - get().gameViewportPos.y;
    currentY = 2234 - ((currentY / get().gameViewportSize.y) * 2234);

    return currentY;
  }


  public static void setGameViewportPos(Vector2f gameViewportPos) {
    get().gameViewportPos.set(gameViewportPos);
  }

  public static void setGameViewportSize(Vector2f gameViewportSize) {
    get().gameViewportSize.set(gameViewportSize);
  }

  public static Vector2f screenToWorld(Vector2f screenCoords) {
    Vector2f normalizedScreenCords = new Vector2f(
            screenCoords.x / Window.getWidth(),
            screenCoords.y / Window.getHeight()
    );
    normalizedScreenCords.mul(2.0f).sub(new Vector2f(1.0f, 1.0f));
    Camera camera = Window.getCurrentScene().getCamera();
    Vector4f tmp = new Vector4f(normalizedScreenCords.x, normalizedScreenCords.y,
            0, 1);
    Matrix4f inverseView = new Matrix4f(camera.getInverseViewMatrix());
    Matrix4f inverseProjection = new Matrix4f(camera.getInverseProjectionMatrix());
    tmp.mul(inverseView.mul(inverseProjection));
    return new Vector2f(tmp.x, tmp.y);
  }

  public static Vector2f worldToScreen(Vector2f worldCoords) {
    Camera camera = Window.getCurrentScene().getCamera();
    Vector4f ndcSpacePos = new Vector4f(worldCoords.x, worldCoords.y, 0, 1);
    Matrix4f view = new Matrix4f(camera.getViewMatrix());
    Matrix4f projection = new Matrix4f(camera.getProjectionMatrix());
    ndcSpacePos.mul(projection.mul(view));
    Vector2f windowSpace = new Vector2f(ndcSpacePos.x, ndcSpacePos.y).mul(1.0f / ndcSpacePos.w);
    windowSpace.add(new Vector2f(1.0f, 1.0f)).mul(0.5f);
    windowSpace.mul(new Vector2f(Window.getWidth(), Window.getHeight()));

    return windowSpace;
  }

}
