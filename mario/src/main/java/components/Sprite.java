package components;

import org.joml.Vector2f;
import renders.Texture;

/**
 * Sprite is the term for 2D image.
 * Sprite is all about the texture.
 * */
public class Sprite {
  private int width, height;

  private Texture texture = null;
  private Vector2f[] textCoords = new Vector2f[] {
          new Vector2f(1,1),
          new Vector2f(1,0),
          new Vector2f(0,0),
          new Vector2f(0,1)};


  public Texture getTexture() {
    return this.texture;
  }

  public Vector2f[] getTextCoords() {
    return this.textCoords;
  }

  public void setTexture(Texture texture) {
    this.texture = texture;
  }

  public void setTextCoords(Vector2f[] textCoords) {
    this.textCoords = textCoords;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public int getHeight() {
    return height;
  }

  public int getWidth() {
    return width;
  }
}
