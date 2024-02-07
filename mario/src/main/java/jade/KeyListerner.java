package jade;

import static org.lwjgl.glfw.GLFW.*;

public class KeyListerner {
  private static KeyListerner instance;
  private boolean keyPressed[] = new boolean[350];

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
    } else if (action == GLFW_RELEASE) {
      get().keyPressed[key] = false;
    }
  }

  public static boolean isKeyPressed(int keyCode) {
    return get().keyPressed[keyCode];
  }
}
