package Scene;

import components.*;
import imgui.ImGui;
import imgui.ImVec2;
import jade.*;
import org.joml.Vector2f;
import physics2d.components.Box2DCollider;
import physics2d.components.Rigidbody2D;
import physics2d.enums.BodyType;
import util.AssetPool;

import java.io.File;
import java.util.Collection;

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

    levelEditorStuff.addComponent(new MouseControl());
    levelEditorStuff.addComponent(new GridLines());
    levelEditorStuff.addComponent(new EditorCamera(scene.getCamera()));
    levelEditorStuff.addComponent(new GizmoSystem(gizmos));
    levelEditorStuff.addComponent(new KeyControl());

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
    AssetPool.addSpriteSheet("assets/images/turtle.png",
            new SpriteSheet(AssetPool.getTexture("assets/images/turtle.png"),
                    16, 24, 4, 0));
    AssetPool.addSpriteSheet("assets/images/bigSpritesheet.png",
            new SpriteSheet(AssetPool.getTexture("assets/images/bigSpritesheet.png"),
                    16, 32, 42, 0));
    AssetPool.addSpriteSheet("assets/images/pipes.png",
            new SpriteSheet(AssetPool.getTexture("assets/images/pipes.png"),
                    32, 32, 4, 0));

    AssetPool.addSound("assets/sounds/main-theme-overworld.ogg", true);
    AssetPool.addSound("assets/sounds/flagpole.ogg", false);
    AssetPool.addSound("assets/sounds/break_block.ogg", false);
    AssetPool.addSound("assets/sounds/bump.ogg", false);
    AssetPool.addSound("assets/sounds/coin.ogg", false);
    AssetPool.addSound("assets/sounds/gameover.ogg", false);
    AssetPool.addSound("assets/sounds/jump-small.ogg", false);
    AssetPool.addSound("assets/sounds/mario_die.ogg", false);
    AssetPool.addSound("assets/sounds/pipe.ogg", false);
    AssetPool.addSound("assets/sounds/powerup.ogg", false);
    AssetPool.addSound("assets/sounds/powerup_appears.ogg", false);
    AssetPool.addSound("assets/sounds/stage_clear.ogg", false);
    AssetPool.addSound("assets/sounds/stomp.ogg", false);
    AssetPool.addSound("assets/sounds/kick.ogg", false);
    AssetPool.addSound("assets/sounds/invincible.ogg", false);

    AssetPool.getSound("assets/sounds/main-theme-overworld.ogg").stopSound();

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

  public void update(float dt) {

    levelEditorStuff.update(dt);
  }


  @Override
  public void imgui() {

    ImGui.begin("LevelEditor");
    levelEditorStuff.imgui();
    ImGui.end();

    ImGui.begin("Objects");

    if (ImGui.beginTabBar("WindowTabBar")) {

      if (ImGui.beginTabItem("Solid Blocks")) {

        ImVec2 windowPos = new ImVec2();
        ImGui.getWindowPos(windowPos);
        ImVec2 windowSize = new ImVec2();
        ImGui.getWindowSize(windowSize);
        ImVec2 itemSpacing = new ImVec2();
        ImGui.getStyle().getItemSpacing(itemSpacing);

        float windowX2 = windowPos.x + windowSize.x;
        for (int i = 0; i < sprites.size(); i++) {
          if (i == 34) continue;
          if (i >= 38 && i < 61) continue;

          Sprite sprite = sprites.getSprite(i);
          float spriteWidth = sprite.getWidth() * 2;
          float spriteHeight = sprite.getHeight() * 2;
          int id = sprite.getTexture().getTextID();
          Vector2f[] texCoords = sprite.getTextCoords();

          ImGui.pushID(i);
          if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
            GameObject genSprite = Prefabs.generateSpriteWithinGameObject(sprite, .25f, .25f);
            Rigidbody2D rb = new Rigidbody2D();
            rb.setBodyType(BodyType.Static);
            genSprite.addComponent(rb);
            Box2DCollider box2DCollider = new Box2DCollider();
            box2DCollider.setHalfSize(new Vector2f(0.25f));
            genSprite.addComponent(box2DCollider);
            genSprite.addComponent(new Ground());
            // The breakable brick
            if (i == 12) {
              genSprite.addComponent(new BreakableBrick());
            }

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

      if (ImGui.beginTabItem("Decoration Blocks")) {
        ImVec2 windowPos = new ImVec2();
        ImGui.getWindowPos(windowPos);
        ImVec2 windowSize = new ImVec2();
        ImGui.getWindowSize(windowSize);
        ImVec2 itemSpacing = new ImVec2();
        ImGui.getStyle().getItemSpacing(itemSpacing);

        float windowX2 = windowPos.x + windowSize.x;
        for (int i = 34; i < 61; i++) {
          if (i >= 35 && i < 38) continue;
          if (i >= 42 && i < 45) continue;

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
        int uniqueID = 0;
        SpriteSheet playerSprite = AssetPool.getSpriteSheet("assets/images/spritesheet.png");
        Sprite sprite = playerSprite.getSprite(0);
        float spriteWidth = sprite.getWidth() * 2;
        float spriteHeight = sprite.getHeight() * 2;
        int id = sprite.getTexture().getTextID();
        Vector2f[] texCoords = sprite.getTextCoords();

        ImGui.pushID(uniqueID++);
        if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
          GameObject genSprite = Prefabs.generateMario();
          levelEditorStuff.getComponent(MouseControl.class).pickupObject(genSprite);
        }
        ImGui.popID();
        ImGui.sameLine();

        SpriteSheet items = AssetPool.getSpriteSheet("assets/images/items.png");
        sprite = items.getSprite(0);
        id = sprite.getTexture().getTextID();
        texCoords = sprite.getTextCoords();

        ImGui.pushID(uniqueID++);
        if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
          GameObject object = Prefabs.generateQuestionBlock();
          levelEditorStuff.getComponent(MouseControl.class).pickupObject(object);
        }

        ImGui.popID();
        ImGui.sameLine();

        sprite = items.getSprite(7);
        id = sprite.getTexture().getTextID();
        texCoords = sprite.getTextCoords();
        ImGui.pushID(uniqueID++);
        if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
          GameObject object = Prefabs.generateCoin();
          levelEditorStuff.getComponent(MouseControl.class).pickupObject(object);
        }
        ImGui.popID();
        ImGui.sameLine();

        sprite = items.getSprite(14);
        id = sprite.getTexture().getTextID();
        texCoords = sprite.getTextCoords();
        ImGui.pushID(uniqueID++);
        if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
          GameObject object = Prefabs.generateGoomba();
          levelEditorStuff.getComponent(MouseControl.class).pickupObject(object);
        }
        ImGui.popID();
        ImGui.sameLine();

        SpriteSheet pipes = AssetPool.getSpriteSheet("assets/images/pipes.png");
        sprite = pipes.getSprite(0);
        id = sprite.getTexture().getTextID();
        texCoords = sprite.getTextCoords();
        ImGui.pushID(uniqueID++);
        if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
          GameObject object = Prefabs.generatePipe(Direction.Down);
          levelEditorStuff.getComponent(MouseControl.class).pickupObject(object);
        }
        ImGui.popID();
        ImGui.sameLine();

        sprite = pipes.getSprite(1);
        id = sprite.getTexture().getTextID();
        texCoords = sprite.getTextCoords();
        ImGui.pushID(uniqueID++);
        if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
          GameObject object = Prefabs.generatePipe(Direction.Up);
          levelEditorStuff.getComponent(MouseControl.class).pickupObject(object);
        }
        ImGui.popID();
        ImGui.sameLine();

        sprite = pipes.getSprite(2);
        id = sprite.getTexture().getTextID();
        texCoords = sprite.getTextCoords();
        ImGui.pushID(uniqueID++);
        if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
          GameObject object = Prefabs.generatePipe(Direction.Right);
          levelEditorStuff.getComponent(MouseControl.class).pickupObject(object);
        }
        ImGui.popID();
        ImGui.sameLine();

        sprite = pipes.getSprite(3);
        id = sprite.getTexture().getTextID();
        texCoords = sprite.getTextCoords();
        ImGui.pushID(uniqueID++);
        if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
          GameObject object = Prefabs.generatePipe(Direction.Left);
          levelEditorStuff.getComponent(MouseControl.class).pickupObject(object);
        }
        ImGui.popID();
        ImGui.sameLine();

        SpriteSheet turtle = AssetPool.getSpriteSheet("assets/images/turtle.png");
        sprite = turtle.getSprite(0);
        id = sprite.getTexture().getTextID();
        texCoords = sprite.getTextCoords();
        ImGui.pushID(uniqueID++);
        if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
          GameObject object = Prefabs.generateTurtle();
          levelEditorStuff.getComponent(MouseControl.class).pickupObject(object);
        }
        ImGui.popID();
        ImGui.sameLine();

        sprite = items.getSprite(6);
        id = sprite.getTexture().getTextID();
        texCoords = sprite.getTextCoords();
        ImGui.pushID(uniqueID++);
        if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
          GameObject object = Prefabs.generateFlagTop();
          levelEditorStuff.getComponent(MouseControl.class).pickupObject(object);
        }
        ImGui.popID();
        ImGui.sameLine();

        sprite = items.getSprite(33);
        id = sprite.getTexture().getTextID();
        texCoords = sprite.getTextCoords();
        ImGui.pushID(uniqueID++);
        if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
          GameObject object = Prefabs.generateFlagPole();
          levelEditorStuff.getComponent(MouseControl.class).pickupObject(object);
        }
        ImGui.popID();

        ImGui.endTabItem();
      }

      if (ImGui.beginTabItem("Sounds")) {
        Collection<Sound> sounds = AssetPool.getSounds();
        int soundNum = sounds.size();
        int counter = 1; // for sound alignment
        for (Sound sound : sounds) {
          File tmp = new File(sound.getFilePath());
          if (ImGui.button(tmp.getName())) {
            if (!sound.isPlaying()) {
              sound.playSound();
            } else {
              sound.stopSound();
            }
          }
          if (counter % 5 == 0) {
            ImGui.newLine();
          } else {
            ImGui.sameLine();
          }
          counter++;
        }

        ImGui.endTabItem();
      }

      ImGui.endTabBar();
    }

    ImGui.end();
  }

}
