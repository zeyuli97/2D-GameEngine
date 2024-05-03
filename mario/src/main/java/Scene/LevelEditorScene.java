package Scene;

import components.*;
import imgui.ImGui;
import imgui.ImVec2;
import jade.Camera;
import jade.GameObject;
import jade.Prefabs;
import jade.Transform;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import renders.DebugDraw;
import util.AssetPool;

public class LevelEditorScene extends Scene {

  private GameObject obj1;
  private SpriteSheet sprites;
  private SpriteRender obj1Sprite;
  private SpriteRender obj2Sprite;
  private MouseControl mouseControl = new MouseControl();
  private GameObject levelEditorStuff = new GameObject("LevelEditor", new Transform(new Vector2f()), 0);

  public LevelEditorScene() {

  }

  @Override
  public void init() {
    levelEditorStuff.addComponent(new MouseControl());
    levelEditorStuff.addComponent(new GridLines());

    loadResources();
    this.camera = new Camera(new Vector2f(-250, 0));
    sprites = AssetPool.getSpriteSheet("assets/images/spritesheets/decorationsAndBlocks.png");

    if (levelLoaded) {
      if (gameObjects.size() > 0) {
        this.activeGameObject = gameObjects.get(0);
      }
    }


    //this.activeGameObject = obj1;

//    System.out.println("first time there is no active go.");
//    obj1 = new GameObject("Object 1", new Transform(new Vector2f(200, 100), new Vector2f(256, 256)), 2);
//    obj1Sprite = new SpriteRender();
//    obj1Sprite.setColor(new Vector4f(1, 0, 0, 1));
//    obj1.addComponent(obj1Sprite);
//    obj1.addComponent(new RigidBody());
//    this.addGameObjectToScene(obj1);
//
//    GameObject obj2 = new GameObject("Object 2",
//            new Transform(new Vector2f(400, 100), new Vector2f(256, 256)), 3);
//    SpriteRender obj2SpriteRenderer = new SpriteRender();
//    Sprite obj2Sprite = new Sprite();
//    obj2Sprite.setTexture(AssetPool.getTexture("assets/images/green.png"));
//    obj2SpriteRenderer.setSprite(obj2Sprite);
//    obj2.addComponent(obj2SpriteRenderer);
//    this.addGameObjectToScene(obj2);
//    DebugDraw.addLine2D(new Vector2f(0,0), new Vector2f(800, 800), new Vector3f(1, 0, 0), 1000);
//
//    this.activeGameObject = obj1;
  }

  private void loadResources() {
    AssetPool.getShader("assets/shaders/default.glsl");
    AssetPool.getTexture("assets/images/green.png");
    AssetPool.addSpriteSheet("assets/images/spritesheets/decorationsAndBlocks.png",
            new SpriteSheet(AssetPool.getTexture("assets/images/spritesheets/decorationsAndBlocks.png"),
                    16, 16, 81, 0));


    for (GameObject go : gameObjects) {
      if (go.getComponent(SpriteRender.class) != null) {
        SpriteRender sr = go.getComponent(SpriteRender.class);
        if (sr.getTexture() != null) {
          sr.setTexture(AssetPool.getTexture(sr.getTexture().getFilePath()));
        }
      }
    }
  }


  @Override
  public void update(double dt) {

    levelEditorStuff.update(dt);
    //DebugDraw.addBox2D(new Vector2f(200,200), new Vector2f(64, 32), new Vector3f(0,1,0), 1, 30);
    //DebugDraw.addCircle(new Vector2f(400,400), 80, new Vector3f(1, 0, 0), 1);
    // Note this is gameObjects the Scene List that contains all the Game Objects.
    for (GameObject go : this.gameObjects) {
      go.update(dt);
    }
  }

  @Override
  public void render() {
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
      float spriteWidth = sprite.getWidth() * 2; // The ratio does not align with the older version
      float spriteHeight = sprite.getHeight() * 2;
      int id = sprite.getTexture().getTextID();
      Vector2f[] texCoords = sprite.getTextCoords();

      ImGui.pushID(i);
      if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
        GameObject genSprite = Prefabs.generateSpriteWithinGameObject(sprite, spriteWidth, spriteHeight);
        // Attach this to the cursor.
        levelEditorStuff.getComponent(MouseControl.class).pickupObject(genSprite);
      }
      ImGui.popID();

      ImVec2 lastButtonPos = new ImVec2();
      ImGui.getItemRectMax(lastButtonPos);
      float lastButtonX2 = lastButtonPos.x;
      float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
      if (i + 1 < sprites.size() && (nextButtonX2 + 8) < windowX2) {
        ImGui.sameLine();
      }
    }

    ImGui.end();
  }

}
