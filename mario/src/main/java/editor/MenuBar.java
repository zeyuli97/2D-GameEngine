package editor;

import Observers.EventSystem;
import Observers.Events.Event;
import Observers.Events.EventType;
import imgui.ImGui;

public class MenuBar {

  public void imgui() {
    ImGui.beginMenuBar();

    if (ImGui.beginMenu("File")) {
      if (ImGui.menuItem("Save", "Command + s")) {
        EventSystem.notify(null, new Event(EventType.SaveLevel));
      }
      if (ImGui.menuItem("Load", "Command + l")) {
        EventSystem.notify(null, new Event(EventType.LoadLevel));
      }

      ImGui.endMenu();
    }

    ImGui.endMenuBar();

  }
}
