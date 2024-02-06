package jade;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener {
  private static MouseListener instance = null; // singular of MouseListener.
  private double scrollX;
  private double scrollY;
  private double xPos, yPos, lastX, lastY;
  private boolean mouseButtonPressed[] = new boolean[3]; // Default value is false.
  private boolean isDragging;

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
    get().lastX = get().xPos;
    get().lastY = get().yPos;
    get().xPos = xPos;
    get().yPos = yPos;
  }

  public static void mouseButtonCallback(long window, int button, int action, int mods) {
    if (action == GLFW_PRESS) {
      if (button < get().mouseButtonPressed.length) {
        get().mouseButtonPressed[button] = true;
      }
    } else if (action == GLFW_RELEASE) {
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
  }

}
