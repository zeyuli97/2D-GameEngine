package jade;


import imgui.ImGui;
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

  protected transient GameObject gameObject = null;

  public void start() {

  }

  public void update(double dt) {

  };

  public GameObject getGameObject() {
    return gameObject;
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
          int[] imInt = {val};
          if (ImGui.dragInt(name + ": ", imInt)) {
            field.set(this, imInt[0]);
          }
        } else if (type == boolean.class) {
          boolean val = (boolean) object;
          if (ImGui.checkbox(name + ": ", val)) {
            field.set(this, !val);
          }
        } else if (type == float.class) {
          float val = (float) object;
          float[] imFloat = {val};
          if (ImGui.dragFloat(name + ": ", imFloat)) {
            field.set(this, imFloat[0]);
          }
        } else if (type == Vector3f.class) {
          Vector3f val = (Vector3f) object;
          float[] imFloat = {val.x(), val.y(), val.z()};
          if (ImGui.dragFloat3(name + ": ", imFloat)) {
            val.set(imFloat[0], imFloat[1], imFloat[2]);
          }
        } else if (type == Vector4f.class) {
          Vector4f val = (Vector4f) object;
          float[] imFloat = {val.x(), val.y(), val.z(), val.w()};
          if (ImGui.dragFloat4(name + ": ", imFloat)) {
            val.set(imFloat[0], imFloat[1], imFloat[2], imFloat[3]);
          }
        }
        field.setAccessible(false);
      }
    }
    catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }
}
