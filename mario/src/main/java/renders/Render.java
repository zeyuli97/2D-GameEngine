package renders;

import components.SpriteRender;
import jade.GameObject;

import java.util.ArrayList;
import java.util.List;

public class Render {
  private final int MAX_BATCH_SIZE = 1000;
  private List<RenderBatch> batches;

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
      if (batch.getHasRoom()) {
        batch.addSprite(sprite);
        added = true;
        break;
      }
    }

    if (!added) {
      RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE);
      newBatch.start();
      batches.add(newBatch);
      newBatch.addSprite(sprite);
    }
  }

  public void render() {
    for (RenderBatch batch : batches) {
      batch.render();
    }
  }

}
