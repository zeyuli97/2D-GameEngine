package renders;

import static org.lwjgl.opengl.GL30.*;

public class FrameBuffer {

  private int fboID = 0;

  private Texture texture = null;

  public FrameBuffer(int width, int height) {
    // Generate frameBuffer

    fboID = glGenFramebuffers();
    glBindFramebuffer(GL_FRAMEBUFFER, fboID);

    // Create the texture to render the data to, and attach it to our framebuffer.
    this.texture = new Texture(width, height);
    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.texture.getTextID(), 0);

    // Renderbuffer stores depth data.
    int rboID = glGenRenderbuffers();
    glBindRenderbuffer(GL_RENDERBUFFER, rboID);
    glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT32, width, height);

    glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rboID);

    if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
      assert false : "Frame buffer is not complete.";
    }

    glBindFramebuffer(GL_FRAMEBUFFER, 0);
  }

  public int getFboID() {
    return fboID;
  }

  public Texture getTexture() {
    return texture;
  }

  public void bind() {
    glBindFramebuffer(GL_FRAMEBUFFER, fboID);
  }

  public void unbind() {
    glBindFramebuffer(GL_FRAMEBUFFER, 0);
  }
}
