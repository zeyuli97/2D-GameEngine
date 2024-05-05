package components;

import jade.KeyListerner;
import jade.Window;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_T;

public class GizmoSystem extends Component {
  private SpriteSheet gizmoSheet;
  private int usingGizmo = 0;
  
  public GizmoSystem(SpriteSheet gizmoSheet) {
    this.gizmoSheet = gizmoSheet;
    
  }
  
  @Override
  public void start() {
    gameObject.addComponent(new TranslateGizmo(gizmoSheet.getSprite(1),
            Window.getImGuiLayer().getWindowProperties()));
    gameObject.addComponent(new ScaleGizmo(gizmoSheet.getSprite(2), Window.getImGuiLayer().getWindowProperties()));
  }
  
  
  @Override
  public void update(double dt) {
    if (usingGizmo == 0) {
      gameObject.getComponent(TranslateGizmo.class).setUsing();
      gameObject.getComponent(ScaleGizmo.class).setNotUsing();
    } else if (usingGizmo == 1){
      gameObject.getComponent(ScaleGizmo.class).setUsing();
      gameObject.getComponent(TranslateGizmo.class).setNotUsing();
    }
    
    if (KeyListerner.isKeyPressed(GLFW_KEY_T)) {
      usingGizmo = 0;
    } else if (KeyListerner.isKeyPressed(GLFW_KEY_S)) {
      usingGizmo = 1;
    }
  }
}
