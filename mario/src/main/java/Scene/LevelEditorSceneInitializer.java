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

    AssetPool.addSpriteSheet("assets/images/spritesheets/decorationsAndBlocks.png",
            new SpriteSheet(AssetPool.getTexture("assets/images/spritesheets/decorationsAndBlocks.png"),
                    16, 16, 81, 0));
    AssetPool.addSpriteSheet("assets/images/spritesheet.png",
            new SpriteSheet(AssetPool.getTexture("assets/images/spritesheet.png"),
                    16, 16, 26, 0));
    AssetPool.addSpriteSheet("assets/images/items.png",
            new SpriteSheet(AssetPool.getTexture("assets/images/items.png"),
                    16, 16, 43, 0));
    AssetPool.addSpriteSheet("assets/images/gizmos.png",
            new SpriteSheet(AssetPool.getTexture("assets/images/gizmos.png"),
                    24, 48, 3, 0));

    for (GameObject g : scene.getGameObjects()) {
      if (g.getComponent(SpriteRender.class) != null) {
        SpriteRender spr = g.getComponent(SpriteRender.class);
        if (spr.getTexture() != null) {
          spr.setTexture(AssetPool.getTexture(spr.getTexture().getFilePath()));
        }
      }

      if (g.getComponent(StateMachine.class) != null) {
        StateMachine stateMachine = g.getComponent(StateMachine.class);
        stateMachine.refreshTextures();
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

    ImGui.begin("Objects");

    if (ImGui.beginTabBar("WindowTabBar")) {

      if (ImGui.beginTabItem("Blocks")) {

        ImVec2 windowPos = new ImVec2();
        ImGui.getWindowPos(windowPos);
        ImVec2 windowSize = new ImVec2();
        ImGui.getWindowSize(windowSize);
        ImVec2 itemSpacing = new ImVec2();
        ImGui.getStyle().getItemSpacing(itemSpacing);

        float windowX2 = windowPos.x + windowSize.x;
        for (int i = 0; i < sprites.size(); i++) {
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
        ImGui.endTabItem();
      }

      if (ImGui.beginTabItem("Prefabs")) {
        SpriteSheet playerSprite = AssetPool.getSpriteSheet("assets/images/spritesheet.png");
        Sprite sprite = playerSprite.getSprite(0);
        float spriteWidth = sprite.getWidth() * 2;
        float spriteHeight = sprite.getHeight() * 2;
        int id = sprite.getTexture().getTextID();
        Vector2f[] texCoords = sprite.getTextCoords();

        if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
          GameObject genSprite = Prefabs.generateMario();
          levelEditorStuff.getComponent(MouseControl.class).pickupObject(genSprite);
        }
        ImGui.sameLine();

        SpriteSheet items = AssetPool.getSpriteSheet("assets/images/items.png");
        sprite = items.getSprite(0);
        id = sprite.getTexture().getTextID();
        texCoords = sprite.getTextCoords();
        if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
          GameObject object = Prefabs.generateQuestionBlock();
          levelEditorStuff.getComponent(MouseControl.class).pickupObject(object);
        }
        ImGui.endTabItem();
      }

      ImGui.endTabBar();
    }

    ImGui.end();
  }

}
