package jade;

import org.joml.Vector2d;

/**
 * The Transform Class stores two key information.
 * The first information is the position of the game object.
 * The second is the scale of the game object.
 * */
public class Transform {

  private Vector2d position;
  private Vector2d scale;

  public Transform() {
    init(new Vector2d(), new Vector2d());
  }

  public Transform(Vector2d position) {
    init(position, new Vector2d());
  }

  public Transform(Vector2d position, Vector2d scale) {
    init(position, scale);
  }

  private void init(Vector2d position, Vector2d scale) {
    this.position = position;
    this.scale = scale;
  }

  public Vector2d getPosition() {
    return position;
  }

  public Vector2d getScale() {
    return scale;
  }

  public Transform copy() {
    return new Transform(new Vector2d(position), new Vector2d(scale));
  }


  public void copyTo(Transform transform) {
    transform.position.set(this.position);
    transform.scale.set(this.scale);
  }

  public void setPositionX(double x) {
    this.position.x = x;
  }

  public void setPositionY(double y) {
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
