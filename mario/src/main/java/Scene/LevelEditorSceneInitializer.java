package Scene;

import components.*;
import imgui.ImGui;
import imgui.ImVec2;
import jade.*;
import org.joml.Vector2f;
import util.AssetPool;

public class LevelEditorSceneInitializer extends SceneInitializer {

  private SpriteSheet sprites;
  private GameObject levelEditorStuff;

  public LevelEditorSceneInitializer() {

  }

  @Override
  public void init(Scene scene) {
    SpriteSheet gizmos = AssetPool.getSpriteSheet("assets/images/gizmos.png");
    sprites = AssetPool.getSpriteSheet("assets/images/spritesheets/decorationsAndBlocks.png");

    levelEditorStuff = scene.createGameObject("levelEditorStuff");
    levelEditorStuff.setNoSerialize();

    levelEditorStuff.addComponent(new Transform());
    levelEditorStuff.addComponent(new MouseControl());
    levelEditorStuff.addComponent(new GridLines());
    levelEditorStuff.addComponent(new EditorCamera(scene.getCamera()));
    levelEditorStuff.addComponent(new GizmoSystem(gizmos));

    scene.addGameObjectToScene(levelEditorStuff);
  }



  @Override
  public void loadResources(Scene scene) {
    AssetPool.getShader("assets/shaders/default.glsl");
    AssetPool.getTexture("assets/images/green.png");
    AssetPool.addSpriteSheet("assets/images/spritesheets/decorationsAndBlocks.png",
            new SpriteSheet(AssetPool.getTexture("assets/images/spritesheets/decorationsAndBlocks.png"),
                    16, 16, 81, 0));
    AssetPool.addSpriteSheet("assets/images/gizmos.png",
            new SpriteSheet(AssetPool.getTexture("assets/images/gizmos.png"), 24, 48, 3, 0));



    for (GameObject go : scene.getGameObjects()) {
      if (go.getComponent(SpriteRender.class) != null) {
        SpriteRender sr = go.getComponent(SpriteRender.class);
        if (sr.getTexture() != null) {
          sr.setTexture(AssetPool.getTexture(sr.getTexture().getFilePath()));
        }
      }
    }
  }

  @Override
  public void loadScene(Scene scene) {

  }

  public void update(double dt) {

    levelEditorStuff.update(dt);
  }


  @Override
  public void imgui() {

    ImGui.begin("LevelEditor");
    levelEditorStuff.imgui();
    ImGui.end();

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
      float spriteWidth = sprite.getWidth() * 2;
      float spriteHeight = sprite.getHeight() * 2;
      int id = sprite.getTexture().getTextID();
      Vector2f[] texCoords = sprite.getTextCoords();

      ImGui.pushID(i);
      if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
        GameObject genSprite = Prefabs.generateSpriteWithinGameObject(sprite, .25f, .25f);
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
