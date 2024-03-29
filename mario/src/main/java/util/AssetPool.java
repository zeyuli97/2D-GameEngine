package util;

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
}
