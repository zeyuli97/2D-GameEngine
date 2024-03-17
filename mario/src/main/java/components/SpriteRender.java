package components;

import jade.Component;

public class SpriteRender extends Component {

  private boolean isFirstTime = true;

  @Override
  public void start() {
    System.out.println("I am starting");
  }
  @Override
  public void update(double dt) {
    if (isFirstTime) {
      System.out.println("I am updating.");
      isFirstTime = false;
    }
  }
}
