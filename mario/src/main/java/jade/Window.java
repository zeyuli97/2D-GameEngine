package jade;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
  private int width;
  private int height;
 private String title;
 private static Window window = null;
 private long glfwWindow; // This number is the memory address of the window.

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

    // For being proper, we need to free the memory.
    glfwFreeCallbacks(glfwWindow);
    glfwDestroyWindow(glfwWindow);

    // Terminate GLFW and free the error call back
    glfwTerminate();
    glfwSetErrorCallback(null).free();
  }

  public void init() {
    // Setup error call back which is where the error should show.
    GLFWErrorCallback.createPrint(System.err).set(); // Tell GLFW show error in System.err

    // Initialize GLFW by glfwInit() function.
    // If init successfully, it wil return ture.
    if (!glfwInit()) {
      throw new IllegalStateException("Unable to initialize GLFW.");
    }

    // Configure GLFW
    glfwDefaultWindowHints();
    // We first set GLFW_VISIBLE false
    // so that user will not notice the modification of window size etc.
    // Once the window is set, we will reshow the window.
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
    glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

    // Create the window. The function return the window memory address in long.
    // The NULL here is a special keep of NULL.
    glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
    if (glfwWindow == NULL) {
      throw new IllegalStateException("The glfw window creation failed.");
    }

    // Make the OpenGL context current
    glfwMakeContextCurrent(glfwWindow);
    // Enable v-sync
    glfwSwapInterval(1);

    // Make the window visible
    glfwShowWindow(glfwWindow);

    // This line is critical for LWJGL's interoperation with GLFW's
    // OpenGL context, or any context that is managed externally.
    // LWJGL detects the context that is current in the current thread,
    // creates the GLCapabilities instance and makes the OpenGL
    // bindings available for use.
    GL.createCapabilities();
  }

  public void loop() {
    while (!glfwWindowShouldClose(glfwWindow)) {
      // Poll events
      glfwPollEvents();

      glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
      glClear(GL_COLOR_BUFFER_BIT);


      glfwSwapBuffers(glfwWindow);
    }

  }







}
