package Scene;
import components.*;
import jade.*;
import util.AssetPool;


public class LevelSceneInitializer extends SceneInitializer {


  public LevelSceneInitializer() {

  }

  @Override
  public void init(Scene scene) {
    SpriteSheet sprites = AssetPool.getSpriteSheet("assets/images/spritesheets/decorationsAndBlocks.png");

    GameObject cameraObject = scene.createGameObject("GameCamera");
    cameraObject.addComponent(new GameCamera(scene.getCamera()));
    cameraObject.start();
    scene.addGameObjectToScene(cameraObject);
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

    AssetPool.addSpriteSheet("assets/images/turtle.png",
            new SpriteSheet(AssetPool.getTexture("assets/images/turtle.png"),
                    16, 24, 4, 0));

    AssetPool.addSpriteSheet("assets/images/bigSpritesheet.png",
            new SpriteSheet(AssetPool.getTexture("assets/images/bigSpritesheet.png"),
                    16, 32, 42, 0));

    AssetPool.addSpriteSheet("assets/images/pipes.png",
            new SpriteSheet(AssetPool.getTexture("assets/images/pipes.png"),
                    32, 32, 4, 0));

    AssetPool.addSpriteSheet("assets/images/items.png",
            new SpriteSheet(AssetPool.getTexture("assets/images/items.png"),
                    16, 16, 43, 0));

    AssetPool.addSpriteSheet("assets/images/gizmos.png",
            new SpriteSheet(AssetPool.getTexture("assets/images/gizmos.png"),
                    24, 48, 3, 0));

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

    AssetPool.getSound("assets/sounds/main-theme-overworld.ogg").playSound();

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
  public void imgui() {

  }
}
