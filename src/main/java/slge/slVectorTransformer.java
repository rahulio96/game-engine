package slge;

import org.joml.Vector2f;

// TODO: This should be called "RelocateResizeVector, UpdateVertexPostionScale, or something
// we are not doing any transformation in the sense of a matrix multiplication here!
public class slVectorTransformer {

    public Vector2f position;
    public Vector2f scale;

    // have a separate my_init() and call my_init() from the constructor - this will
    // make coding simpler a bit - instead of a separate class, we can just overload
    // the init() for 3D later. Or we could have used a templated function.
    public slVectorTransformer() {
        my_init(new Vector2f(), new Vector2f());
    }

    public slVectorTransformer(Vector2f position){
        my_init(position, new Vector2f());
    }

    public slVectorTransformer(Vector2f position, Vector2f scale) {
        my_init(position, scale);
    }

    public void my_init(Vector2f position, Vector2f scale) {
        this.position = position;
        this.scale = scale;
    }

}
