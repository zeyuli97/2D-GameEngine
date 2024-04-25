package jade;

import components.Sprite;
import components.SpriteRender;
import components.SpriteSheet;
import org.joml.Vector2d;
import org.joml.Vector4d;
import renders.Texture;
import util.AssetPool;

public class LevelEditorScene extends Scene{

  private GameObject obj1;
  private SpriteSheet spriteSheet;

  public LevelEditorScene() {

  }

  @Override
  public void init() {
    loadResources();
    this.camera = new Camera(new Vector2d(-250, 0));

    spriteSheet = AssetPool.getSpriteSheet("assets/images/spritesheet.png");

    obj1 = new GameObject("Object 1", new Transform(new Vector2d(100, 100), new Vector2d(100, 100)));
    obj1.addComponent(new SpriteRender(spriteSheet.getSprite(10)));
    //obj1.addComponent(new SpriteRender(spriteSheet.getSprite(0)));
    this.addGameToScene(obj1);

    GameObject obj2 = new GameObject("Second Object", new Transform(new Vector2d(400, 400), new Vector2d(256, 256)));
    obj2.addComponent(new SpriteRender(spriteSheet.getSprite(10)));
    this.addGameToScene(obj2);

  }

  private void loadResources() {
    AssetPool.getShader("assets/shaders/default.glsl");
    AssetPool.addSpriteSheet("assets/images/spritesheet.png", new SpriteSheet(
            new Texture("assets/images/spritesheet.png"), 16, 16, 26, 0));
  }


  private int spirteIndex = 0;
  private double spriteFlipTime = 0.4;
  private double spriteFlipTimeLeft = 0;

  @Override
  public void update(double dt) {
    spriteFlipTimeLeft -= dt;
    if (spriteFlipTimeLeft <= 0) {
      spriteFlipTimeLeft = spriteFlipTime;
      spirteIndex++;
      if (spirteIndex > 4) {
        spirteIndex = 0;
      }
      obj1.getComponent(SpriteRender.class).setSprite(spriteSheet.getSprite(spirteIndex));
    }


    obj1.getTransform().setPositionX(obj1.getTransform().getPosition().x + 10 * dt);

    // Note this is gameObjects the Scene List that contains all the Game Objects.
    for (GameObject go : this.gameObjects) {
      go.update(dt);
    }

    this.theRender.render();


  }

}
