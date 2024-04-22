package components;

import jade.Component;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector4d;
import org.w3c.dom.Text;
import renders.Texture;

public class SpriteRender extends Component {

  private Sprite sprite;
  private Vector4d color;

  public SpriteRender(Vector4d color) {
    this.color = color;
    this.sprite = new Sprite(null);
  }

  public SpriteRender(Sprite sprite) {
    this.sprite = sprite;
    this.color = new Vector4d(1,1,1,1);
  }

  @Override
  public void start() {

  }
  @Override
  public void update(double dt) {

  }

  public Vector4d getColor() {
    return color;
  }

  public Texture getTexture() {
    return sprite.getTexture();
  }

  public Vector2f[] getTextureCoords() {
    return this.sprite.getTextCoords();
  }
}
