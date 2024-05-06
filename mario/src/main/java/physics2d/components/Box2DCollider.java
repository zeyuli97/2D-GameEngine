package physics2d.components;

import components.Component;
import org.joml.Vector2f;
import renders.DebugDraw;

public class Box2DCollider extends Collider {

  private Vector2f halfSize = new Vector2f(1);
  private Vector2f origin = new Vector2f();

  public Vector2f getHalfSize() {
    return halfSize;
  }

  public void setHalfSize(Vector2f halfSize) {
    this.halfSize = halfSize;
  }

  public Vector2f getOrigin() {
    return origin;
  }

  @Override
  public void editorUpdate(double dt) {
    Vector2f center = new Vector2f(this.gameObject.transform.position).add(this.getOffset());
    DebugDraw.addBox2D(center, this.halfSize, this.gameObject.transform.rotation);
  }
}
