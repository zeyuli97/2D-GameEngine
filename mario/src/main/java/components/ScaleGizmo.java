package components;

import editor.WindowProperties;
import jade.MouseListener;

public class ScaleGizmo extends Gizmo{
  public ScaleGizmo(Sprite sprite, WindowProperties window) {
    super(sprite, window);
  }

  @Override
  public void editorUpdate(double dt) {
    if (activeGameObject != null) {
      if (xAxisActive && !yAxisActive) {
        activeGameObject.getTransform().setScaleX(activeGameObject.getTransform().getScale().x - MouseListener.getWorldDx());
      } else if (yAxisActive) {
        activeGameObject.getTransform().setScaleY(activeGameObject.getTransform().getScale().y - MouseListener.getWorldDy());
      }
    }

    super.editorUpdate(dt);
  }
}
