package jade;

import org.joml.Vector2d;

public abstract class Scene {

  protected Camera camera = new Camera(new Vector2d());

  public Scene() {

  }

  public void init() {

  }

  public abstract void update(double dt);
}
