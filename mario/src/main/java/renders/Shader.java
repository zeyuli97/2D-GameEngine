package renders;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

/**
 * This shader class is trying to apply regular expression so that we do not need to repeat
 * ourselves over and over. This file will help to handle the vertex shader and the fragment shader.
 * */
public class Shader {

  private int shaderProgramID;

  private String vertexSrc;
  private String fragmentSrc;
  private String filepath;

  public Shader(String filepath) {
    this.filepath = filepath;
    try {
      String source = new String(Files.readAllBytes(Paths.get(filepath)));
      String[] spliteString = source.split("(#type)( )+([a-zA-Z]+)"); // Using regular expression to split shader file.

      int index = source.indexOf("#type") + 6;
      int eol = source.indexOf("\n", index); // index here means the starting search index.
      String firstPattern = source.substring(index, eol).trim(); // Remove all the white space.

      index = source.indexOf("#type", eol) + 6;
      eol = source.indexOf("\n", index);
      String secondPattern = source.substring(index, eol).trim();

      // If a source starts with #type something, splitString[0] will be "". So, we do not want splitString[0] in all cases.
      if (firstPattern.equals("vertex")) {
        vertexSrc = spliteString[1];
      } else if (firstPattern.equals("fragment")) {
        fragmentSrc = spliteString[1];
      } else {
        throw new IOException("Unexpected token '" + firstPattern + "'.");
      }
      if (secondPattern.equals("vertex")) {
        vertexSrc = spliteString[2];
      } else if (secondPattern.equals("fragment")) {
        fragmentSrc = spliteString[2];
      } else {
        throw new IOException("Unexpected token '" + secondPattern + "'.");
      }
    } catch (IOException e) {
      e.printStackTrace();
      assert false : "Error: Could not open file for shader: '" + filepath + "'";
    }

    System.out.println(vertexSrc);
    System.out.println(fragmentSrc);

  }

  public void compile() {
    int vertexID, fragmentID;
    // We need to compile and link the shaders.
    // First, we need to load and compile the vertex shader.
    vertexID = glCreateShader(GL_VERTEX_SHADER);
    // Pass the shader source to the GPU.
    glShaderSource(vertexID, vertexSrc);
    glCompileShader(vertexID);

    // Check for error in compilation.
    int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
    if (success == GL_FALSE) {
      int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
      System.out.println("Error: " + filepath + " \n\tVertex shader compilation failed.");
      System.out.println(glGetShaderInfoLog(vertexID, len));
      assert false : "";

    }

    fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
    // Pass the shader source to the GPU.
    glShaderSource(fragmentID, fragmentSrc);
    glCompileShader(fragmentID);

    // Check for error in compilation.
    success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
    if (success == GL_FALSE) {
      int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
      System.out.println("Error: '" + filepath + "' \n\tFragment shader compilation failed.");
      System.out.println(glGetShaderInfoLog(fragmentID, len));
      assert false : "";

    }

    // Link shaders and check for errors
    shaderProgramID = glCreateProgram();
    glAttachShader(shaderProgramID, vertexID);
    glAttachShader(shaderProgramID, fragmentID);
    glLinkProgram(shaderProgramID);

    // Check for linking errors.
    success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
    if (success == GL_FALSE) {
      int len = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
      System.out.println("Error: '" + filepath + "' \n\tLinking shaders failed.");
      System.out.println(glGetProgramInfoLog(shaderProgramID, len));
      assert false : "";
    }
  }

  public void use() {
    // Bind shader program.
    glUseProgram(shaderProgramID); // shaderProgram is an ID.
  }


  public void detach() {
    glUseProgram(0); // 0 means bind nothing.
  }
}
