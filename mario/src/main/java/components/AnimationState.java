package components;

import util.AssetPool;

import java.util.ArrayList;
import java.util.List;

/**
 * AnimationState is the holder of all the frame to form an animation.
 * */
public class AnimationState extends Component {
  public String title;

  public List<Frame> animationFrames = new ArrayList<>();

  private static Sprite defaultSprite = new Sprite();
  private transient float timeTracker = 0f;
  private transient int currentSprite = 0;
  public boolean doesLoop = false;



  public void addFrame(Sprite sprite, float frameTime) {
    animationFrames.add(new Frame(sprite, frameTime));
  }

  public void refreshTexture() {
    for (Frame frame : animationFrames) {
      frame.sprite.setTexture(AssetPool.getTexture(frame.sprite.getTexture().getFilePath()));
    }
  }

  public void update(double dt) {
    if (currentSprite < animationFrames.size()) {
      timeTracker -= dt;
      if (timeTracker <= 0) {
        if ((currentSprite != animationFrames.size() - 1)  || doesLoop) {
          currentSprite = (currentSprite + 1) % animationFrames.size();
        }

        timeTracker = animationFrames.get(currentSprite).frameTime;
      }
    }
  }

  public Sprite getCurrentSprite() {
    if (currentSprite < animationFrames.size()) {
      return animationFrames.get(currentSprite).sprite;
    }
    return defaultSprite;
  }
}
