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
  private boolean mouseButtonPressed[] = new boolean[9]; // Default value is false.
  private boolean isDragging;
  private int mouseButtonDownCounter = 0;
  private Vector2f gameViewportPos = new Vector2f();
  private Vector2f gameViewportSize = new Vector2f();
  private double worldX, worldY, lastWorldX, lastWorldY;

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
    if (get().mouseButtonDownCounter > 0) {
      get().isDragging = true;
    }
    get().lastX = get().xPos;
    get().lastY = get().yPos;
    get().lastWorldX = get().worldX;
    get().lastWorldY = get().worldY;
    get().xPos = xPos;
    get().yPos = yPos;
    calOrthoX();
    calOrthoY();
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
    get().lastWorldX = get().worldX;
    get().lastWorldY = get().worldY;
  }

  public static float getxPos() {
    return (float) get().xPos;
  }

  public static float getyPos() {
    return (float) get().yPos;
  }

  private static void calOrthoX() {
    float currentX = getxPos() - get().gameViewportPos.x;
    currentX = (currentX / get().gameViewportSize.x) * 2.0f - 1.0f;
    Vector4f tmp = new Vector4f(currentX, 0, 0, 1);

    Camera camera = Window.getCurrentScene().getCamera();
    Matrix4f viewProjection = new Matrix4f();
    camera.getInverseViewMatrix().mul(camera.getInverseProjectionMatrix(), viewProjection);
    tmp.mul(viewProjection);

    get().worldX = tmp.x;
  }

  public static float getOrthoX() {
    return (float) get().worldX;
  }

  public static float getOrthoY() {
    return (float) get().worldY;
  }

  private static void calOrthoY() {
    float currentY = getyPos() - get().gameViewportPos.y;
    currentY = -((currentY / get().gameViewportSize.y) * 2.0f - 1.0f);
    Vector4f tmp = new Vector4f(0, currentY, 0, 1);

    Camera camera = Window.getCurrentScene().getCamera();
    Matrix4f viewProjection = new Matrix4f();
    camera.getInverseViewMatrix().mul(camera.getInverseProjectionMatrix(), viewProjection);
    tmp.mul(viewProjection);

    get().worldY = tmp.y;;
  }

  public static double getDx() {
    return (get().lastX - get().xPos);
  }

  public static double getDy() {
    return (get().lastY - get().yPos);
  }

  public static float getWorldDx() {
    return (float) (get().lastWorldX - get().worldX);
  }

  public static float getWorldDy() {
    return (float) (get().lastWorldY - get().worldY);
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

  public static void setGameViewportPos(Vector2f gameViewportPos) {
    get().gameViewportPos.set(gameViewportPos);
  }

  public static void setGameViewportSize(Vector2f gameViewportSize) {
    get().gameViewportSize.set(gameViewportSize);
  }
}
