package jade;

import static org.lwjgl.glfw.GLFW.*;

public class KeyListerner {
  private static KeyListerner instance;
  private boolean keyPressed[] = new boolean[350]; // default is false.
  private boolean keyFirstPressed[] = new boolean[350];

  private KeyListerner() {

  }

  public static KeyListerner get() {
    if (instance == null) {
      instance = new KeyListerner();
    }
    return instance;
  }

  public static void keyCallback(long window, int key, int scancode, int action, int mods) {
    if (action == GLFW_PRESS) {
        get().keyPressed[key] = true;
        get().keyFirstPressed[key] = true;
    } else if (action == GLFW_RELEASE) {
      get().keyPressed[key] = false;
      get().keyFirstPressed[key] = false;
    }
  }

  public static boolean isKeyPressed(int keyCode) {
    return get().keyPressed[keyCode];
  }

  public static boolean isKeyFirstPressed(int keyCode) {
    boolean result = get().keyFirstPressed[keyCode];
    if (result) {
      get().keyFirstPressed[keyCode] = false;
    }
    return result;
  }
}
