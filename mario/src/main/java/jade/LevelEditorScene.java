package jade;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.RigidBody;
import components.Sprite;
import components.SpriteRender;
import components.SpriteSheet;
import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector4f;
import renders.Texture;
import util.AssetPool;

public class LevelEditorScene extends Scene{

  private GameObject obj1;
  private SpriteSheet sprites;
  private SpriteRender obj1Sprite;
  private SpriteRender obj2Sprite;

  public LevelEditorScene() {

  }

  @Override
  public void init() {
    loadResources();
    this.camera = new Camera(new Vector2d(-250, 0));
    sprites = AssetPool.getSpriteSheet("assets/images/spritesheets/decorationsAndBlocks.png");
    if (levelLoaded) {
      this.activeGameObject = gameObjects.get(0);
      return;
    }


    obj1 = new GameObject("Object 1", new Transform(new Vector2d(200, 100), new Vector2d(256, 256)), 2);
    obj1Sprite = new SpriteRender();
    obj1Sprite.setColor(new Vector4f(1, 0, 0, 1));
    obj1.addComponent(obj1Sprite);
    obj1.addComponent(new RigidBody());
    this.addGameObjectToScene(obj1);
    this.activeGameObject = obj1;

    GameObject obj2 = new GameObject("Object 2",
            new Transform(new Vector2d(400, 100), new Vector2d(256, 256)), 3);
    SpriteRender obj2SpriteRenderer = new SpriteRender();
    Sprite obj2Sprite = new Sprite();
    obj2Sprite.setTexture(AssetPool.getTexture("assets/images/green.png"));
    obj2SpriteRenderer.setSprite(obj2Sprite);
    obj2.addComponent(obj2SpriteRenderer);
    this.addGameObjectToScene(obj2);

  }

  private void loadResources() {
    AssetPool.getShader("assets/shaders/default.glsl");
    AssetPool.getTexture("assets/images/green.png");
    AssetPool.addSpriteSheet("assets/images/spritesheets/decorationsAndBlocks.png",
            new SpriteSheet(AssetPool.getTexture("assets/images/spritesheets/decorationsAndBlocks.png"),
                    16, 16, 81, 0));
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
    ImGui.begin("Test window");

    ImVec2 windowPos = new ImVec2();
    ImGui.getWindowPos(windowPos);
    ImVec2 windowSize = new ImVec2();
    ImGui.getWindowSize(windowSize);
    ImVec2 itemSpacing = new ImVec2();
    ImGui.getStyle().getItemSpacing(itemSpacing);

    float windowX2 = windowPos.x + windowSize.x;
    for (int i=0; i < sprites.size(); i++) {
      Sprite sprite = sprites.getSprite(i);
      float spriteWidth = sprite.getTexture().getWidth() * 0.2f; // The ratio does not align with the older version
      float spriteHeight = sprite.getTexture().getHeight() * 0.2f;
      int id = sprite.getTexture().getTextID();
      Vector2f[] texCoords = sprite.getTextCoords();

      ImGui.pushID(i);
      if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
        System.out.println("Button " + i + "clicked");
      }
      ImGui.popID();

      ImVec2 lastButtonPos = new ImVec2();
      ImGui.getItemRectMax(lastButtonPos);
      float lastButtonX2 = lastButtonPos.x;
      float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
      if (i + 1 < sprites.size() && (nextButtonX2 + 6) < windowX2) {
        ImGui.sameLine();
      }
    }

    ImGui.end();
  }

}
