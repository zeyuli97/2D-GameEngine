package Scene;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.Component;
import components.ComponentDeserializer;
import components.Transform;
import jade.Camera;
import jade.GameObject;
import jade.GameObjectDeserializer;
import org.joml.Vector2f;
import physics2d.Physics2D;
import renders.Render;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Scene {

  private Render theRender;
  private Camera camera;
  private boolean isRunning;
  private List<GameObject> gameObjects;
  private SceneInitializer sceneInitializer;
  private Physics2D physics2D;

  public Scene(SceneInitializer sceneInitializer) {
    this.sceneInitializer = sceneInitializer;
    this.physics2D = new Physics2D();
    this.isRunning = false;
    this.gameObjects = new ArrayList<>();
    this.theRender = new Render();

  }

  public void init() {
    this.camera = new Camera(new Vector2f(-250, 0));
    this.sceneInitializer.loadResources(this);
    this.sceneInitializer.init(this);
  }

  public void start() {
    for (int i = 0; i < gameObjects.size(); i++) {
      GameObject go = gameObjects.get(i);
      go.start();
      this.theRender.add(go);
      this.physics2D.add(go);
    }
    isRunning = true;
  }

  /**
   * Add GameObject has two cases.
   * 1. If the game is currently running, we add the GameObject and start the GameObject.
   * 2. If not running, we only add the GameObject into the list.
   * */
  public void addGameObjectToScene(GameObject go) {
    if (!isRunning) {
      gameObjects.add(go);
    } else {
      gameObjects.add(go);
      go.start();
      this.theRender.add(go);
      this.physics2D.add(go);
    }
  }

  public GameObject getGameObject(int targetID) {
    Optional<GameObject> result = this.gameObjects.stream()
            .filter(gameObject -> gameObject.getUid() == targetID).findFirst();
    return result.orElse(null);
  }

  public void editorUpdate(double dt) {
    this.camera.adjustProjection();

    for (int i = 0; i < gameObjects.size(); i++) {
      GameObject go = gameObjects.get(i);
      go.editorUpdate(dt);
      if (go.isDead()) {
        gameObjects.remove(i);
        this.theRender.destroyGameObject(go);
        this.physics2D.destroyGameObject(go);
        i--;
      }
    }
  }

  public void update(double dt) {
    this.camera.adjustProjection();
    this.physics2D.update(dt);

    for (int i = 0; i < gameObjects.size(); i++) {
      GameObject go = gameObjects.get(i);
      if (go.isDead()) {
        gameObjects.remove(i);
        this.theRender.destroyGameObject(go);
        this.physics2D.destroyGameObject(go);
        i--;
      } else {
        go.update(dt);
      }
    }
  }

  public void destroy() {
    for (int i = 0; i < gameObjects.size(); i++) {
      GameObject go = gameObjects.get(i);
      go.destroy();
    }
  }

  public void render() {
    this.theRender.render();
  };

  public Camera getCamera() {
    return camera;
  }

  public void imgui() {
    this.sceneInitializer.imgui();
  }

  public GameObject createGameObject(String name) {
    GameObject go = new GameObject(name);
    go.addComponent(new Transform());
    //go.transform = go.getComponent(Transform.class);
    go.setTransform(go.getComponent(Transform.class));
    return go;
  }



  public void save() {

    Gson gson = new GsonBuilder().setPrettyPrinting()
            .registerTypeAdapter(Component.class, new ComponentDeserializer())
            .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
            .create();

    try {
      FileWriter writer = new FileWriter("level.txt");
      List<GameObject> objsToSerialize = new ArrayList<>();
      for (GameObject go : gameObjects) {
        if (go.isSERIALIZATION()) {
          objsToSerialize.add(go);
        }
      }
      writer.write(gson.toJson(objsToSerialize));
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
      int maxGameObjectsId = -1;
      int maxComponentId = -1;
      GameObject[] gameObjects = gson.fromJson(inFile, GameObject[].class);
      for (int i = 0; i < gameObjects.length; i++) {
        addGameObjectToScene(gameObjects[i]);
        for (Component component : gameObjects[i].getComponents()) {
          if (component.getUid() > maxComponentId) {
            maxComponentId = component.getUid();
          }
        }
        if (gameObjects[i].getUid() > maxGameObjectsId) {
          maxGameObjectsId = gameObjects[i].getUid();
        }
      }

      maxComponentId++;
      maxGameObjectsId++;
      GameObject.init(maxGameObjectsId);
      Component.init(maxComponentId);
    }
  }


  public List<GameObject> getGameObjects() {
    return gameObjects;
  }
}
