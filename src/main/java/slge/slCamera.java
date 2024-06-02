package slge;


import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class slCamera {
    private Matrix4f projectionMatrix, viewMatrix;
    public Vector3f lookFrom = new Vector3f(0.0f, 0.0f, 00.0f);
    public Vector3f lookAt = new Vector3f(0.0f, 0.0f, -1.0f);
    public Vector3f upVector = new Vector3f(0.0f, 1.0f, 0.0f);

    // Inside getViewMatrix() or elsewhere, don't update the above: will be needed
    // if we reset the camera to starting position. Instead mutate the following where needed:
    private Vector3f curLookFrom = new Vector3f(lookFrom);
    private Vector3f curLookAt   = new Vector3f(lookAt);
    private Vector3f curUpVector = new Vector3f(upVector);

    // tiles are 32x32 pixels
    private float grid_size = 32.0f, num_htiles = 40.0f, num_vtiles = 21.0f;
    private float screen_left = 0.0f, screen_right = grid_size * num_htiles,
                    screen_bottom = 0.0f, screen_top = grid_size * num_vtiles;
    private float zNear = 0.0f, zFar = 100.0f;  // these are NOT pixels!

    // camera_position.z > 0 as (0, 0, 0) is at the center of the screen; e.g: (0, 0, 20):
    public slCamera(Vector3f camera_position) {
        this.lookFrom = camera_position;
        this.projectionMatrix = new Matrix4f();
        this.projectionMatrix.identity();
        this.viewMatrix = new Matrix4f();
        this.viewMatrix.identity();
        setProjection();
    }

    public void setProjection() {
        projectionMatrix.identity();
        projectionMatrix.ortho(screen_left, screen_right,
                                    screen_bottom, screen_top, zNear, zFar);
    }

    public Matrix4f getViewMatrix() {
        curLookFrom.set(lookFrom);
        curLookAt.set(lookAt);
        this.viewMatrix.identity();
        this.viewMatrix.lookAt(curLookFrom, curLookAt.add(lookFrom), curUpVector);

        return this.viewMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }
}
