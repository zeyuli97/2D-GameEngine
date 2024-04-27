package jade;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.Sprite;
import components.SpriteRender;
import components.SpriteSheet;
import imgui.ImGui;
import org.joml.Vector2d;
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

    if (levelLoaded) {
      return;
    }

    spriteSheet = AssetPool.getSpriteSheet("assets/images/spritesheet.png");

    obj1 = new GameObject("Object 1", new Transform(new Vector2d(200, 100), new Vector2d(256, 256)), 2);
    SpriteRender spriteRender1 = new SpriteRender();
    Sprite sprite1 = new Sprite();
    sprite1.setTexture(AssetPool.getTexture("assets/images/green.png"));

    spriteRender1.setSprite(sprite1);
    obj1.addComponent(spriteRender1);


    //obj1.addComponent(new SpriteRender(spriteSheet.getSprite(0)));
    this.addGameToScene(obj1);

    GameObject obj2 = new GameObject("Second Object", new Transform(new Vector2d(400, 100), new Vector2d(256, 256)), 2);

    SpriteRender spriteRender2 = new SpriteRender();
    Sprite sprite2 = new Sprite();
    spriteRender2.setColor(new Vector4f(1,0,0,1));
    obj2.addComponent(spriteRender2);
    this.addGameToScene(obj2);
    this.activeGameObject = obj2;

    Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(Component.class, new ComponentDeserializer()).create();
    String serialize = gson.toJson(obj1);
    System.out.println(serialize);
    GameObject obj3 = gson.fromJson(serialize, GameObject.class);
    System.out.println(obj3);
  }

  private void loadResources() {
    Texture texture = new Texture();
    AssetPool.getShader("assets/shaders/default.glsl");
    AssetPool.addSpriteSheet("assets/images/spritesheet.png", new SpriteSheet(
            AssetPool.getTexture("assets/images/spritesheet.png"), 16, 16, 26, 0));
  }


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
