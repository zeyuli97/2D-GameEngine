package components;

import util.AssetPool;

public class BreakableBrick extends InteractiveBlock {
  @Override
  void playerHit(PlayerController playerController) {
    if (!playerController.isSmall()) {
      AssetPool.getSound("assets/sounds/break_block.ogg").playSound();
      gameObject.destroy();
    }
  }
}
