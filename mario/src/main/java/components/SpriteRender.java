package components;

import jade.Component;
import jade.Transform;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector4d;
import org.w3c.dom.Text;
import renders.Texture;

public class SpriteRender extends Component {

  private Sprite sprite;
  private Vector4d color;
  private Transform lastTransform;
  private boolean isDirty = false; // IsDirty will tell whether we need to update the sprite.

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
    this.lastTransform = this.gameObject.getTransform().copy();

  }
  @Override
  public void update(double dt) {
    if (!this.lastTransform.equals(this.gameObject.getTransform())) {
      this.gameObject.getTransform().copyTo(lastTransform);
      isDirty = true;
    }
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

  public void setSprite(Sprite sprite) {
    this.sprite = sprite;
    this.isDirty = true;
  }

  public void setColor(Vector4d color) {
    if (!this.color.equals(color)) {
      this.color = color;
      this.isDirty = true;
    }
  }

  public boolean isDirty() {
    return isDirty;
  }

  public void setDirtyToClean() {
    this.isDirty = false;
  }
}
