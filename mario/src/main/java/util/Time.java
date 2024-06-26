package util;

/**
 * The time passed since the initiation of the game.
 * Start from the first time that Time class is called.
 * */
public class Time {
  public static long timeStarted = System.nanoTime();

  public static float getTime() {
    // The time is in nanosecond, we need to convert to second.
    return (float) ((System.nanoTime() - timeStarted) * 1E-9); //conversion
  }

}
