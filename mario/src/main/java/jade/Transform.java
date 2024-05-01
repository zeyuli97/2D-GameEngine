package jade;

import org.joml.Vector2f;

/**
 * The Transform Class stores two key information.
 * The first information is the position of the game object.
 * The second is the scale of the game object.
 * */
public class Transform {

  private Vector2f position;
  private Vector2f scale;

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
    return this.position.equals(transform.position) && this.scale.equals(transform.scale);
  }
}
