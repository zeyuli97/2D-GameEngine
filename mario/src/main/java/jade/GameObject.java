package jade;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.*;
import imgui.ImGui;
import util.AssetPool;

import java.util.ArrayList;
import java.util.List;

public class GameObject {

  private int gameObjectID = -1;
  public String name;

  private List<Component> components;

  public transient Transform transform;

  private static int id_Counter = 0;
  private boolean SERIALIZATION = true;
  private boolean isDead = false;


  public GameObject(String name) {
    this.name = name;
    components = new ArrayList<>();
    this.gameObjectID = id_Counter++;
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
    component.generateID();
    components.add(component);
    component.setGameObject(this); // tie the component with the current game object.

  }

  public void editorUpdate(float dt) {
    for (int i = 0; i < components.size(); i++) {
      components.get(i).editorUpdate(dt);
    }
  }

  public void update(float dt) {
    for (int i = 0; i < components.size(); i++) {
      components.get(i).update(dt);
    }
  }

  public void start() {
    for (int i = 0; i < components.size(); i++) {
      components.get(i).start();
    }
  }

  public Transform getTransform() {
    return transform;
  }


  public void imgui() {
    for (Component component : components) {
      if (ImGui.collapsingHeader(component.getClass().getSimpleName())) {
        component.imgui();
      }
    }
  }

  public static void init(int maxId) {
    id_Counter = maxId;
  }

  public int getUid() {
    return gameObjectID;
  }

  public List<Component> getComponents() {
    return components;
  }

  public void setNoSerialize() {
    this.SERIALIZATION = false;
  }

  public void setSERIALIZATION() {
    this.SERIALIZATION = true;
  }

  public boolean isSERIALIZATION() {

    return SERIALIZATION;
  }

  public void setTransform(Transform transform) {
    if (this.transform != null) {
      this.transform.setPosition(transform.getPosition());
      this.transform.setRotation(transform.getRotation());
      this.transform.setScale(transform.getScale());
    } else {
      this.transform = transform;
    }

  }

  public void destroy() {
    this.isDead = true;
    for (int i = 0; i < components.size(); i++) {
      components.get(i).destroy();
    }
  }

  public boolean isDead() {
    return isDead;
  }


  public GameObject copy() {
    Gson gson = new GsonBuilder().registerTypeAdapter(Component.class, new ComponentDeserializer())
            .registerTypeAdapter(GameObject.class, new GameObjectDeserializer()).enableComplexMapKeySerialization().create();
    String json = gson.toJson(this);

    GameObject go = gson.fromJson(json, GameObject.class);
    go.generateUID();

    for (Component component : components) {
      component.generateID();
    }

    SpriteRender spr = go.getComponent(SpriteRender.class);
    if (spr != null && spr.getTexture() != null) {
      spr.setTexture(AssetPool.getTexture(spr.getTexture().getFilePath()));
    }

    if (go.getComponent(StateMachine.class) != null) {
      go.getComponent(StateMachine.class).refreshTextures();
    }
    return go;
  }

  private void generateUID() {
    this.gameObjectID = id_Counter++;
  }
}


