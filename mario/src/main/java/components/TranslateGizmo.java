package components;

import editor.WindowProperties;
import jade.MouseListener;

public class TranslateGizmo extends Gizmo {

  public TranslateGizmo(Sprite arrowSprite, WindowProperties propertiesWindow) {
    super(arrowSprite, propertiesWindow);
  }


  @Override
  public void editorUpdate(double dt) {

    if (activeGameObject != null) {
      if (xAxisActive && !yAxisActive) {
        activeGameObject.getTransform().setPositionX(activeGameObject.getTransform().getPosition().x - MouseListener.getWorldDx());
      } else if (yAxisActive && !xAxisActive) {
        activeGameObject.getTransform().setPositionY(activeGameObject.getTransform().getPosition().y - MouseListener.getWorldDy());
      }
    }

    super.editorUpdate(dt);
  }
}
