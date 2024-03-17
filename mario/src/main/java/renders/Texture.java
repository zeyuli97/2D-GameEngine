package renders;

import org.lwjgl.BufferUtils;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;

/**
 * Texture class that read the given file and upload to the GPU.
 * */

public class Texture {

  private int textID;

  /**
   * Constructor of the Texture class.
   * @param filePath the file path. Note check file type;
   * Allowed: JPEG, PNG, TGA, BMP, PSD, GIF, HDR, and PIC.
   * */
  public Texture(String filePath) {

    //Let openGL create texture in GPU.
    textID = glGenTextures();
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, textID);

    // set texture parameters.
    // repeat the image in both directions.
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT); // allow repeat/ wrap in x direction.
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT); // allow in y direction.

    // When stretch the image, we choose the pixelation option.
    // Not blurry no interpolation of the texture, we enlarge the pixel.
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

    // When shrinking the image, also pixelate, so we shrink the pixel.
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

    IntBuffer width = BufferUtils.createIntBuffer(1);
    IntBuffer height = BufferUtils.createIntBuffer(1);
    IntBuffer channel = BufferUtils.createIntBuffer(1); // Channel record whether pixel is RGBA or RGB.

    // This is loading the filepath file into the ByteBuffer.
    ByteBuffer image = stbi_load(filePath, width, height, channel, 0); // Allocate memory, need to free.

    if (image != null) {
      if (channel.get(0) == 3) {
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0), height.get(0),
                0, GL_RGB, GL_UNSIGNED_BYTE, image);
      } else if (channel.get(0) == 4) {
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0),
                0, GL_RGBA, GL_UNSIGNED_BYTE, image);
      } else {
        assert false : "Error: unspecified chanel number" + channel.get(0) + " for the given texture.";
      }
    } else {
      assert false : "Error: Texture file can not be loaded with path: " + filePath + "!";
    }

    // free allocated memory.
    stbi_image_free(image);
  }


  public void bind() {
    glBindTexture(GL_TEXTURE_2D, textID);
  }

  public void unbind() {
    // Bind to 0 means bind to nothing -- unbind.
    glBindTexture(GL_TEXTURE_2D, 0);
  }
}
