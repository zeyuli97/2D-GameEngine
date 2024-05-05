package components;

import editor.WindowProperties;
import jade.GameObject;
import jade.Prefabs;
import jade.Window;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class TranslateGizmo extends Component {
  private Vector4f xAxisColor = new Vector4f(1,0,0,1);
  private Vector4f yAxisColor = new Vector4f(0,1,0,1);
  private Vector4f xAxisColorHover = new Vector4f();
  private Vector4f yAxisColorHover = new Vector4f();

  private Vector2f xAxisOffset = new Vector2f(61, 9);
  private Vector2f yAxisOffset = new Vector2f(22, 64);

  private GameObject xAxisObject;
  private GameObject yAxisObject;
  private SpriteRender xAxisSprite;
  private SpriteRender yAxisSprite;

  private WindowProperties windowProperties;

  private GameObject activeGameObject = null;

  public TranslateGizmo(Sprite arrowSprite, WindowProperties propertiesWindow) {
    this.xAxisObject = Prefabs.generateSpriteWithinGameObject(arrowSprite, 16, 48);
    this.yAxisObject = Prefabs.generateSpriteWithinGameObject(arrowSprite, 16, 48);
    this.xAxisSprite = this.xAxisObject.getComponent(SpriteRender.class);
    this.yAxisSprite = this.yAxisObject.getComponent(SpriteRender.class);

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
    if (activeGameObject != null) {
      this.xAxisObject.getTransform().setPosition(new Vector2f(this.activeGameObject.getTransform().getPosition()).add(xAxisOffset));
      this.yAxisObject.getTransform().setPosition(new Vector2f(this.activeGameObject.getTransform().getPosition()).add(yAxisOffset));
      //System.out.println(this.activeGameObject.getTransform().getPosition() + "   " + this.xAxisObject.getTransform().getPosition());
    }

    this.activeGameObject = this.windowProperties.getActiveGameObject();

    if (this.activeGameObject != null) {
      this.setActive();
    } else {
      this.setInActive();
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

}
