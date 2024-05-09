package jade;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.*;

public class KeyListerner {
  private static KeyListerner instance;
  private boolean[] keyPressed = new boolean[GLFW_KEY_LAST + 1]; // default is false.
  private boolean[] keyFirstPressed = new boolean[GLFW_KEY_LAST + 1];

  private KeyListerner() {

  }

  public static KeyListerner get() {
    if (instance == null) {
      instance = new KeyListerner();
    }
    return instance;
  }

  public static void keyCallback(long window, int key, int scancode, int action, int mods) {
    if (key <= GLFW_KEY_LAST && key >= 0) {
      if (action == GLFW_PRESS) {
        get().keyPressed[key] = true;
        get().keyFirstPressed[key] = true;
      } else if (action == GLFW_RELEASE) {
        get().keyPressed[key] = false;
        get().keyFirstPressed[key] = false;
      }
    }
  }

  public static boolean isKeyPressed(int keyCode) {
    if (keyCode <= GLFW_KEY_LAST && keyCode >= 0) {
      return get().keyPressed[keyCode];
    }
    return false;
  }

  public static boolean isKeyFirstPressed(int keyCode) {
    if (keyCode <= GLFW_KEY_LAST && keyCode >= 0) {
      return get().keyFirstPressed[keyCode];
    }
    return false;
  }

  public static void endFrame() {
    Arrays.fill(get().keyFirstPressed, false);
  }
}
