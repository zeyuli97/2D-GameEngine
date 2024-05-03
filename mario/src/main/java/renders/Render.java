package renders;

import components.SpriteRender;
import jade.GameObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Render {
  private final int MAX_BATCH_SIZE = 1000;
  private List<RenderBatch> batches;
  private static Shader currentShader;

  public Render() {
    this.batches = new ArrayList<>();
  }

  public void add(GameObject go) {
    SpriteRender sprite = go.getComponent(SpriteRender.class);
    if (sprite != null) {
      add_helper(sprite);
    }
  }


  private void add_helper(SpriteRender sprite) {
    boolean added = false;
    for (RenderBatch batch : batches) {
      if (batch.getHasRoom() && batch.getzIndex() == sprite.getGameObject().getzIndex()) {
        Texture texture = sprite.getTexture();
        if (texture == null || batch.containsTexture(texture) || batch.hasTextureRoom()) {
          batch.addSprite(sprite);
          added = true;
          break;
        }
      }
    }

    if (!added) {
      RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE, sprite.getGameObject().getzIndex());
      newBatch.start();
      batches.add(newBatch);
      newBatch.addSprite(sprite);
      Collections.sort(batches); // sort the batches according to zIndex.
    }
  }

  public static void bindShader(Shader shader) {
    currentShader = shader;
  }

  public static Shader getCurrentShader() {
    return currentShader;
  }

  public void render() {

    for (RenderBatch batch : batches) {
      batch.render();
    }
  }

}
