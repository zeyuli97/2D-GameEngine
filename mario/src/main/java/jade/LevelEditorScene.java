package jade;

import components.SpriteRender;
import org.joml.Vector2d;
import org.joml.Vector4d;

public class LevelEditorScene extends Scene{


  public LevelEditorScene() {

  }

  @Override
  public void init() {
    this.camera = new Camera(new Vector2d(-250, 0));

    int xOffset = 10;
    int yOffset = 10;

    double totalWidth = (600 - xOffset * 2);
    double totalHeight = (600 - yOffset * 2);

    double sizeX = totalWidth / 100;
    double sizeY = totalHeight / 100;
    double padding = 3;

    for (int x = 0; x < 100; x ++) {
      for (int y = 0 ; y < 100; y ++) {
        double xPos = xOffset + (x * sizeX) + padding * x;
        double yPose = yOffset + (y * sizeY) + padding * y;

        GameObject go = new GameObject("Obj" + x + y, new Transform(new Vector2d(xPos, yPose), new Vector2d(sizeX, sizeY)));
        go.addComponent(new SpriteRender(new Vector4d(xPos/totalWidth, yPose/totalHeight, 1, 1)));
        this.addGameToScene(go);
      }
    }

  }

  @Override
  public void update(double dt) {
    // camera is inherited from the Scene class.
    //System.out.println(Time.getTime());
    //camera.position.x -= dt * 50; // The Object is not moving, instead we are move camera in opposite direction.
    //camera.position.y -= dt * 20;

    // Note this is gameObjects the Scene List that contains all the Game Objects.
    for (GameObject go : this.gameObjects) {
      go.update(dt);
    }

    this.theRender.render();
  }

}
