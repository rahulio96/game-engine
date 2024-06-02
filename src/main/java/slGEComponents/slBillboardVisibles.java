package slGEComponents;

import slge.slComponent;
import org.joml.Vector4f;

// This is the PER VERTEX property of the primitive.
// TODO: The texture coordinates can be tucked in here??
public class slBillboardVisibles extends slComponent {

    private Vector4f color;

    public slBillboardVisibles(Vector4f my_color) {
        this.color = my_color;
    }
    @Override
    public void start() {

    }

    @Override
    public void update(float time_delta) {

    }

    public Vector4f getColor() {
        return this.color;
    }
}
