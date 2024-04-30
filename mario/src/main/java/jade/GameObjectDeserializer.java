package jade;

import com.google.gson.*;
import components.Component;

import java.lang.reflect.Type;

public class GameObjectDeserializer implements JsonDeserializer<GameObject> {
  @Override
  public GameObject deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
    JsonObject jsonObject = jsonElement.getAsJsonObject();
    String name = jsonObject.get("name").getAsString();
    JsonArray components = jsonObject.getAsJsonArray("components");
    Transform transform = jsonDeserializationContext.deserialize(jsonObject.get("transform"), Transform.class);
    int zIndex = jsonDeserializationContext.deserialize(jsonObject.get("zIndex"), int.class);

    GameObject gameObject = new GameObject(name, transform, zIndex);
    for (JsonElement component : components) {
      Component c = jsonDeserializationContext.deserialize(component, Component.class);
      gameObject.addComponent(c);
    }

    return gameObject;
  }
}
