package jade;

import java.util.ArrayList;
import java.util.List;

public class GameObject {

  private String name;

  private List<Component> components;

  private Transform transform;

  private int zIndex;

  public GameObject(String name) {
    this.name = name;
    components = new ArrayList<>();
    this.transform = new Transform();
    this.zIndex = 0;
  }

  public GameObject(String name, Transform transform, int zIndex) {
    this.name = name;
    components = new ArrayList<>();
    this.transform = transform;
    this.zIndex = zIndex;
  }

  /**
   * Method will return the desired component class.
   * If the class is not found in the List, return null.
   * */
  public <T extends Component> T getComponent(Class<? extends T> componentClass) {
    for (Component c : components) {
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

  public<T extends Component> void removeComponent(Class<? extends T> componentClass) {
    for (int i = 0; i < components.size(); i++) {
      Component c = components.get(i);
      if (componentClass.isAssignableFrom(c.getClass())) {
        components.remove(i);
        return;
      }
    }

  }

  public void addComponent(Component component) {
    components.add(component);
    component.gameObject = this; // tie the component with the current game object.

  }

  public void update(double dt) {
    for (Component component : components) {
      component.update(dt);
    }
  }

  public void start() {
    for (Component component : components) {
      component.start();
    }
  }

  public Transform getTransform() {
    return transform;
  }

  public int getzIndex() {
    return zIndex;
  }

  public void imgui() {
    for (Component component : components) {
      component.imgui();
    }
  }
}


