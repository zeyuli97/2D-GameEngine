package components;

import editor.WindowProperties;
import jade.MouseListener;

public class TranslateGizmo extends Gizmo {

  public TranslateGizmo(Sprite arrowSprite, WindowProperties propertiesWindow) {
    super(arrowSprite, propertiesWindow);
  }


  @Override
  public void editorUpdate(float dt) {

    if (activeGameObject != null) {
      //System.out.println("there is an active go!");
      if (xAxisActive || yAxisActive) {
        //System.out.println("there is an active gizmo!");
        activeGameObject.getTransform().setPositionY(activeGameObject.getTransform().getPosition().y - MouseListener.getWorldDeltaY());
        activeGameObject.getTransform().setPositionX(activeGameObject.getTransform().getPosition().x - MouseListener.getWorldDeltaX());
      }


    }

    super.editorUpdate(dt);
  }
}
