package components;


/**
 * Frame is a holder of the combination of sprite and its runtime.
 *
 * */
public class Frame {

  public Sprite sprite;
  public  float frameTime;

  public Frame() {

  }

  public Frame(Sprite sprite, float frameTime) {
    this.sprite = sprite;
    this.frameTime = frameTime;
  }



}
