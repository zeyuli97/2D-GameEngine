package editor;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import jade.MouseListener;
import jade.Window;
import org.joml.Vector2f;

public class GameViewWindow {

  private float leftX, rightX, topY, bottomY;

  public void imgui() {
    ImGui.begin("Game Viewport", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);

    ImVec2 windowSize = getLargestSizeForViewport();
    ImVec2 windowPos = getCenteredPositionForViewport(windowSize);

    ImGui.setCursorPos(windowPos.x, windowPos.y);

    ImVec2 topLeft = new ImVec2();
    ImGui.getCursorScreenPos(topLeft);
    topLeft.x -= ImGui.getScrollX();
    topLeft.y -= ImGui.getScrollY();
    leftX = topLeft.x;
    topY = topLeft.y + windowSize.y;
    bottomY = topLeft.y;
    rightX = topLeft.x + windowSize.x;

    int textureID = Window.getFrameBuffer().getTexture().getTextID();
    ImGui.image(textureID, windowSize.x, windowSize.y, 0, 1, 1, 0);

    //System.out.println(topLeft.x + " " + topLeft.y + " " + windowSize.x + " " + windowSize.y);

    MouseListener.setGameViewportPos(new Vector2f(topLeft.x, topLeft.y));
    MouseListener.setGameViewportSize(new Vector2f(windowSize.x, windowSize.y));

    ImGui.end();
  }

  public boolean getWantCaptureMouse() {
    return MouseListener.getxPos() >= leftX && MouseListener.getxPos() <= rightX
            && MouseListener.getyPos() >= bottomY && MouseListener.getyPos() <= topY;
  }

  private ImVec2 getLargestSizeForViewport() {
    ImVec2 windowSize = new ImVec2();
    ImGui.getContentRegionAvail(windowSize);
    windowSize.x -= ImGui.getScrollX();
    windowSize.y -= ImGui.getScrollY();

    float aspectWidth = windowSize.x;
    float aspectHeight = aspectWidth / Window.getTargetAspectRatio(); // 16 : 9

    if (aspectHeight > windowSize.y) {
      aspectHeight = windowSize.y;
      aspectWidth = aspectHeight * Window.getTargetAspectRatio();
    }

    return new ImVec2(aspectWidth, aspectHeight);
  }

  private ImVec2 getCenteredPositionForViewport(ImVec2 aspectSize) {
    ImVec2 windowSize = new ImVec2();
    ImGui.getContentRegionAvail(windowSize);
    windowSize.x -= ImGui.getScrollX();
    windowSize.y -= ImGui.getScrollY();

    float viewportWidth = (windowSize.x / 2.0f) - (aspectSize.x / 2.f);
    float viewportHeight = (windowSize.y / 2.0f) - (aspectSize.y / 2.f);

    return new ImVec2(viewportWidth + ImGui.getCursorPosX(), viewportHeight + ImGui.getCursorPosY());
  }

}
