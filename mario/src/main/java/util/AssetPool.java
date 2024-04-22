package util;

import components.Sprite;
import components.SpriteSheet;
import renders.Shader;
import renders.Texture;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * The utility class that serve the goal of resource management.
 * */
public class AssetPool {
  private static Map<String, Shader> shaders = new HashMap<>();

  private static Map<String, Texture> textures = new HashMap<>();

  private static Map<String, SpriteSheet> spriteSheets = new HashMap<>();

  /**
   * Resource manager for the shaders.
   * If shader exits, we will just return, else we store and return.
   * */
  public static Shader getShader(String resourcePath) {
    File file = new File(resourcePath);
    if (AssetPool.shaders.containsKey(file.getAbsolutePath())) {
      return AssetPool.shaders.get(file.getAbsolutePath());
    } else {
      Shader shader = new Shader(resourcePath);
      shader.compile();
      AssetPool.shaders.put(resourcePath, shader);
      return shader;
    }
  }


  /**
   * Resource manager for texture.
   * */
  public static Texture getTexture(String resourcePath) {
    File file = new File(resourcePath);
    if (AssetPool.textures.containsKey(file.getAbsolutePath())) {
      return AssetPool.textures.get(file.getAbsolutePath());
    } else {
      Texture texture = new Texture(resourcePath);
      AssetPool.textures.put(resourcePath, texture);
      return texture;
    }
  }


  public static void addSpriteSheet(String filePath, SpriteSheet spriteSheet) {
    File file = new File(filePath);
    if (!AssetPool.spriteSheets.containsKey(file.getAbsolutePath())) {
      AssetPool.spriteSheets.put(file.getAbsolutePath(), spriteSheet);
    }
  }

  public static SpriteSheet getSpriteSheet(String filePath) {
    File file = new File(filePath);
    if (!AssetPool.spriteSheets.containsKey(file.getAbsolutePath())) {
      assert false : "Error: the requested sprite sheet has not added yet.\n";
    }

    return AssetPool.spriteSheets.getOrDefault(file.getAbsolutePath(), null);
  }
}
