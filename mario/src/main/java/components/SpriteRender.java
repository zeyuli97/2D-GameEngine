package components;

import jade.Component;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector4d;
import org.w3c.dom.Text;
import renders.Texture;

public class SpriteRender extends Component {

  private Vector4d color;

  private Vector2d[] textureCoords;

  private Texture texture;

  public SpriteRender(Vector4d color) {
    this.color = color;
    this.texture = null;
  }

  public SpriteRender(Texture texture) {
    this.texture = texture;
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
    return texture;
  }

  public Vector2f[] getTextureCoords() {
    Vector2f[] coords = {
            new Vector2f(1,1),
            new Vector2f(1,0),
            new Vector2f(0,0),
            new Vector2f(0,1)
    };
    return coords;
  }
}
