package jade;

import org.joml.Vector2d;
import org.lwjgl.BufferUtils;
import renders.Shader;
import renders.Texture;
import util.Time;

import java.awt.event.KeyEvent;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene{
  private Shader defaultShader;

  private Texture texture;

  private double[] vertexArray = {
          // Position(xyz)              //and Color(rgba).            // UV Coordinate
          100.5, 0.5, 0.0,               1.0, 0.0, 0.0, 1.0,          0, 1,          // Bottom right. Treat as 0.
          0.5, 100.5, 0.0,               0.0, 1.0, 0.0, 1.0,          1, 0,          // Top left.              1.
          100.5, 100.5, 0.0,             0.0, 0.0, 1.0, 1.0,          0, 0,          // Top right.             2.
          0.5, 0.5, 0.0,                 1.0, 1.0, 0.0, 1.0,          1, 1           // Bottom left.           3.
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
    //this.camera = new Camera(new Vector2d()); // Vector init to 0.
    defaultShader = new Shader("assets/shaders/default.glsl");
    texture = new Texture("assets/images/pixelMario.png");
    defaultShader.compile();

    // Generate VAO, VBO, and EBO. Send them to GPU.
    vaoID = glGenVertexArrays();
    // All future operations should use this VAO's settings and buffers.
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
    int uvSize = 2; // UV coordinate size
    int vertexSizeBytes = (positionSize + colorSize + uvSize) * Double.BYTES; // size of stride for the next vertex.

    // In our default.glsl file, we have 0 as position and 1 as color.
    glVertexAttribPointer(0, positionSize, GL_DOUBLE, false, vertexSizeBytes, 0);
    glEnableVertexAttribArray(0); // This allows vertex data be accessed by Vertex shader.

    glVertexAttribPointer(1, colorSize, GL_DOUBLE, false, vertexSizeBytes, positionSize * Double.BYTES);
    glEnableVertexAttribArray(1);

    glVertexAttribPointer(2, uvSize, GL_DOUBLE, false, vertexSizeBytes, (positionSize + colorSize) * Double.BYTES);
    glEnableVertexAttribArray(2);
  }

  @Override
  public void update(double dt) {
    // camera is inherited from the Scene class.
    //System.out.println(Time.getTime());
    camera.position.x -= dt * 50; // The Object is not moving, instead we are move camera in opposite direction.
    camera.position.y -= dt * 20;

    defaultShader.use();

    // Upload texture to shader.
    defaultShader.uploadTexture("textureSampler", 0);
    //glActiveTexture(GL_TEXTURE0);
    //texture.bind();
    defaultShader.uploadMat4d("uProjection", camera.getProjectionMatrix());
    defaultShader.uploadMat4d("uView", camera.getViewMatrix());
    defaultShader.uploadFloat("uTime", (float) Time.getTime());
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
