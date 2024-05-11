package components;


import jade.Window;
import org.joml.Vector2f;
import physics2d.components.Box2DCollider;
import physics2d.components.CircleCollider;
import physics2d.components.Rigidbody2D;
import renders.DebugDraw;

/**
 * The PillBox Collider represents the mario inside of physics world.
 * */
public class PillboxCollider extends Component {
  private transient CircleCollider topCircle = new CircleCollider();
  private transient CircleCollider bottomCircle = new CircleCollider();
  private transient Box2DCollider middleBox = new Box2DCollider();
  // reset usage: Mario changed in size during collision ie. mushroom. Update the fixture in next frame update.
  private transient boolean resetFixtureNextFrame = false;

  public float width = 0.1f;
  public float height = 0.2f;
  public Vector2f offset = new Vector2f();

  @Override
  public void start() {
    //this.topCircle.gameObject = this.gameObject;
    this.bottomCircle.gameObject = this.gameObject;
    this.middleBox.gameObject = this.gameObject;
    recalculatedColliders();
  }

  @Override
  public void update(float dt) {
    if (resetFixtureNextFrame) {
      resetFixture();
    }
  }

  @Override
  public void editorUpdate(float dt) {
    //topCircle.editorUpdate(dt);
    bottomCircle.editorUpdate(dt);
    middleBox.editorUpdate(dt);
    recalculatedColliders();


    if (resetFixtureNextFrame)  {
      resetFixture();
    }
  }

  public void setWidth(float width) {
    this.width = width;
    recalculatedColliders();
    resetFixture();
  }

  public void setHeight(float height) {
    this.height = height;
    recalculatedColliders();
    resetFixture();
  }

  public void resetFixture() {
    if (Window.getPhysics().isLocked()) {
      resetFixtureNextFrame = true;
      return;
    }
    resetFixtureNextFrame = false;

    if (gameObject != null) {
      Rigidbody2D rb = gameObject.getComponent(Rigidbody2D.class);
      if (rb != null) {
        Window.getPhysics().resetPillboxCollider(rb, this);
      }
    }
  }

  public void recalculatedColliders() {
//    float circleRadius = width / 4;
//    float boxHeight = height - 2 * circleRadius;
//    topCircle.setRadius(circleRadius);
//    bottomCircle.setRadius(circleRadius);
//    topCircle.setOffset(new Vector2f(offset).add(0, boxHeight / 4));
//    bottomCircle.setOffset(new Vector2f(offset).sub(0, boxHeight / 4));
//    middleBox.setHalfSize(new Vector2f(width / 2, boxHeight / 2));
//    middleBox.setOffset(offset);

    float circleRadius = width / 2f;
    float boxHeight = height - circleRadius;
    bottomCircle.setRadius(circleRadius);
    bottomCircle.setOffset(new Vector2f(offset).sub(0, (height - (circleRadius * 2f)) / 2f));
    middleBox.setHalfSize(new Vector2f(width - 0.01f, boxHeight));
    middleBox.setOffset(new Vector2f(offset).add(0, (height - boxHeight) / 2f));
  }

  public Box2DCollider getMiddleBox() {
    return middleBox;
  }

  public CircleCollider getBottomCircle() {
    return bottomCircle;
  }

  public CircleCollider getTopCircle() {
    return topCircle;
  }
}
