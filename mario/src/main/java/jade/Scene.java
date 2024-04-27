package jade;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import imgui.ImGui;
import org.joml.Vector2d;
import renders.Render;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public abstract class Scene {

  protected Render theRender = new Render();

  protected Camera camera = new Camera(new Vector2d());

  private boolean isRunning = false;

  protected List<GameObject> gameObjects = new ArrayList<>();

  // activeGameObject is the object what is chosen will be modified.
  protected GameObject activeGameObject = null;
  protected boolean levelLoaded = false;

  public Scene() {

  }

  public void init() {
  }

  public void start() {
    for (GameObject go : gameObjects) {
      go.start();
      this.theRender.add(go);
    }
    isRunning = true;
  }

  /**
   * Add GameObject has two cases.
   * 1. If the game is currently running, we add the GameObject and start the GameObject.
   * 2. If not running, we only add the GameObject into the list.
   * */
  public void addGameToScene(GameObject go) {
    if (!isRunning) {
      gameObjects.add(go);
    } else {
      gameObjects.add(go);
      go.start();
      this.theRender.add(go);
    }
  }

  public abstract void update(double dt);


  public Camera getCamera() {
    return camera;
  }

  public void sceneImgui() {
    if (activeGameObject != null) {
      ImGui.begin("Inspector");
      activeGameObject.imgui();
      ImGui.end();
    }

    imgui();
  }

  public void imgui() {

  }

  public void saveExit() {
    Gson gson = new GsonBuilder().setPrettyPrinting()
            .registerTypeAdapter(Component.class, new ComponentDeserializer())
            .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
            .create();

    try {
      FileWriter writer = new FileWriter("level.txt");
      writer.write(gson.toJson(this.gameObjects));
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void load() {
    Gson gson = new GsonBuilder().setPrettyPrinting()
            .registerTypeAdapter(Component.class, new ComponentDeserializer())
            .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
            .create();
    String inFile = "";
    try {
      inFile = new String(Files.readAllBytes(Paths.get("level.txt")));
    } catch (IOException e) {
      e.printStackTrace();
    }
    if (!inFile.equals("")) {
      GameObject[] gameObjects = gson.fromJson(inFile, GameObject[].class);
      for (GameObject go : gameObjects) {
        addGameToScene(go);

      }
    }
    levelLoaded = true;
  }
}
