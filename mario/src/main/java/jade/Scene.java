package jade;

import org.joml.Vector2d;
import renders.Render;

import java.util.ArrayList;
import java.util.List;

public abstract class Scene {

  protected Render theRender = new Render();

  protected Camera camera = new Camera(new Vector2d());

  private boolean isRunning = false;

  protected List<GameObject> gameObjects = new ArrayList<>();

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
}
