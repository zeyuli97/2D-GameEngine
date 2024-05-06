package jade;

import Observers.EventSystem;
import Observers.Events.Event;
import Observers.Events.EventType;
import Observers.Observer;
import Scene.*;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import renders.*;
import util.AssetPool;
import util.Time;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window implements Observer {
  private int width;
  private int height;
  private String title;
  private static Window window = null;
  private long glfwWindow; // This number is the memory address of the window.
  public float r, g , b, a;
  private static ImGuiLayer imGuiLayer;
  private static Scene currentScene;
  private FrameBuffer frameBuffer;
  private PickingTexture pickingTexture;
  private boolean runTimePlaying = false; // Whether we are currently at runtime or just editing the scene.



 /**
  * Private constructor so that we can make sure there is only one Window exists.
  * The only existing window instance is existing under the Window.window.
  * */
  private Window() {
    this.width = 1920;
    this.height = 1080;
    this.title = "Jade";
    this.r = 1;
    this.g = 1;
    this.b = 1;
    this.a = 1;
    EventSystem.addObserver(this);
  }

  public static void changeScene(SceneInitializer sceneInitializer) {
    if (currentScene != null) {
      currentScene.destroy();
    }

    imGuiLayer.getWindowProperties().setActiveGameObject(null);

    currentScene = new Scene(sceneInitializer);
    System.out.println("I am about load current scene");
    currentScene.load();
    System.out.println("I am about init current scene");
    currentScene.init();
    System.out.println("I am about start current scene");
    currentScene.start();

    System.out.println(currentScene.getGameObjects().size());
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
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
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

    glfwSetWindowSizeCallback(glfwWindow, (w, newWidth, newHeight) -> {
      Window.setWidth(newWidth);
      Window.setHeight(newHeight);
    });

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

    glEnable(GL_BLEND);
    glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

    this.frameBuffer = new FrameBuffer(3456, 2234);
    // We are mimic the above frame buffer and replace color info with component's uid.
    this.pickingTexture = new PickingTexture(3456, 2234);
    glViewport(0,0,3456, 2234);

    imGuiLayer = new ImGuiLayer(glfwWindow, pickingTexture);
    imGuiLayer.initImGui();

    Window.changeScene(new LevelEditorSceneInitializer());
  }

  public void loop() {
    double beginTime = Time.getTime();
    double endTime;

    double dt = -1.0;

    Shader defaultShader = AssetPool.getShader("assets/shaders/default.glsl");
    Shader pickingShader = AssetPool.getShader("assets/shaders/pickingShader.glsl");


    while (!glfwWindowShouldClose(glfwWindow)) {
      // Poll events that we setup in the init() -- all the callback functions.
      glfwPollEvents();

      // Render path 1. Render to picking texture.
      glDisable(GL_BLEND); // We do not want blend, we just want pure pixel data.
      pickingTexture.enableWriting();
      glViewport(0, 0, 3456, 2234);
      glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

      Render.bindShader(pickingShader);
      currentScene.render();


      pickingTexture.disableWriting();
      glEnable(GL_BLEND);

      // Render path 2. Render actual game engine related.
      DebugDraw.beginFrame();
      Render.bindShader(defaultShader);

      this.frameBuffer.bind();

      glClearColor(r, g, b, a); // Set clear color to a tone defined by rgba.
      // glClear() called at the beginning of each iteration of the rendering loop to clear the color buffer.
      // This ensures that the framebuffer starts with a clean slate before rendering new content for the current frame.
      glClear(GL_COLOR_BUFFER_BIT); // Use the clear color to fill the color buffer


      if (dt >= 0) {
        DebugDraw.draw();
        //currentScene.update(dt);
        if (runTimePlaying) {
          currentScene.update(dt);
        } else {
          currentScene.editorUpdate(dt);
        }
        currentScene.render();
      }

      this.frameBuffer.unbind();

      imGuiLayer.update((float) dt, currentScene);
      // Now we perform color buffer swap.
      glfwSwapBuffers(glfwWindow);

      MouseListener.endFrame();

      // The following function will give us the time cost for one iteration.
      endTime = Time.getTime();
      dt = endTime - beginTime;
      beginTime = endTime;
      //System.out.println(currentScene.getGameObjects().size());
    }

  }

  public static int getHeight() {
    return Window.get().height;
  }

  public static int getWidth() {
    return Window.get().width;
  }

  public static void setWidth(int newWidth) {
    Window.get().width = newWidth;
  }

  public static void setHeight(int newHeight) {
    Window.get().height = newHeight;
  }

  public static FrameBuffer getFrameBuffer() {
    return get().frameBuffer;
  }

  public static float getTargetAspectRatio() {
    return 16f / 9f;
  }

  public static ImGuiLayer getImGuiLayer() {
    return imGuiLayer;
  }

  @Override
  public void onNotify(GameObject gameObject, Event event) {
    switch (event.getType()) {
      case GameEngineStartPlay:
        this.runTimePlaying = true;
        currentScene.save();
        Window.changeScene(new LevelEditorSceneInitializer());
        break;
      case GameEngineStopPlay:
        this.runTimePlaying = false;
        Window.changeScene(new LevelEditorSceneInitializer());
        break;
      case LoadLevel:
        Window.changeScene(new LevelEditorSceneInitializer());
        break;
      case SaveLevel:
        currentScene.save();
        break;
    }
  }
}
