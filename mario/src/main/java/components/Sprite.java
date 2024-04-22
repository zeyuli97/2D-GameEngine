package components;

import org.joml.Vector2f;
import renders.Texture;

/**
 * Sprite is the term for 2D image.
 * Sprite is all about the texture.
 * */
public class Sprite {

  private Texture texture;
  private Vector2f[] textCoords;

  public Sprite(Texture texture) {
    this.texture = texture;
    this.textCoords = new Vector2f[] {
            new Vector2f(1,1),
            new Vector2f(1,0),
            new Vector2f(0,0),
            new Vector2f(0,1)
    };
  }

  public Sprite(Texture texture, Vector2f[] array) {
    this.texture = texture;
    this.textCoords = array;
  }

  public Texture getTexture() {
    return this.texture;
  }

  public Vector2f[] getTextCoords() {
    return this.textCoords;
  }
}
