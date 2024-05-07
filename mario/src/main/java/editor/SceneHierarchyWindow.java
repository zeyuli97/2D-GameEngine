package editor;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import jade.GameObject;
import jade.Window;

import java.util.List;

public class SceneHierarchyWindow {

  private static String payloadDragDropType = "SceneHierarchy";

  public void imgui() {
    ImGui.begin("Scene Hierarchy");

    List<GameObject> gameObjectList = Window.getCurrentScene().getGameObjects();
    int index = 0;
    for (GameObject go : gameObjectList) {
      if (!go.isSERIALIZATION()) {
        continue;
      }

      ImGui.pushID(index);
      boolean treeNodeOpen = doTreeNode(go, index);
      ImGui.popID();

      if (treeNodeOpen) {
        ImGui.treePop();
      }
      index++;
    }
    ImGui.end();

  }

  private boolean doTreeNode(GameObject go, int index) {
    ImGui.pushID(index);
    boolean treeNodeOpen = ImGui.treeNodeEx(go.name,
            ImGuiTreeNodeFlags.DefaultOpen |
                    ImGuiTreeNodeFlags.FramePadding |
                    ImGuiTreeNodeFlags.OpenOnArrow |
                    ImGuiTreeNodeFlags.SpanAvailWidth, go.name);
    ImGui.popID();

    if (ImGui.beginDragDropSource()) {
      ImGui.setDragDropPayload(payloadDragDropType, go);

      ImGui.text(go.name);
      ImGui.button("This is a button");

      ImGui.endDragDropSource();
    }

    if (ImGui.beginDragDropTarget()) {
      Object payloadObj = ImGui.acceptDragDropPayload(payloadDragDropType);
      if (payloadObj != null) {
        if (payloadObj.getClass().isAssignableFrom(GameObject.class)) {
          GameObject playerGo = (GameObject) payloadObj;
          System.out.println("Payload accepted " + playerGo.name);
        }
      }
      ImGui.endDragDropTarget();
    }

    return treeNodeOpen;
  }
}
