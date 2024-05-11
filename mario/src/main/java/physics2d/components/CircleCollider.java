package physics2d.components;

import components.Component;
import jade.Window;
import org.joml.Vector2f;
import renders.DebugDraw;

public class CircleCollider extends Component {
  private float radius = 1f;
  private transient boolean resetFixtureInNextFrame = false;
  private Vector2f offset = new Vector2f();

  public Vector2f getOffset() {
    return offset;
  }

  public float getRadius() {
    return radius;
  }

  public void setOffset(Vector2f offset) {
    this.offset.set(offset);
  }

  public void setRadius(float radius) {
    resetFixtureInNextFrame = true;
    this.radius = radius;
  }

  @Override
  public void editorUpdate(float dt) {
    Vector2f center = new Vector2f(this.gameObject.transform.position).add(this.offset);
    DebugDraw.addCircle(center, this.radius);

    if (resetFixtureInNextFrame) {
      resetFixture();
    }
  }

  @Override
  public void update(float dt) {
    if (resetFixtureInNextFrame) {
      resetFixture();
    }
  }

  public void resetFixture() {
    if (Window.getPhysics().isLocked()) {
      resetFixtureInNextFrame = true;
      return;
    }

    resetFixtureInNextFrame = false;

    if (gameObject != null) {
      Rigidbody2D rigidbody2D = gameObject.getComponent(Rigidbody2D.class);
      if (rigidbody2D != null) {
        Window.getPhysics().resetCircleCollider(rigidbody2D, this);
      }
    }
  }
}
