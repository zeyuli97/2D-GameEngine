package components;

import jade.Component;
import org.joml.Vector4d;

public class SpriteRender extends Component {

  private Vector4d color;

  public SpriteRender(Vector4d color) {
    this.color = color;
  }

  @Override
  public void start() {

  }
  @Override
  public void update(double dt) {

  }

  public Vector4d getColor() {
    return color;
  }
}
