package renders;

import org.joml.Vector2i;

import static org.lwjgl.opengl.GL30.*;

/**
 * Since each game object and component have uid -- unique.
 * We use uid as the color value to framebuffer.
 * When a user picks a component at a specific location, we get the color information
 *  at the click position. This will be the uid of component, so we know what user picked.
 * */
public class PickingTexture {
  private int pickingTextureID;
  private int fbo;
  private int depthTexture;

  public PickingTexture(int width, int height) {
    if (!init(width, height)) {
      assert false : "Error initializing PickingTexture failed.";
    }
  }

  public boolean init(int width, int height) {
    fbo = glGenFramebuffers();
    glBindFramebuffer(GL_FRAMEBUFFER, fbo);

    pickingTextureID = glGenTextures();
    glBindTexture(GL_TEXTURE_2D, pickingTextureID);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
    //gl_nearest is crucial here, since we want an accurate picking id.
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB32F, width, height, 0, GL_RGB, GL_FLOAT, 0);

    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, pickingTextureID, 0);

    // Create the texture object for the depth buffer.
    glEnable(GL_DEPTH_TEST);
    depthTexture = glGenTextures();
    glBindTexture(GL_TEXTURE_2D, depthTexture);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, 0);
    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTexture, 0);

    glReadBuffer(GL_NONE);
    glDrawBuffer(GL_COLOR_ATTACHMENT0);

    if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
      assert false : "Error during framebuffer reading.";
      return false;
    }

    // unbind
    glBindFramebuffer(GL_TEXTURE_2D, 0);
    glBindFramebuffer(GL_FRAMEBUFFER, 0);
    glDisable(GL_DEPTH_TEST);
    return true;
  }



  public void enableWriting() {
    glBindFramebuffer(GL_DRAW_FRAMEBUFFER, fbo);
  }

  public void disableWriting() {
    glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
  }

  public int readPixel(int x, int y) {
    glBindFramebuffer(GL_READ_FRAMEBUFFER, fbo);
    glReadBuffer(GL_COLOR_ATTACHMENT0);

    float[] pixels = new float[3];
    glReadPixels(x, y, 1, 1, GL_RGB, GL_FLOAT, pixels);

    return  (int) pixels[0] - 1;
  }

  public float[] readMulPixels(Vector2i start, Vector2i end) {
    glBindFramebuffer(GL_READ_FRAMEBUFFER, fbo);
    glReadBuffer(GL_COLOR_ATTACHMENT0);

    Vector2i size = new Vector2i(end).sub(start).absolute();
    int numPixels = size.x * size.y;
    float[] pixels = new float[numPixels * 3];
    glReadPixels(start.x, start.y, size.x, size.y, GL_RGB, GL_FLOAT, pixels);

    for (int i = 0; i < pixels.length; i++) {
      pixels[i] = pixels[i] - 1;
    }

    return pixels;

  }
}
