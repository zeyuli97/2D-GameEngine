package renders;

import components.SpriteRender;
import jade.Window;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector4d;
import org.joml.Vector4f;
import util.AssetPool;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/**
 * We are combining all the similar vertex data into a huge vertex array.
 * We all only call draw function on this huge vertex array.
 * */
public class RenderBatch implements Comparable<RenderBatch>{
  // The Vertex Array has followed information in order.
  // Pos -> RGBA -> Texture coordinates -> texture id
  // double, double          double, double, double, double,      double, double,       double.

  private final int POS_SIZE = 2;
  private final int COLOR_SIZE = 4;

  private final int POS_OFFSET = 0;

  private final int TEXTURE_COORDS_SIZE = 2;
  private final int TEXTURE_ID_SIZE = 1;
  private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Double.BYTES;

  private final int TEXTURE_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE * Double.BYTES;

  private final int TEXTURE_ID_OFFSET = TEXTURE_COORDS_OFFSET + TEXTURE_COORDS_SIZE * Double.BYTES;
  private final int VERTEX_SIZE = 9; // For each vertex we have 6 double in it
  private final int VERTEX_SIZE_BYTE = VERTEX_SIZE * Double.BYTES;

  private List<Texture> textures;

  private int[] textureSlots = {0,1,2,3,4,5,6,7};
  private SpriteRender[] sprites;
  private int numSprites;

  private boolean hasRoom;
  private double[] vertices;
  private int vaoID, vboID;
  private int maxBatchSize; // The max number of sprites we can hold in this batch.
  private Shader shader;
  private int zIndex; // zIndex is used to check the layer of sprite.

  /**
   * Constructor that takes one parameter int maxBatchSize.
   * Each batch is a quad or two triangles so total six indices per quad.
   * */
  public RenderBatch(int maxBatchSize, int zIndex) {
    this.shader = AssetPool.getShader("assets/shaders/default.glsl");
    this.maxBatchSize = maxBatchSize;
    this.sprites = new SpriteRender[maxBatchSize];

    // For each, we have 4 vertices and each vertex has 9 double values.
    vertices = new double[maxBatchSize * 4 * VERTEX_SIZE];

    this.numSprites = 0;
    this.hasRoom = true;
    this.textures = new ArrayList<>();
    this.zIndex = zIndex;
  }


  /**
   * This start method will work with vao vbo and ebo.
   * */
  public void start() {
    // Generate and bind vertex array object -- vao.
    vaoID = glGenVertexArrays();
    glBindVertexArray(vaoID);

    vboID = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, vboID);
    // Here we use dynamic draw since vertices will change.
    glBufferData(GL_ARRAY_BUFFER, (long) vertices.length * Double.BYTES, GL_DYNAMIC_DRAW);

    // Create and upload the indices
    int eboID = glGenBuffers();
    int[] indices = generateIndices();
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

    // Enable the buffer attribute pointers
    glVertexAttribPointer(0, POS_SIZE, GL_DOUBLE, false, VERTEX_SIZE_BYTE, POS_OFFSET);
    glEnableVertexAttribArray(0);

    glVertexAttribPointer(1, COLOR_SIZE, GL_DOUBLE, false, VERTEX_SIZE_BYTE, COLOR_OFFSET);
    glEnableVertexAttribArray(1);

    glVertexAttribPointer(2, TEXTURE_COORDS_SIZE, GL_DOUBLE, false, VERTEX_SIZE_BYTE, TEXTURE_COORDS_OFFSET);
    glEnableVertexAttribArray(2);

    glVertexAttribPointer(3, TEXTURE_ID_SIZE, GL_DOUBLE, false, VERTEX_SIZE_BYTE, TEXTURE_ID_OFFSET);
    glEnableVertexAttribArray(3);
  }

  public void render() {
    boolean thereIsUpdate = false;
    for (int i = 0; i < numSprites; i++) {
      SpriteRender sprite = sprites[i];
      if (sprite.isDirty()) {
        this.loadVertexProperties(i);
        sprite.setDirtyToClean();
        thereIsUpdate = true;
      }
    }
    if (thereIsUpdate) {
      // Re-buffer data if there is update occurred.
      glBindBuffer(GL_ARRAY_BUFFER, vboID);
      // Update subset (potential all data) of buffer.
      glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
    }

    // shader part.
    shader.use();

    shader.uploadMat4f("uProjection", Window.getCurrentScene().getCamera().getProjectionMatrix());
    shader.uploadMat4f("uView", Window.getCurrentScene().getCamera().getViewMatrix());

    // Bind all textures GPU could memorize multiple textures.
    for (int i = 0; i < textures.size(); i++) {
      glActiveTexture(GL_TEXTURE0 + i + 1);
      textures.get(i).bind();
    }
    shader.uploadIntArray("uTextures", textureSlots);

    glBindVertexArray(vaoID);
    glEnableVertexAttribArray(0);
    glEnableVertexAttribArray(1);

    glDrawElements(GL_TRIANGLES, this.numSprites * 6, GL_UNSIGNED_INT, 0);

    glDisableVertexAttribArray(0);
    glDisableVertexAttribArray(1);

    glBindVertexArray(0);
    for (Texture texture : textures) {
      texture.unbind();
    }
    shader.detach();
  }



  /**
   * This method will generate different sprite indices with the same structure.
   * Each sprite has four indices, and these four indices form two Triangles.
   * For indices in the array, we offset by 6 to arrive the next sprite.
   * The vertices number will increment by 4 since each sprite has four.
   * */
  private int[] generateIndices() {
    int[] elements = new int[6 * maxBatchSize];
    for (int i = 0; i < maxBatchSize; i++) {
      loadElementIndices(elements, i);
    }
    return elements;
  }

  private void loadElementIndices(int[] elements, int index) {
    int offsetArrayIndex = 6 * index;
    int offset = 4 * index;

    // Sample : 3,2,0,0,2,1            7,6,4,4,6,5
    //Triangle 1
    elements[offsetArrayIndex] = offset + 3;
    elements[offsetArrayIndex + 1] = offset + 2;
    elements[offsetArrayIndex + 2] = offset;

    // Triangle 2
    elements[offsetArrayIndex + 3] = offset;
    elements[offsetArrayIndex + 4] = offset + 2;
    elements[offsetArrayIndex + 5] = offset + 1;
  }

  /**
   * We add a SpriteRender to our Render Batch.
   * SpriteRender contains color, texture and texture coordinates.
   * We want all that information loaded into our Render Batch.
   * */
  public void addSprite(SpriteRender render) {
    // Get index and add render object
    int index = this.numSprites;
    this.sprites[index] = render;
    this.numSprites++;

    if (render.getTexture() != null) {
      if (!textures.contains(render.getTexture())) {
        textures.add(render.getTexture());
      }
    }

    // add properties to local vertices array
    loadVertexProperties(index);
    if (numSprites >= this.maxBatchSize) {
      this.hasRoom = false;
    }
  }


  private void loadVertexProperties(int index) {
    SpriteRender sprite = this.sprites[index];

    int targetTexID = 0; // I currently default to 0.
    if (sprite.getTexture() != null) {
      for (int i = 0; i < textures.size(); i++) {
        if (textures.get(i).equals(sprite.getTexture())) {
          targetTexID = i + 1;
          break;
        }
      }
    }
    // Find offset within the array
    int offset = index * 4 * VERTEX_SIZE;

    Vector4f color = sprite.getColor();
    Vector2f[] coords = sprite.getTextureCoords();

    // Add vertices with the appropriate properties.
    // (0,1)  (1,1)
    // (0,0)  (1,0)

    double xAdd = 1;
    double yAdd= 1;
    for (int i = 0; i < 4; i++) {
      if (i == 1) {
        yAdd = 0;
      } else if (i == 2) {
        xAdd = 0;
      } else if (i == 3) {
        yAdd = 1;
      }

      // update position
      vertices[offset] = sprite.getGameObject().getTransform().getPosition().x + (xAdd * sprite.getGameObject().getTransform().getScale().x);
      vertices[offset + 1] = sprite.getGameObject().getTransform().getPosition().y + (yAdd * sprite.getGameObject().getTransform().getScale().y);

      // load color
      vertices[offset + 2] = color.x;
      vertices[offset + 3] = color.y;
      vertices[offset + 4] = color.z;
      vertices[offset + 5] = color.w;

      // load texture coords
      vertices[offset + 6] = coords[i].x;
      vertices[offset + 7] = coords[i].y;

      // load texture ID
      vertices[offset + 8] = targetTexID;

      offset += VERTEX_SIZE;
    }

  }

  public boolean getHasRoom() {
    return hasRoom;
  }

  public boolean hasTextureRoom() {
    return this.textures.size() < 8;
  }

  public boolean containsTexture(Texture texture) {
    return textures.contains(texture);
  }

  public int getzIndex() {
    return zIndex;
  }

  @Override
  public int compareTo(RenderBatch o) {
    return Integer.compare(this.zIndex, o.getzIndex());
  }
}
