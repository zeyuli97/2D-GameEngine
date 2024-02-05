package jade;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;

public class Window {
  private int width;
  private int height;
 private String title;
 private static Window window = null;

 /**
  * Private constructor so that we can make sure there is only one Window exists.
  * The only existing window instance is existing under the Window.window.
  * */
  private Window() {
    this.width = 1920;
    this.height = 1080;
    this.title = "Mario";
  }

  /**
   * This method will create a Window.window if there is no window currently exists.
   * If there is, give called the existed one.
   * */
  public static Window get() {
    if (Window.window == null) {
      Window.window = new Window();
    }
    return Window.window; // This is a class variable
  }

  public void run() {
    System.out.println("Hello LWJGL" + Version.getVersion() + "!");

    init();
    loop();
  }

  public void init() {
    // Setup error call back which is where the error should show.
    GLFWErrorCallback
  }

  public void loop() {
  }







}
