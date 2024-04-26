package jade;

import components.Sprite;
import components.SpriteRender;
import components.SpriteSheet;
import imgui.ImGui;
import org.joml.Vector2d;
import org.joml.Vector4d;
import org.joml.Vector4f;
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

    obj1 = new GameObject("Object 1", new Transform(new Vector2d(200, 100), new Vector2d(256, 256)), 2);
    obj1.addComponent(new SpriteRender( new Sprite(
            AssetPool.getTexture("assets/images/green.png")
    )));
    //obj1.addComponent(new SpriteRender(spriteSheet.getSprite(0)));
    this.addGameToScene(obj1);

    GameObject obj2 = new GameObject("Second Object", new Transform(new Vector2d(400, 100), new Vector2d(256, 256)), 2);
    obj2.addComponent(new SpriteRender(new Vector4f(1,0,0,1)
    ));
    this.addGameToScene(obj2);
    this.activeGameObject = obj2;

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


    //obj1.getTransform().setPositionX(obj1.getTransform().getPosition().x + 10 * dt);

    // Note this is gameObjects the Scene List that contains all the Game Objects.
    for (GameObject go : this.gameObjects) {
      go.update(dt);
    }

    this.theRender.render();
  }

  @Override
  public void imgui() {
    ImGui.begin("Some random text test.");
    ImGui.text("Here is the random text");
    ImGui.end();
  }

}
