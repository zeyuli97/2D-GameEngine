package renders;

import jade.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;
import util.AssetPool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class DebugDraw {
  private static int MAX_LINE = 500;

  private static List<Line2D> lines = new ArrayList<>();

  // 6 floats per vertex, 2 vertices per line
  private static float[] vertexArray = new float[MAX_LINE * 6];
  private static Shader shader = AssetPool.getShader("assets/shaders/debugLine2D.glsl");

  private static int vaoID;
  private static int vboID;

  private static boolean started = false;

  public static void start() {
    // Generate vao
    vaoID = glGenVertexArrays();
    glBindVertexArray(vaoID);

    // Create the vbo and buffer some memory
    vboID = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, vboID);
    glBufferData(GL_ARRAY_BUFFER, (long) vertexArray.length * Float.BYTES, GL_DYNAMIC_DRAW);

    // Enable the vertex array attributes
    glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
    glEnableVertexAttribArray(0);

    glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
    glEnableVertexAttribArray(1);

    glLineWidth(200.0f);
  }

  public static void beginFrame() {
    if (!started) {
      start();
      started = true;
    }

    // Remove the Line2D with lifetime less than 0
    for (int i = 0; i < lines.size(); i++) {
      if (lines.get(i).beginFrame() <= 0) {
        lines.remove(i);
        i--;
      }
    }
  }

  public static void draw() {
    if (lines.isEmpty()) {
      return ;
    }

    int index = 0;
    for (Line2D line : lines) {
      for (int i = 0; i < 2; i++) {
        // This is positioned equals depend on whether i == 0, if yes, line.getStart else line.getEnd.
        Vector2f position = i == 0 ? line.getStart() : line.getEnd();
        Vector3f color = line.getColor();

        // We load the position.
        vertexArray[index] = position.x;
        vertexArray[index + 1] = position.y;
        vertexArray[index + 2] = -1; // we do not have z coord for 2D, I default z at -1.

        // We load the color.
        vertexArray[index + 3] = color.x;
        vertexArray[index + 4] = color.y;
        vertexArray[index + 5] = color.z;

        index += 6;
      }
    }

    glBindBuffer(GL_ARRAY_BUFFER, vboID);
    glBufferSubData(GL_ARRAY_BUFFER, 0, Arrays.copyOfRange(vertexArray, 0, lines.size() * 6 * 2));

    shader.use();
    shader.uploadMat4d("uProjection", Window.getCurrentScene().getCamera().getProjectionMatrix());
    shader.uploadMat4d("uView", Window.getCurrentScene().getCamera().getViewMatrix());

    glBindVertexArray(vaoID);
    glEnableVertexAttribArray(0);
    glEnableVertexAttribArray(1);

    // Draw batch
    glDrawArrays(GL_LINES, 0, lines.size() * 6 * 2);


    glDisableVertexAttribArray(0);
    glDisableVertexAttribArray(1);

    glBindVertexArray(0);

    shader.detach();
  }

  public static void addLine2D(Vector2f start, Vector2f end) {
    // Todo: ADD constants for common colors.
    addLine2D(start, end, new Vector3f(0, 1, 0), 1);
  }

  public static void addLine2D(Vector2f start, Vector2f end, Vector3f color) {
    addLine2D(start, end, color, 1);
  }

  public static void addLine2D(Vector2f start, Vector2f end, Vector3f color, int lifetime) {
    if (lines.size() >= MAX_LINE) {
      return;
    }
    DebugDraw.lines.add(new Line2D(start, end, color, lifetime));
  }

}
