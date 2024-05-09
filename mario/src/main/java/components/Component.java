package components;


import editor.JImGui;
import imgui.ImGui;
import imgui.type.ImInt;
import jade.GameObject;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * The Component class that interact with both Scene and GameObject.
 * The relationship: Scene remembers all the Game Objects; Game Object remembers Components;
 * Component also remembers which gameObject it belongs to.
 * */
public abstract class Component {
  private static int ID_COUNTER = 0; // this is global id.
  private int componentID = -1;

  protected transient GameObject gameObject = null;

  public void start() {

  }

  public void editorUpdate(float dt) {

  }

  public void update(float dt) {

  };

  public GameObject getGameObject() {
    return gameObject;
  }

  public void setGameObject(GameObject gameObject) {
    this.gameObject = gameObject;
  }

  public void destroy() {

  }

  public void beginCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal) {

  }

  public void endCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal) {

  }

  public void preSolve(GameObject collidingObject, Contact contact, Vector2f hitNormal) {

  }

  public void postSolve(GameObject collidingObject, Contact contact, Vector2f hitNormal) {

  }

  public void imgui() {
    try {
      Field[] fields = this.getClass().getDeclaredFields();
      for (Field field : fields) {
        boolean isTransient = Modifier.isTransient(field.getModifiers());
        if (isTransient) {
          continue;
        }
        field.setAccessible(true);

        Class<?> type = field.getType();
        Object object = field.get(this);
        String name = field.getName();

        if (type == int.class) {
          int val = (int) object;
          field.setInt(this, JImGui.dragInt(name, val));
        } else if (type == boolean.class) {
          boolean val = (boolean) object;
          if (ImGui.checkbox(name + ": ", val)) {
            field.set(this, !val);
          }
        } else if (type == float.class) {
          float val = (float) object;
          field.set(this, JImGui.dragFloat(name, val));
        } else if (type == Vector3f.class) {
          Vector3f val = (Vector3f) object;
          JImGui.drawVector3Control(name, val);
        } else if (type == Vector4f.class) {
          Vector4f val = (Vector4f) object;
          float[] imFloat = {val.x, val.y, val.z, val.w};
          if (ImGui.dragFloat4(name + ": ", imFloat)) {
            val.set(imFloat[0], imFloat[1], imFloat[2], imFloat[3]);
          }
        } else if (type == Vector2f.class) {
          Vector2f val = (Vector2f) object;
          JImGui.drawVector2Control(name, val);
        } else if (type.isEnum()) {
          String[] enumValues = getEnumValues(type);
          String enumType = ((Enum)object).name();
          ImInt index = new ImInt(indexOf(enumType, enumValues));
          if (ImGui.combo(field.getName(), index, enumValues, enumValues.length)) {
            field.set(this, type.getEnumConstants()[index.get()]);
          }
        }
        field.setAccessible(false);
      }
    }
    catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  public void generateID() {
    if (this.componentID == -1) {
      this.componentID = ID_COUNTER++;
    }
  }

  public int getUid() {
    return this.componentID;
  }

  public static void init(int maxID) {
    ID_COUNTER = maxID;
  }

  private <T extends Enum<T>> String[] getEnumValues(Class<?> enumType) {
    String[] enumValues = new String[enumType.getEnumConstants().length];
    int i = 0;
    for (Object enumValue : enumType.getEnumConstants()) {
      enumValues[i] = ((Enum<?>) enumValue).name();
      i++;
    }
    return enumValues;
  }

  private int indexOf(String str, String[] arr) {
    for (int i = 0; i < arr.length; i++) {
      if (str.equalsIgnoreCase(arr[i])) {
        return i;
      }
    }
    return -1;
  }
}
