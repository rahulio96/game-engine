package slge;

import java.util.ArrayList;
import java.util.List;

import SlRenderer.slMultipleBatchRenderer;

public abstract class slScene {

    protected slMultipleBatchRenderer mb_renderer = new slMultipleBatchRenderer();

    protected slCamera sceneCamera;
    private boolean isSceneRunning = false;
    protected List<slGameObject> listGameObjects = new ArrayList<>();
    public slScene() {

    }

    public void init() {

    }

    public void start() {
        for (slGameObject my_gobj : listGameObjects) {
            my_gobj.start();
            this.mb_renderer.add(my_gobj);
        }
        isSceneRunning = true;
    }

    // Every slGameObject instantiated is added to the this.listGameObjects AND
    // to slMultipleRendererObject object, this.mb_renderer
    public void addGameObjectToScene(slGameObject my_go) {
        if (!isSceneRunning){
            this.listGameObjects.add(my_go);
        } else {
            // dynamically add the object:
            this.listGameObjects.add(my_go);
            my_go.start();
            this.mb_renderer.add(my_go);
        }

    }

    public slCamera getCamera() {
        return this.sceneCamera;
    }

    public abstract void update(float dt);
}
