package jade;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.openal.SOFTDeferredUpdates;
import org.lwjgl.opengl.GL;
import util.Time;

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
  public float r, g , b, a;
  private boolean fadeToBlack = false;

  private static Scene currentScene;


 /**
  * Private constructor so that we can make sure there is only one Window exists.
  * The only existing window instance is existing under the Window.window.
  * */
  private Window() {
    this.width = 1920;
    this.height = 1080;
    this.title = "Mario";
    r = 1;
    b = 1;
    g = 1;
    a = 1;
  }

  public static void changeScene(int newScene) {
    switch (newScene) {
      case 0:
        currentScene = new LevelEditorScene();
        currentScene.init();
        currentScene.start();
        break;
      case 1:
        currentScene = new LevelScene();
        currentScene.init();
        currentScene.start();
        break;
      default:
        assert false : "Unknown scene '" + newScene + "'";
        break;
    }
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

  public static Scene getCurrentScene() {
    return currentScene;
  }

  public void run() {
    System.out.println("Hello LWJGL" + Version.getVersion() + "!");

    this.init();
    this.loop();

    // For being proper, we need to free the memory.
    glfwFreeCallbacks(glfwWindow);
    glfwDestroyWindow(glfwWindow);

    // Terminate GLFW and free the error call back
    glfwTerminate();
    glfwSetErrorCallback(null).free();
  }

  public void init() {
    // Setup error call back, which is where the error should show.
    GLFWErrorCallback.createPrint(System.err).set(); // Tell GLFW show error in System.err

    // Initialize GLFW by glfwInit() function.
    // If init successfully, it will return true.
    if (!glfwInit()) {
      throw new IllegalStateException("Unable to initialize GLFW.");
    }

    // Configure GLFW
    glfwDefaultWindowHints(); // Set to Default keep a clean state.
    // We first set GLFW_VISIBLE false
    // so that the user will not notice the modification of window size etc.
    // Once the window is set, we will reshow the window.
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
    glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

    // Some new window hints needed so that shader could run.
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
    //glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

    // Create the window.
    // The function returns the window memory address in long.
    // The NULL here is a special keep of NULL.
    glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
    if (glfwWindow == NULL) {
      throw new IllegalStateException("The glfw window creation failed.");
    }

    glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback); // reaction for mouse movement
    glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback); // reaction for mouse button click
    glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback); // reaction for scroll the mouse.
    glfwSetKeyCallback(glfwWindow, KeyListerner::keyCallback); // reaction when keyboard is pressed.

    // Make the OpenGL context current
    glfwMakeContextCurrent(glfwWindow);
    // Enable v-sync
    glfwSwapInterval(1); // 0 will disable. V sync makes rendering more smooth.

    // Make the window visible
    glfwShowWindow(glfwWindow);

    // This line is critical for LWJGL's interoperation with GLFW's
    // OpenGL context, or any context that is managed externally.
    // LWJGL detects the context that is current in the current thread,
    // creates the GLCapabilities instance and makes the OpenGL
    // bindings available for use.
    GL.createCapabilities();

    Window.changeScene(0);
  }

  public void loop() {
    double beginTime = Time.getTime();
    double endTime;

    double dt = -1.0;

    while (!glfwWindowShouldClose(glfwWindow)) {
      // Poll events that we setup in the init() -- all the callback functions.
      glfwPollEvents();

      glClearColor(r, g, b, a); // Set clear color to a tone defined by rgba.
      // glClear() called at the beginning of each iteration of the rendering loop to clear the color buffer.
      // This ensures that the framebuffer starts with a clean slate before rendering new content for the current frame.
      glClear(GL_COLOR_BUFFER_BIT); // Use the clear color to fill the color buffer


      if (dt >= 0) {
        currentScene.update(dt);
      }

      // Now we perform color buffer swap.
      glfwSwapBuffers(glfwWindow);


      // The following function will give us the time cost for one iteration.
      endTime = Time.getTime();
      dt = endTime - beginTime;
      beginTime = endTime;
    }

  }




}
