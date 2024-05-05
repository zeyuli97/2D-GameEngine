package components;

import editor.WindowProperties;
import jade.GameObject;
import jade.MouseListener;
import jade.Prefabs;
import jade.Window;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class Gizmo extends Component {
  private Vector4f xAxisColor = new Vector4f(1,0.3f,0.3f,1);
  private Vector4f yAxisColor = new Vector4f(0.3f,1,0.3f,1);

  private Vector4f xAxisColorHover = new Vector4f(1,0,0,1);
  private Vector4f yAxisColorHover = new Vector4f(0,1,0,1);

  private Vector2f xAxisOffset = new Vector2f(61, 9);
  private Vector2f yAxisOffset = new Vector2f(22, 64);

  private GameObject xAxisObject;
  private GameObject yAxisObject;

  private SpriteRender xAxisSprite;
  private SpriteRender yAxisSprite;

  protected boolean xAxisActive = false;
  protected boolean yAxisActive = false;

  private WindowProperties windowProperties;

  protected GameObject activeGameObject = null;

  private int gizmoWidth, gizmoHeight;

  private boolean using = false;

  public Gizmo(Sprite arrowSprite, WindowProperties propertiesWindow) {
    this.xAxisObject = Prefabs.generateSpriteWithinGameObject(arrowSprite, 16, 48);
    this.yAxisObject = Prefabs.generateSpriteWithinGameObject(arrowSprite, 16, 48);

    xAxisObject.addComponent(new NonActiveGameObjectClass());
    yAxisObject.addComponent(new NonActiveGameObjectClass());

    this.xAxisSprite = this.xAxisObject.getComponent(SpriteRender.class);
    this.yAxisSprite = this.yAxisObject.getComponent(SpriteRender.class);
    gizmoWidth = 16;
    gizmoHeight = 48;


    this.windowProperties = propertiesWindow;

    Window.getCurrentScene().addGameObjectToScene(this.xAxisObject);
    Window.getCurrentScene().addGameObjectToScene(this.yAxisObject);
  }

  @Override
  public void start() {
    this.xAxisObject.getTransform().setRotation(90);
    this.yAxisObject.getTransform().setRotation(180);
    this.xAxisObject.setNoSerialize();
    this.yAxisObject.setNoSerialize();
  }

  @Override
  public void update(double dt) {
    if (!using) {
      return;
    }

    this.activeGameObject = this.windowProperties.getActiveGameObject();

    if (this.activeGameObject != null) {
      this.setActive();
    } else {
      this.setInActive();
      return;
    }

    boolean xAxisHot = checkXHoverState();
    boolean yAxisHot = checkYHoverState();

    if ((xAxisHot || xAxisActive) && MouseListener.getIsDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
      xAxisActive = true;
      yAxisActive = false;
    } else if ((yAxisHot || yAxisActive) && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && MouseListener.getIsDragging()) {
      yAxisActive = true;
      xAxisActive = false;
    } else {
      xAxisActive = false;
      yAxisActive = false;
    }

    if (activeGameObject != null) {
      this.xAxisObject.getTransform().setPosition(new Vector2f(this.activeGameObject.getTransform().getPosition()).add(xAxisOffset));
      this.yAxisObject.getTransform().setPosition(new Vector2f(this.activeGameObject.getTransform().getPosition()).add(yAxisOffset));
      //System.out.println(this.activeGameObject.getTransform().getPosition() + "   " + this.xAxisObject.getTransform().getPosition());
    }
  }

  private void setActive() {
    this.xAxisSprite.setColor(xAxisColor);
    this.yAxisSprite.setColor(yAxisColor);
  }

  private void setInActive() {
    this.activeGameObject = null;
    this.xAxisSprite.setColor(new Vector4f(0,0,0,0));
    this.yAxisSprite.setColor(new Vector4f(0,0,0,0));
  }

  private boolean checkXHoverState() {
    Vector2f mousePos = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());
    if (mousePos.x <= xAxisObject.getTransform().getPosition().x
            && mousePos.x >= xAxisObject.getTransform().getPosition().x - gizmoHeight
            && mousePos.y >= xAxisObject.getTransform().getPosition().y
            && mousePos.y <= xAxisObject.getTransform().getPosition().y + gizmoWidth) {
      xAxisSprite.setColor(xAxisColorHover);
      return true;
    }
    xAxisSprite.setColor(xAxisColor);
    return false;
  }

  private boolean checkYHoverState() {
    Vector2f mousePos = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());
    if (mousePos.x <= yAxisObject.getTransform().getPosition().x
            && mousePos.x >= yAxisObject.getTransform().getPosition().x - gizmoWidth
            && mousePos.y <= yAxisObject.getTransform().getPosition().y
            && mousePos.y >= yAxisObject.getTransform().getPosition().y - gizmoHeight) {
      yAxisSprite.setColor(yAxisColorHover);
      return true;
    }
    yAxisSprite.setColor(yAxisColor);
    return false;
  }


  public void setUsing() {
    this.using = true;
  }

  public void setNotUsing() {
    this.using = false;
    this.setInActive();
  }
}
