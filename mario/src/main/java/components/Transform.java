package components;

import editor.JImGui;
import org.joml.Vector2f;

/**
 * The Transform Class stores two key information.
 * The first information is the position of the game object.
 * The second is the scale of the game object.
 * */
public class Transform extends Component{

  public Vector2f position;
  public Vector2f scale;
  public float rotation = 0f;
  public int zIndex;

  public Transform() {
    init(new Vector2f(), new Vector2f());
  }

  public Transform(Vector2f position) {
    init(position, new Vector2f());
  }

  public Transform(Vector2f position, Vector2f scale) {
    init(position, scale);
  }

  private void init(Vector2f position, Vector2f scale) {
    this.position = position;
    this.scale = scale;
    this.zIndex = 0;
  }

  @Override
  public void imgui() {
    JImGui.drawVector2Control("Position", this.position);
    JImGui.drawVector2Control("Scale", this.scale, 32f);
    this.rotation = JImGui.dragFloat("Rotation", this.rotation);
    this.zIndex = JImGui.dragInt("Z Index", this.zIndex);
  }

  public Vector2f getPosition() {
    return position;
  }

  public Vector2f getScale() {
    return scale;
  }

  public Transform copy() {
    return new Transform(new Vector2f(position), new Vector2f(scale));
  }


  public void copyTo(Transform transform) {
    transform.position.set(this.position);
    transform.scale.set(this.scale);
  }

  public void setPositionX(float x) {
    this.position.x = x;
  }

  public void setPositionY(float y) {
    this.position.y = y;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || this.getClass() != obj.getClass()) {
      return false;
    }
    Transform transform = (Transform) obj;
    return this.position.equals(transform.getPosition())
            && this.scale.equals(transform.getScale())
            && this.zIndex == transform.getZIndex()
            && this.rotation == transform.getRotation();
  }

  public void setPosition(Vector2f newPosition) {
    this.position = newPosition;
  }

  public float getRotation() {
    return rotation;
  }

  public void setRotation(float rotation) {
    this.rotation = rotation;
  }

  public void setScaleX(float scaleX) {
    this.scale.x = scaleX;
  }

  public void setScaleY(float scaleY) {
    this.scale.y = scaleY;
  }

  public void setScale(Vector2f scale) {
    this.scale = scale;
  }

  public int getZIndex() {
    return zIndex;
  }

  public void setZIndex(int zIndex) {
    this.zIndex = zIndex;
  }
}
