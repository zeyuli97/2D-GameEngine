package components;

import jade.Camera;
import jade.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;
import renders.DebugDraw;
import util.Settings;

public class GridLines extends Component {

  @Override
  public void editorUpdate(double dt) {
    Camera camera = Window.getCurrentScene().getCamera();

    Vector2f cameraPosition = camera.getPosition();
    Vector2f projectionSize = camera.getProjectionSize();

    int firstX = ((int) (cameraPosition.x / Settings.Grid_Width) - 1) * Settings.Grid_Width;
    int firstY = ((int) (cameraPosition.y / Settings.Grid_Height) -1) * Settings.Grid_Height;

    int numVerticalLines = (int) (projectionSize.x * camera.getZoom()) / Settings.Grid_Width + 2;
    int numHorizontalLines = (int) (projectionSize.y * camera.getZoom()) / Settings.Grid_Height + 2;

    int height = (int) (projectionSize.y * camera.getZoom()) + Settings.Grid_Height;
    int width = (int) (projectionSize.x * camera.getZoom()) + Settings.Grid_Width;

    int maxLines = Math.max(numVerticalLines, numHorizontalLines);

    Vector3f color = new Vector3f(0.2f, 0.2f, 0.2f);
    for (int i = 0; i < maxLines; i++) {
      int x = firstX + (i * Settings.Grid_Width);
      int y = firstY + (i * Settings.Grid_Height);

      if (i < numVerticalLines) {
        DebugDraw.addLine2D(new Vector2f(x, firstY), new Vector2f(x, firstY + height), color);
      }

      if (i < numHorizontalLines) {
        DebugDraw.addLine2D(new Vector2f(firstX, y), new Vector2f(firstX + width, y), color);
      }
    }
  }

}
