package components;

import jade.GameObject;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

public class FlagPole extends Component {
  private boolean isPole = true;


  public FlagPole(boolean isPole) {
    this.isPole = isPole;
  }

  @Override
  public void beginCollision(GameObject obj, Contact contact, Vector2f contactNormal) {
    PlayerController playerController = obj.getComponent(PlayerController.class);
    if (playerController != null) {
      playerController.playWinAnimation(this.gameObject);
    }
  }
}
