package components;

import org.joml.Vector2f;
import renders.Texture;

import java.util.ArrayList;
import java.util.List;

/**
 * Sprite Sheet will make SpriteSheet into individual sprites.
 * The order to read sprites: Left to right top to bottom.
 * The order to read sprite: start from bottom left coordinate.
 * */
public class SpriteSheet {

  // The texture here is the overall texture -- SpriteSheet.
  // All the individual sprite is contained inside this Texture.
  private Texture texture;

  private List<Sprite> sprites;

  private int spriteWidth;

  private int spriteHeight;

  private int numSprites;
  private int spacing;

  /**
   * The SpriteSheet constructor.
   * @param texture The texture location that contains SpriteSheet.
   * @param spriteWidth The width of each sprite image.
   * @param spriteHeight The height of each sprite image.
   * @param numSprites total number of sprites inside SpriteSheet.
   * @param spacing Whether there is space between each sprite.
   * */
  public SpriteSheet(Texture texture, int spriteWidth, int spriteHeight, int numSprites, int spacing) {
    this.sprites = new ArrayList<>();
    this.texture = texture;

    // This is the top left most sprite's bottom right coords.
    int currentX = 0;
    int currentY = texture.getHeight() - spriteHeight;
    for (int i = 0; i < numSprites; i++) {
      float topY = (currentY + spriteHeight) / (float) texture.getHeight();
      float rightX = (currentX + spriteWidth) / (float) texture.getWidth();
      float leftX = currentX / (float) texture.getWidth();
      float bottomY = currentY / (float) texture.getHeight();

      Vector2f[] textureCoords = {
              new Vector2f(rightX,topY),
              new Vector2f(rightX,bottomY),
              new Vector2f(leftX,bottomY),
              new Vector2f(leftX,topY)
      };

      Sprite sprite = new Sprite();
      sprite.setTexture(texture);
      sprite.setTextCoords(textureCoords);
      sprites.add(sprite);

      currentX += spriteWidth + spacing;
      if (currentX >= texture.getWidth()) {
        currentX = 0;
        currentY -= spriteHeight + spacing;
      }
    }

  }


  public Sprite getSprite(int index) {
    return  this.sprites.get(index);
  }
}
