package jade;

import components.SpriteRender;
import org.joml.Vector2d;
import org.joml.Vector4d;
import util.AssetPool;

public class LevelEditorScene extends Scene{


  public LevelEditorScene() {

  }

  @Override
  public void init() {
    this.camera = new Camera(new Vector2d(-250, 0));

    GameObject obj1 = new GameObject("Object 1", new Transform(new Vector2d(100, 100), new Vector2d(256, 256)));
    obj1.addComponent(new SpriteRender(AssetPool.getTexture("assets/images/pixelMario.png")));
    this.addGameToScene(obj1);

    GameObject obj2 = new GameObject("Second Object", new Transform(new Vector2d(400, 400), new Vector2d(256, 256)));
    obj2.addComponent(new SpriteRender(AssetPool.getTexture("assets/images/testImage.jpg")));
    this.addGameToScene(obj2);

    //loadResources();
  }

  private void loadResources() {
    AssetPool.getShader("assets/shaders/default.glsl");
  }

  @Override
  public void update(double dt) {
    // camera is inherited from the Scene class.
    //System.out.println(Time.getTime());
    //camera.position.x -= dt * 50; // The Object is not moving, instead we are move camera in opposite direction.
    //camera.position.y -= dt * 20;

    // Note this is gameObjects the Scene List that contains all the Game Objects.
    for (GameObject go : this.gameObjects) {
      go.update(dt);
    }

    this.theRender.render();


  }

}
