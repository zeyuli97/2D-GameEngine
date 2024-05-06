package components;

import editor.JImGui;
import imgui.ImGui;
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
    if (this.gameObject.getTransform() != null) {
      this.lastTransform = this.gameObject.getTransform().copy();
    }

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
    if (JImGui.colorPicker4("Color Picker", color)) {
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
