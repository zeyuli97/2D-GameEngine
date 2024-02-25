package jade;

import org.joml.Matrix4d;
import org.joml.Vector2d;
import org.joml.Vector3d;

public class Camera {
  private Matrix4d projectionMatrix, viewMatrix;
  protected Vector2d position;

  public Camera(Vector2d position) {
    this.position = position;
    this.projectionMatrix = new Matrix4d();
    this.viewMatrix = new Matrix4d();
    adjustProjection();
  }

  public void adjustProjection() {
    // A usual way to start working on a matrix. Identity matrix makes sure future transformation will not be affected.
    projectionMatrix.identity();
    // Init an orthography matrix, each parameter defines a clipping plane.
    projectionMatrix.ortho(0, 32 * 40, 0, 32 * 21, 0, 100);
  }

  public Matrix4d getViewMatrix() {
    Vector3d cameraFront = new Vector3d(0,0,-1); // Negative z is into the screen.
    Vector3d cameraUp = new Vector3d(0,1,0);
    this.viewMatrix.identity();
    // lookAt: This method sets up the view matrix based on a camera position, a target position, and an up vector.
    viewMatrix.lookAt(new Vector3d(position.x, position.y, 20), // z = 20 bird eye view
            cameraFront.add(position.x, position.y, 0),
            cameraUp);

    return this.viewMatrix;
  }

  public Matrix4d getProjectionMatrix() {
    return this.projectionMatrix;
  }

}
