package components;

import jade.Component;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class RigidBody extends Component {
  private Vector3f velocity = new Vector3f(1,1,0);
  private int bodyID = 0;
  private float friction = 0.5f;
  private transient Vector4f hide = new Vector4f(1,1,1,1);
}
