package components;

import imgui.ImGui;
import jade.Transform;
import org.joml.Vector2f;
import org.joml.Vector4f;
import renders.Texture;

public class SpriteRender extends Component {

  private Sprite sprite = new Sprite();
  private Vector4f color = new Vector4f(1,1,1,1);

  //transient is a key word for gson, let gson not serialize the transient variable.
  private transient Transform lastTransform;
  private transient boolean isDirty = true; // IsDirty will tell whether we need to update the sprite.


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

  @Override
  public void imgui() {
    float[] imColor = {color.x, color.y, color.z, color.w};
    if (ImGui.colorPicker4("Color Picker", imColor)) {
      this.color.set(imColor[0], imColor[1], imColor[2], imColor[3]);
      this.isDirty = true;
    }
  }

  public Vector4f getColor() {
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

  public void setColor(Vector4f color) {
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

  public void setTexture(Texture texture) {
    this.sprite.setTexture(texture);
  }
}
