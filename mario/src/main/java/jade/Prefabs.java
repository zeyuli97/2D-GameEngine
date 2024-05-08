package jade;

import components.*;
import org.joml.Vector2f;
import util.AssetPool;

public class Prefabs {

  public static GameObject generateSpriteWithinGameObject(Sprite sprite, float sizeX, float sizeY) {
    GameObject block = Window.getCurrentScene().createGameObject("Generated");
    block.setTransform(new Transform(new Vector2f(), new Vector2f(sizeX, sizeY)));
    SpriteRender render = new SpriteRender();
    render.setSprite(sprite);
    block.addComponent(render);

    return block;
  }

  public static GameObject generateMario() {
    SpriteSheet playerSpriteSheet = AssetPool.getSpriteSheet("assets/images/spritesheet.png");
    GameObject mario = generateSpriteWithinGameObject(playerSpriteSheet.getSprite(0), 0.25f, 0.25f);

    AnimationState run = new AnimationState();
    run.title = "Run";
    float defaultFrameTime = 0.23f;
    run.addFrame(playerSpriteSheet.getSprite(0), defaultFrameTime);
    run.addFrame(playerSpriteSheet.getSprite(2), defaultFrameTime);
    run.addFrame(playerSpriteSheet.getSprite(3), defaultFrameTime);
    run.addFrame(playerSpriteSheet.getSprite(2), defaultFrameTime);
    run.doesLoop = true;

    StateMachine stateMachine = new StateMachine();
    stateMachine.addState(run);
    stateMachine.setDefaultState(run.title);
    mario.addComponent(stateMachine);
    return mario;
  }

  public static GameObject generateQuestionBlock() {
    SpriteSheet playerSprites = AssetPool.getSpriteSheet("assets/images/items.png");
    GameObject questionBlock = generateSpriteWithinGameObject(playerSprites.getSprite(0), 0.25f, 0.25f);

    AnimationState run = new AnimationState();
    run.title = "Question";
    float defaultFrameTime = 0.23f;
    run.addFrame(playerSprites.getSprite(0), 0.57f);
    run.addFrame(playerSprites.getSprite(1), defaultFrameTime);
    run.addFrame(playerSprites.getSprite(2), defaultFrameTime);
    run.doesLoop = true;

    StateMachine stateMachine = new StateMachine();
    stateMachine.addState(run);
    stateMachine.setDefaultState(run.title);
    questionBlock.addComponent(stateMachine);

    return questionBlock;
  }
}
