package jade;


/**
 * The Component class that interact with both Scene and GameObject.
 * The relationship: Scene remembers all the Game Objects; Game Object remembers Components;
 * Component also remembers which gameObject it belongs to.
 * */
public abstract class Component {

  protected GameObject gameObject = null;

  public void start() {

  }

  public abstract void update(double dt);

  public GameObject getGameObject() {
    return gameObject;
  }
}
