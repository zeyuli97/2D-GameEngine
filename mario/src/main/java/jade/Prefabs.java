package jade;

import components.Sprite;
import components.SpriteRender;
import org.joml.Vector2d;

public class Prefabs {

  public static GameObject generateSpriteWithinGameObject(Sprite sprite, float sizeX, float sizeY) {
    GameObject block = new GameObject("Sprite_Object_GameObject_Generation",
            new Transform(new Vector2d(), new Vector2d(sizeX, sizeY)), 0);
    SpriteRender render = new SpriteRender();
    render.setSprite(sprite);
    block.addComponent(render);

    return block;
  }
}
