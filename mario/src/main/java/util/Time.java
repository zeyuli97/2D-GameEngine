package util;

public class Time {
  public static long timeStarted = System.nanoTime();

  public static double getTime() {
    // The time is in nanosecond, we need to convert to second.
    return ((System.nanoTime() - timeStarted) * 1E-9); //conversion
  }

}
