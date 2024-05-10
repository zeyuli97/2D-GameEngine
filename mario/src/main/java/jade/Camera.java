package jade;

import org.joml.*;

public class Camera {
  private Matrix4f projectionMatrix, viewMatrix, inverseViewMatrix, inverseProjectionMatrix;
  public Vector2f position;
  private float projectionWidth = 6;
  private float projectionHeight = 3;
  private Vector2f projectionSize = new Vector2f(projectionWidth, projectionHeight);
  private float zoom = 1f;
  public Vector4f clearColor = new Vector4f(1f, 1f, 1f, 1f);

  public Camera(Vector2f position) {
    this.position = position;
    this.projectionMatrix = new Matrix4f();
    this.viewMatrix = new Matrix4f();
    this.inverseViewMatrix = new Matrix4f();
    this.inverseProjectionMatrix = new Matrix4f();
    adjustProjection();
  }

  public void adjustProjection() {
    // A usual way to start working on a matrix. Identity matrix makes sure future transformation will not be affected.
    projectionMatrix.identity();
    //System.out.println(zoom);
    // Init an orthography matrix, each parameter defines a clipping plane.
    projectionMatrix.ortho(0, projectionSize.x * zoom, 0, projectionSize.y * zoom, 0, 100);
    projectionMatrix.invert(inverseProjectionMatrix);
  }

  public Matrix4f getViewMatrix() {
    Vector3f cameraFront = new Vector3f(0,0,-1); // Negative z is into the screen.
    Vector3f cameraUp = new Vector3f(0,1,0);
    this.viewMatrix.identity();
    // lookAt: This method sets up the view matrix based on a camera position, a target position, and an up vector.
    viewMatrix.lookAt(new Vector3f(position.x, position.y, 20), // z = 20 bird eye view
            cameraFront.add(position.x, position.y, 0),
            cameraUp);

    viewMatrix.invert(inverseViewMatrix);
    return this.viewMatrix;
  }

  public Matrix4f getProjectionMatrix() {
    return this.projectionMatrix;
  }

  public Matrix4f getInverseViewMatrix() {
    return this.inverseViewMatrix;
  }

  public Matrix4f getInverseProjectionMatrix() {
    return inverseProjectionMatrix;
  }

  public Vector2f getProjectionSize() {
    return projectionSize;
  }

  public Vector2f getPosition() {
    return position;
  }

  public void setPosition(Vector2f position) {
    this.position = position;
  }

  public float getZoom() {
    return zoom;
  }

  public void setZoom(float zoom) {
    this.zoom = zoom;
  }

  public void addZoom(float value) {
    this.zoom += value;
  }
}
