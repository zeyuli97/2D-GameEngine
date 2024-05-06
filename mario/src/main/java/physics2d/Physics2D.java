package physics2d;

import components.Transform;
import jade.GameObject;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;
import org.joml.Vector2f;
import physics2d.components.Box2DCollider;
import physics2d.components.CircleCollider;
import physics2d.components.RigidBody2D;

public class Physics2D {
  private Vec2 gravity = new Vec2(0f, -10f);
  private World world = new World(gravity);

  private double physicsTime = 0;
  private float physicsTimeStep = 1 / 60f;
  private int velocityIterations = 8;
  private int positionIterations = 3;

  public void update(double dt) {
    physicsTime += dt;
    if (physicsTime >- 0) {
      physicsTime -= physicsTimeStep;
      world.step(physicsTimeStep, velocityIterations, positionIterations);
    }

  }

  /**
   * We are using our rigid body to wrap the body inside Box2D.
   * */
  public void add(GameObject go) {
    RigidBody2D rb = go.getComponent(RigidBody2D.class);
    if (rb != null && rb.getRawBody() == null) {
      Transform transform = go.getTransform();

      BodyDef bodyDef = new BodyDef();
      bodyDef.angle = (float) Math.toRadians(transform.getRotation());
      bodyDef.position.set(transform.getPosition().x, transform.getPosition().y);
      bodyDef.angularDamping = rb.getAngularDamping();
      bodyDef.linearDamping = rb.getLinearDamping();
      bodyDef.fixedRotation = rb.isFixedRotation();
      bodyDef.bullet = rb.isContinuousCollision();

      switch (rb.getBodyType()) {
        case Kinematic:
          bodyDef.type = BodyType.KINEMATIC;
          break;
        case Dynamic:
          bodyDef.type = BodyType.DYNAMIC;
          break;
        case Static:
          bodyDef.type = BodyType.STATIC;
          break;
      }

      PolygonShape shape = new PolygonShape();
      CircleCollider circleCollider = null;
      Box2DCollider boxCollider = null;

      if ((circleCollider = go.getComponent(CircleCollider.class)) != null) {
        shape.setRadius(circleCollider.getRadius());
      } else if ((boxCollider = go.getComponent(Box2DCollider.class)) != null) {
        Vector2f halfSize = new Vector2f(boxCollider.getHalfSize()).mul(0.5f);
        Vector2f offset = boxCollider.getOffset();
        Vector2f origin = new Vector2f(boxCollider.getOrigin());

        shape.setAsBox(halfSize.x, halfSize.y, new Vec2(origin.x, origin.y), 0);

        Vec2 pos = bodyDef.position;
        float xPos = pos.x + offset.x;
        float yPos = pos.y + offset.y;
        bodyDef.position.set(xPos, yPos);
      }

      Body body = this.world.createBody(bodyDef);
      rb.setRawBody(body);
      body.createFixture(shape, rb.getMass());
    }
  }

  public void destroyGameObject(GameObject go) {
    RigidBody2D rb = go.getComponent(RigidBody2D.class);
    if (rb != null && rb.getRawBody() != null) {
      world.destroyBody(rb.getRawBody());
      rb.setRawBody(null);
    }
  }
}
