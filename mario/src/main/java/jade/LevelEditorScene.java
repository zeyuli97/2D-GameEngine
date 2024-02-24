package jade;

import org.lwjgl.BufferUtils;

import java.awt.event.KeyEvent;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.sql.SQLOutput;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene{
  private String vertexShaderSrc = "#version 330 core\n" +
          "layout (location = 0) in vec3 aPos;\n" +
          "layout (location = 1) in vec4 aColor;\n" +
          "\n" +
          "out vec4 fColor;\n" +
          "\n" +
          "void main() {\n" +
          "    fColor = aColor;\n" +
          "    gl_Position = vec4(aPos, 1.0);\n" +
          "}";
  private String fragmentShaderSrc = "#version 330 core\n" +
          "\n" +
          "in vec4 fColor;\n" +
          "\n" +
          "out vec4 color;\n" +
          "\n" +
          "void main() {\n" +
          "    color = fColor;\n" +
          "}";

  private int vertexID, fragmentID, shaderProgram;

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
    // We need to compile and link the shaders.

    // First, we need to load and compile the vertex shader.
    vertexID = glCreateShader(GL_VERTEX_SHADER);
    // Pass the shader source to the GPU.
    glShaderSource(vertexID, vertexShaderSrc);
    glCompileShader(vertexID);

    // Check for error in compilation.
    int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
    if (success == GL_FALSE) {
      int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
      System.out.println("Error: 'defaultShader.glsl' \n\tVertex shader compilation failed.");
      System.out.println(glGetShaderInfoLog(vertexID, len));
      assert false : "";

    }

    fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
    // Pass the shader source to the GPU.
    glShaderSource(fragmentID, fragmentShaderSrc);
    glCompileShader(fragmentID);

    // Check for error in compilation.
    success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
    if (success == GL_FALSE) {
      int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
      System.out.println("Error: 'defaultShader.glsl' \n\tFragment shader compilation failed.");
      System.out.println(glGetShaderInfoLog(fragmentID, len));
      assert false : "";

    }


    // Link shaders and check for errors
    shaderProgram = glCreateProgram();
    glAttachShader(shaderProgram, vertexID);
    glAttachShader(shaderProgram, fragmentID);
    glLinkProgram(shaderProgram);

    // Check for linking errors.
    success = glGetProgrami(shaderProgram, GL_LINK_STATUS);
    if (success == GL_FALSE) {
      int len = glGetProgrami(shaderProgram, GL_INFO_LOG_LENGTH);
      System.out.println("Error: 'defaultShader.glsl' \n\tLinking shaders failed.");
      System.out.println(glGetProgramInfoLog(shaderProgram, len));
      assert false : "";
    }

    // Generate VAO, VBO, and EBO. Send them to GPU.
    vaoID = glGenVertexArrays();
    glBindVertexArray(vaoID);

    // create a buffer of vertices
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
    // Bind shader program.
    glUseProgram(shaderProgram); // shaderProgram is an ID.
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

    glUseProgram(0); // Same.
  }

}
