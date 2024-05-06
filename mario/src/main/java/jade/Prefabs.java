package jade;

import components.Sprite;
import components.SpriteRender;
import components.Transform;
import org.joml.Vector2f;

public class Prefabs {

  public static GameObject generateSpriteWithinGameObject(Sprite sprite, float sizeX, float sizeY) {
    GameObject block = Window.getCurrentScene().createGameObject("Generated");
    block.setTransform(new Transform(new Vector2f(), new Vector2f(sizeX, sizeY)));
    SpriteRender render = new SpriteRender();
    render.setSprite(sprite);
    block.addComponent(render);

    return block;
  }
}
