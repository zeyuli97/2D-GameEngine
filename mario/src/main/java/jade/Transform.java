package jade;

import org.joml.Vector2d;

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
}
