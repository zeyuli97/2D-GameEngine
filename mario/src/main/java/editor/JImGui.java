package editor;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class JImGui {

  private static float columnWidthDefault = 110f;

  public static void drawVector2Control(String label, Vector2f values) {
    drawVector2Control(label, values, 0f, columnWidthDefault);
  }

  public static void drawVector2Control(String label, Vector2f values, float resetValue) {
    drawVector2Control(label, values, resetValue, columnWidthDefault);
  }

  public static void drawVector2Control(String label, Vector2f values, float resetValue, float columnWidth) {
    ImGui.pushID(label);

    ImGui.columns(2);
    ImGui.setColumnWidth(0, columnWidth);
    ImGui.text(label);
    ImGui.nextColumn();

    ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 0);

    float lineHeight = ImGui.getFontSize() + ImGui.getStyle().getFramePaddingY() * 2f;
    Vector2f buttonSize = new Vector2f(lineHeight + 3f, lineHeight);
    float widthEach = (ImGui.calcItemWidth() - buttonSize.x * 2f) / 2f;

    ImGui.pushStyleColor(ImGuiCol.Button, 0.8f, 0.1f, 0.15f, 1f);
    ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.9f, 0.2f, 0.2f, 1f);
    ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.8f, 0.1f, 0.15f, 1f);
    ImGui.pushItemWidth(widthEach);
    if (ImGui.button("X", buttonSize.x, buttonSize.y)) {
      values.x = resetValue;
    }
    ImGui.popStyleColor(3);

    ImGui.sameLine();
    float[] vecValuesX = {values.x};
    ImGui.dragFloat("##X", vecValuesX, 0.1f);
    ImGui.popItemWidth();
    ImGui.sameLine();


    ImGui.pushStyleColor(ImGuiCol.Button, 0.2f, 0.7f, 0.2f, 1f);
    ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.3f, 0.8f, 0.3f, 1f);
    ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.2f, 0.7f, 0.2f, 1f);
    ImGui.pushItemWidth(widthEach);
    if (ImGui.button("Y", buttonSize.x, buttonSize.y)) {
      values.y = resetValue;
    }

    ImGui.sameLine();
    float[] vecValuesY = {values.y};
    ImGui.dragFloat("##Y", vecValuesY, 0.1f);
    ImGui.popItemWidth();
    ImGui.popStyleColor(3);

    ImGui.nextColumn();

    values.x = vecValuesX[0];
    values.y = vecValuesY[0];

    ImGui.popStyleVar();
    ImGui.columns(1);
    ImGui.popID();
  }

  public static void drawVector3Control(String label, Vector3f values) {
    drawVector3Control(label, values, 0f, columnWidthDefault);
  }

  public static void drawVector3Control(String label, Vector3f values, float resetValue) {
    drawVector3Control(label, values, resetValue, columnWidthDefault);
  }

  public static void drawVector3Control(String label, Vector3f values, float resetValue, float columnWidth) {
    ImGui.pushID(label);

    ImGui.columns(2);
    ImGui.setColumnWidth(0, columnWidth);
    ImGui.text(label);
    ImGui.nextColumn();

    ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 0);

    float lineHeight = ImGui.getFontSize() + ImGui.getStyle().getFramePaddingY() * 2.0f;
    Vector2f buttonSize = new Vector2f(lineHeight + 3.0f, lineHeight);
    float widthEach = (ImGui.calcItemWidth() - buttonSize.x * 3.0f) / 3.0f;

    ImGui.pushItemWidth(widthEach);
    ImGui.pushStyleColor(ImGuiCol.Button, 0.8f, 0.1f, 0.15f, 1.0f);
    ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.9f, 0.2f, 0.2f, 1.0f);
    ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.8f, 0.1f, 0.15f, 1.0f);
    if (ImGui.button("X", buttonSize.x, buttonSize.y)) {
      values.x = resetValue;
    }
    ImGui.popStyleColor(3);

    ImGui.sameLine();
    float[] vecValuesX = {values.x};
    ImGui.dragFloat("##X", vecValuesX, 0.1f);
    ImGui.popItemWidth();
    ImGui.sameLine();

    ImGui.pushItemWidth(widthEach);
    ImGui.pushStyleColor(ImGuiCol.Button, 0.2f, 0.7f, 0.2f, 1.0f);
    ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.3f, 0.8f, 0.3f, 1.0f);
    ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.2f, 0.7f, 0.2f, 1.0f);
    if (ImGui.button("Y", buttonSize.x, buttonSize.y)) {
      values.y = resetValue;
    }
    ImGui.popStyleColor(3);

    ImGui.sameLine();
    float[] vecValuesY = {values.y};
    ImGui.dragFloat("##Y", vecValuesY, 0.1f);
    ImGui.popItemWidth();
    ImGui.columns(1);
    ImGui.sameLine();

    ImGui.pushItemWidth(widthEach);
    ImGui.pushStyleColor(ImGuiCol.Button, 0.1f, 0.25f, 0.8f, 1.0f);
    ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.2f, 0.35f, 0.9f, 1.0f);
    ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.1f, 0.25f, 0.8f, 1.0f);
    if (ImGui.button("Z", buttonSize.x, buttonSize.y)) {
      values.z = resetValue;
    }
    ImGui.popStyleColor(3);

    ImGui.sameLine();
    float[] vecValuesZ = {values.z};
    ImGui.dragFloat("##Z", vecValuesZ, 0.1f);
    ImGui.popItemWidth();
    ImGui.columns(1);

    values.x = vecValuesX[0];
    values.y = vecValuesY[0];
    values.z = vecValuesZ[0];

    ImGui.popStyleVar();
    ImGui.popID();
  }

  public static float dragFloat(String label, float value) {
    ImGui.pushID(label);

    ImGui.columns(2);
    ImGui.setColumnWidth(0, columnWidthDefault);
    ImGui.text(label);
    ImGui.nextColumn();

    float[] valArray = {value};
    ImGui.dragFloat("##dragFloat", valArray, 0.1f);

    ImGui.columns(1);
    ImGui.popID();

    return valArray[0];
  }

  public static int dragInt(String label, int value) {
    ImGui.pushID(label);

    ImGui.columns(2);
    ImGui.setColumnWidth(0, columnWidthDefault);
    ImGui.text(label);
    ImGui.nextColumn();

    int[] valArray = {value};
    ImGui.dragInt("##dragInt", valArray, 0.1f);

    ImGui.columns(1);
    ImGui.popID();

    return valArray[0];
  }

  public static boolean colorPicker4(String label, Vector4f color) {
    boolean result = false;
    ImGui.pushID(label);

    ImGui.columns(2);
    ImGui.setColumnWidth(0, columnWidthDefault);
    ImGui.text(label);
    ImGui.nextColumn();

    float[] imColor = {color.x, color.y, color.z, color.w};
    if (ImGui.colorEdit4("##ColorPicker", imColor)) {
      color.set(imColor[0], imColor[1], imColor[2], imColor[3]);
      result = true;
    }

    ImGui.columns(1);
    ImGui.popID();

    return result;
  }

}
