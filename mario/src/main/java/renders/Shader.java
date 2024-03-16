package renders;


import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
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
  private boolean beingUsed = false;

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

  /**
   * Let openGl use the current shader.
   * */
  public void use() {
    if (!beingUsed) {
      // Bind shader program.
      glUseProgram(shaderProgramID); // shaderProgram is an ID.
      beingUsed = true;
    }
  }


  public void detach() {
    glUseProgram(0); // 0 means bind nothing.
    beingUsed = false;
  }

  /**
   * Upload the information to the glsl file.
   * Upload can be done only when there is a shader in use.
   * */
  public void uploadMat4d(String varName, Matrix4d mat4) {
    // Inside our linked shaders, we are matching uniform variable name with varName.
    // The mat4 will be assigned to that variable in our shaders.
    int varLocation = glGetUniformLocation(shaderProgramID, varName); // Find the location of varName inside shader.
    this.use();
    FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
    mat4.get(matBuffer); // copy mat4 into matBuffer which OpenGl could understand.
    glUniformMatrix4fv(varLocation, false, matBuffer); // upload

  }

  public void uploadMat3d(String varName, Matrix3d mat3) {
    int varLocation = glGetUniformLocation(shaderProgramID, varName);
    this.use();
    FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9);
    mat3.get(matBuffer);
    glUniformMatrix4fv(varLocation, false, matBuffer); // upload
  }

  public void uploadFloat(String varName, Float num) {
    int varLocation = glGetUniformLocation(shaderProgramID, varName);
    this.use();
    glUniform1f(varLocation, num);
  }

  public void uploadVec4d(String varName, Vector4d vec4) {
    int varLocation = glGetUniformLocation(shaderProgramID, varName);
    this.use();
    FloatBuffer vecBuffer = BufferUtils.createFloatBuffer(4);
    vec4.get(vecBuffer); // load double into float buffer works converted itself.
    glUniform4fv(varLocation, vecBuffer);
  }

  public void uploadVec3d(String varName, Vector3d vec3) {
    int varLocation = glGetUniformLocation(shaderProgramID, varName);
    this.use();
    glUniform3f(varLocation, (float) vec3.x, (float) vec3.y, (float) vec3.z);
  }

  public void uploadVec2d(String varName, Vector2d vec2) {
    int varLocation = glGetUniformLocation(shaderProgramID, varName);
    this.use();
    glUniform2f(varLocation, (float) vec2.x, (float) vec2.y);
  }

  public void uploadInt(String varName, int value) {
    int varLocation = glGetUniformLocation(shaderProgramID, varName);
    this.use();
    glUniform1i(varLocation, value);
  }

  public void uploadTexture(String varName, int slotNum) {
    int varLocation = glGetUniformLocation(shaderProgramID, varName);
    this.use();
    glUniform1i(varLocation, slotNum);
  }
}
