package slge;

public abstract class slComponent {

    public slGameObject thisGO  = null;

    // This is a concrete method: if the subclass does not want to
    // have a start(), we should allow for that - some will not have start()
    public void start() {

    }
    public abstract void update(float time_delta);
}
