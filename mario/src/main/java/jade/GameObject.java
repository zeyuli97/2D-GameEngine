package jade;

import java.util.ArrayList;
import java.util.List;

public class GameObject <T extends Component> {

  private String name;

  private List<Component> componets;

  public GameObject(String name) {
    this.name = name;
    componets = new ArrayList<>();

  }

  /**
   * Method will return the desired component class.
   * If the class is not found in the List, return null.
   * */
  public T getComponent(Class<T> componentClass) {
    for (Component c : componets) {
      // isAssignableFrom() allows same class or superclass/superinterface to pass.
      if (componentClass.isAssignableFrom(c.getClass())) {
        try {
          return componentClass.cast(c);
        } catch (ClassCastException e) {
          assert false : "Error occurred during casting!";
        }
      }
    }
    return null; // not found
  }

  public void removeComponent(Class<T> componentClass) {
    for (int i = 0; i < componets.size(); i++) {
      Component c = componets.get(i);
      if (componentClass.isAssignableFrom(c.getClass())) {
        componets.remove(i);
        return;
      }
    }

  }

  public void addComponent(Component component) {
    if (!componets.contains(component)) {
      componets.add(component);
      component.gameObject = this; // tie the component with the current game object.
    }
  }

  public void update(double dt) {
    for (Component component : componets) {
      component.update(dt);
    }
  }

  public void start() {
    for (Component component : componets) {
      component.start();
    }
  }



}


