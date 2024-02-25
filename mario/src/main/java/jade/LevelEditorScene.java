package jade;

import org.lwjgl.BufferUtils;
import renders.Shader;

import java.awt.event.KeyEvent;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.sql.SQLOutput;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene{
  private Shader defaultShader;

  private double[] vertexArray = {
          // Position(xyz)              //and then Color(rgba).
          0.5, -0.5, 0.0,               1.0, 0.0, 0.0, 1.0, // Bottom right. Treat as 0.
          -0.5, 0.5, 0.0,               0.0, 1.0, 0.0, 1.0, // Top left.              1.
          0.5, 0.5, 0.0,                0.0, 0.0, 1.0, 1.0, // Top right.             2.
          -0.5, -0.5, 0.0,              1.0, 1.0, 0.0, 1.0 // Bottom left.            3.
  };

  // This must be in the counterclockwise order. This is important when describe the shape!!!
  private int[] elementArray = {
          // for counterclockwise
          2,1,0, // Top right triangle
          0,1,3 // Bottom left triangle
  };

  private int vaoID, vboID, eboID;

  public LevelEditorScene() {

  }

  @Override
  public void init() {
    defaultShader = new Shader("assets/shaders/default.glsl");
    defaultShader.compile();

    // Generate VAO, VBO, and EBO. Send them to GPU.
    vaoID = glGenVertexArrays();
    glBindVertexArray(vaoID); // VAO is like a manager for VBO and EBO.


    // create a buffer of vertices and store into VBO.
    DoubleBuffer vertexBuffer = BufferUtils.createDoubleBuffer(vertexArray.length);
    vertexBuffer.put(vertexArray); // writen mode.
    vertexBuffer.flip(); // flip into read mode.
    // Create VBO upload the vertex buffer.
    vboID = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, vboID);
    glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

    // Create the indices and upload.
    IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
    elementBuffer.put(elementArray);
    elementBuffer.flip();

    eboID = glGenBuffers();
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

    // Add the vertex attribute pointers.
    int positionSize = 3; // xyz
    int colorSize = 4; // rgba
    int SizeBytesDouble = Double.BYTES; // size of each data
    int vertexSizeBytes = (positionSize + colorSize) * SizeBytesDouble; // size of stride for the next vertex.

    // In our default.glsl file, we have 0 as position and 1 as color.
    glVertexAttribPointer(0, positionSize, GL_DOUBLE, false, vertexSizeBytes, 0);
    glEnableVertexAttribArray(0);

    glVertexAttribPointer(1, colorSize, GL_DOUBLE, false, vertexSizeBytes, positionSize * SizeBytesDouble);
    glEnableVertexAttribArray(1);
  }

  @Override
  public void update(double dt) {
    defaultShader.use();
    // Bind the VAO that we are using.
    glBindVertexArray(vaoID);

    // Enable the vertex attribute pointers
    glEnableVertexAttribArray(0);
    glEnableVertexAttribArray(1);

    glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);


    // Unbind everything
    glDisableVertexAttribArray(0);
    glDisableVertexAttribArray(1);

    glBindVertexArray(0); // 0 means bind nothing.
    defaultShader.detach();
  }

}
