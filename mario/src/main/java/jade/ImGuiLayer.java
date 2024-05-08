package jade;

import Scene.Scene;
import editor.GameViewWindow;
import editor.MenuBar;
import editor.SceneHierarchyWindow;
import editor.WindowProperties;
import imgui.*;
import imgui.callback.ImStrConsumer;
import imgui.callback.ImStrSupplier;
import imgui.flag.*;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImBoolean;
import renders.PickingTexture;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;


/**
 * The ImGuiLayer follow the Window structure inside imgui-java by SpaiR.
 * */
public class ImGuiLayer {

  private WindowProperties windowProperties;

  private GameViewWindow gameViewWindow;

  private long glfwWindow;
  private SceneHierarchyWindow sceneHierarchyWindow;

  // Mouse cursors provided by GLFW
  //private final long[] mouseCursors = new long[ImGuiMouseCursor.COUNT];

  // LWJGL3 renderer (SHOULD be initialized)
  private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
  private final ImGuiImplGlfw implGlfw = new ImGuiImplGlfw();

  private MenuBar menuBar;

  public ImGuiLayer(long glfwWindow, PickingTexture pickingTexture) {
    this.glfwWindow = glfwWindow;
    this.windowProperties = new WindowProperties(pickingTexture);
    this.menuBar = new MenuBar();
    this.sceneHierarchyWindow = new SceneHierarchyWindow();
    this.gameViewWindow = new GameViewWindow();
  }

  // Initialize Dear ImGui.
  public void initImGui() {
    // IMPORTANT!!
    // This line is critical for Dear ImGui to work.
    ImGui.createContext();

    // ------------------------------------------------------------
    // Initialize ImGuiIO config
    final ImGuiIO io = ImGui.getIO();

    io.setIniFilename("imgui.ini"); // We don't want to save .ini file
    io.addConfigFlags(ImGuiConfigFlags.DockingEnable); // Enable docking.
    //io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);
    //io.addBackendFlags(ImGuiBackendFlags.HasMouseCursors); // Mouse cursors to display while resizing windows etc.
    io.setBackendPlatformName("imgui_java_impl_glfw");
    // ------------------------------------------------------------
    // GLFW callbacks to handle user input

    glfwSetKeyCallback(glfwWindow, (w, key, scancode, action, mods) -> {
      if (action == GLFW_PRESS) {
        io.setKeysDown(key, true);
      } else if (action == GLFW_RELEASE) {
        io.setKeysDown(key, false);
      }

      io.setKeyCtrl(io.getKeysDown(GLFW_KEY_LEFT_CONTROL) || io.getKeysDown(GLFW_KEY_RIGHT_CONTROL));
      io.setKeyShift(io.getKeysDown(GLFW_KEY_LEFT_SHIFT) || io.getKeysDown(GLFW_KEY_RIGHT_SHIFT));
      io.setKeyAlt(io.getKeysDown(GLFW_KEY_LEFT_ALT) || io.getKeysDown(GLFW_KEY_RIGHT_ALT));
      io.setKeySuper(io.getKeysDown(GLFW_KEY_LEFT_SUPER) || io.getKeysDown(GLFW_KEY_RIGHT_SUPER));

      if (!io.getWantCaptureKeyboard()) {
        KeyListerner.keyCallback(w, key, scancode, action, mods);
      }
    });

    glfwSetCharCallback(glfwWindow, (w, c) -> {
      if (c != GLFW_KEY_DELETE) {
        io.addInputCharacter(c);
      }
    });

    glfwSetMouseButtonCallback(glfwWindow, (w, button, action, mods) -> {
      final boolean[] mouseDown = new boolean[5];

      mouseDown[0] = button == GLFW_MOUSE_BUTTON_1 && action != GLFW_RELEASE;
      mouseDown[1] = button == GLFW_MOUSE_BUTTON_2 && action != GLFW_RELEASE;
      mouseDown[2] = button == GLFW_MOUSE_BUTTON_3 && action != GLFW_RELEASE;
      mouseDown[3] = button == GLFW_MOUSE_BUTTON_4 && action != GLFW_RELEASE;
      mouseDown[4] = button == GLFW_MOUSE_BUTTON_5 && action != GLFW_RELEASE;

      io.setMouseDown(mouseDown);

      if (!io.getWantCaptureMouse() && mouseDown[1]) {
        ImGui.setWindowFocus(null);
      }

      if (!io.getWantCaptureMouse() || gameViewWindow.getWantCaptureMouse()) {
        MouseListener.mouseButtonCallback(w, button, action, mods);
      }
    });

    glfwSetScrollCallback(glfwWindow, (w, xOffset, yOffset) -> {
      io.setMouseWheelH(io.getMouseWheelH() + (float) xOffset);
      io.setMouseWheel(io.getMouseWheel() + (float) yOffset);
      if (!io.getWantCaptureMouse() || gameViewWindow.getWantCaptureMouse()) {
        MouseListener.mouseScrollCallback(w, xOffset, yOffset);
      }
    });

    io.setSetClipboardTextFn(new ImStrConsumer() {
      @Override
      public void accept(final String s) {
        glfwSetClipboardString(glfwWindow, s);
      }
    });

    io.setGetClipboardTextFn(new ImStrSupplier() {
      @Override
      public String get() {
        final String clipboardString = glfwGetClipboardString(glfwWindow);
        if (clipboardString != null) {
          return clipboardString;
        } else {
          return "";
        }
      }
    });

    // ------------------------------------------------------------
    // Fonts configuration
    // Read: https://raw.githubusercontent.com/ocornut/imgui/master/docs/FONTS.txt

    final ImFontAtlas fontAtlas = io.getFonts();
    final ImFontConfig fontConfig = new ImFontConfig(); // Natively allocated object, should be explicitly destroyed

    // Glyphs could be added per-font as well as per config used globally like here
    fontConfig.setGlyphRanges(fontAtlas.getGlyphRangesDefault());

    // Fonts merge example
    fontConfig.setPixelSnapH(true);
    fontAtlas.addFontFromFileTTF("assets/fonts/Monaco.ttf", 21, fontConfig);

    fontConfig.destroy(); // After all fonts were added we don't need this config more

    // ------------------------------------------------------------
    // Use freetype instead of stb_truetype to build a fonts texture
    //fontAtlas.setFlags(ImGuiFreeTypeBuilderFlags.LightHinting);
    //fontAtlas.build();

    // Method initializes LWJGL3 renderer.
    // This method SHOULD be called after you've initialized your ImGui configuration (fonts and so on).
    // ImGui context should be created as well.
    imGuiGl3.init("#version 330 core");
    implGlfw.init(glfwWindow, false);
  }

  public void update(float dt, Scene currentScene) {
    startFrame(dt);

    // Any Dear ImGui code SHOULD go between ImGui.newFrame()/ImGui.render() methods
    setupDockSpace();
    currentScene.imgui();
    //ImGui.showDemoWindow();
    gameViewWindow.imgui();
    windowProperties.update(dt, currentScene);
    windowProperties.imgui();
    sceneHierarchyWindow.imgui();

    endFrame();
  }

  private void startFrame(final float deltaTime) {
    implGlfw.newFrame();
    ImGui.newFrame();

  }

  private void endFrame() {
    glBindFramebuffer(GL_FRAMEBUFFER, 0);
    glViewport(0,0,Window.getWidth(), Window.getHeight());
    glClearColor(0,0,0,1);
    glClear(GL_COLOR_BUFFER_BIT);

    // After Dear ImGui prepared a draw data, we use it in the LWJGL3 renderer.
    // At that moment ImGui will be rendered to the current OpenGL context.
    ImGui.render();
    imGuiGl3.renderDrawData(ImGui.getDrawData());

    long backupWindowPtr = glfwGetCurrentContext();
    ImGui.updatePlatformWindows();
    ImGui.renderPlatformWindowsDefault();
    glfwMakeContextCurrent(backupWindowPtr);

  }

  // If you want to clean a room after yourself - do it by yourself
  private void destroyImGui() {
    imGuiGl3.dispose();
    ImGui.destroyContext();
    implGlfw.dispose();
  }

  private void setupDockSpace() {
    int windowFlags = ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoDocking;

    ImGuiViewport mainViewport = ImGui.getMainViewport();
    ImGui.setNextWindowPos(mainViewport.getWorkPosX(), mainViewport.getWorkPosY());
    ImGui.setNextWindowSize(mainViewport.getWorkSizeX(), mainViewport.getWorkSizeY());
    ImGui.setNextWindowViewport(mainViewport.getID());
    ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
    ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);
    windowFlags |= ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse |
            ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove |
            ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus;

    ImGui.begin("Dockspace Demo", new ImBoolean(true), windowFlags);
    ImGui.popStyleVar(2);

    // Dockspace
    ImGui.dockSpace(ImGui.getID("Dockspace"));

    menuBar.imgui();

    ImGui.end();
  }

  public WindowProperties getWindowProperties() {
    return windowProperties;
  }
}
