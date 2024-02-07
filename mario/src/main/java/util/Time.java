package util;

public class Time {
  public static long timeStarted = System.nanoTime();

  public static float getTime() {
    // The time is in nanosecond, we need to convert to second.
    return (float) ((System.nanoTime() - timeStarted) * 1E-9); //conversion
  }



}
