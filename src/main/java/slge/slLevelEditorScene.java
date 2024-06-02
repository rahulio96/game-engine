package slge;

import slGEComponents.slBillboardVisibles;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/*

Level Scene Editor: Edits the level scene into batches.

The scene properties defined in this file.
The scene size == (WIDTH x HEIGHT)
Number of tiles in the scene = (X_MAX x Y_MAX).

Divide the scene into that many (square) tiles and add them to listGameObjects
(inherited property).

Create one slGameObject per tile. One slGameObject == [(x, y), (r, g, b, a)], essentially
(for now!).
Add each game object to slScene::mb_renderer which is a slMultipleBatchRenderer object.

slMultipleBatchRenderer::add(go):
If the MBR object has room ints current slSingleBatchRenderer::vertices list, then
add the go to the current SBR. Else create a new SBR object, add it to
slMultipleBatchRenderer::listOfSBRs

When we call slMultipleBatchRenderer::render(), it calls ::render() of each SBR in its
listOfSBRs.
*/


// This is called from main() --> slWindow().get() --> slWindow::init()
public class slLevelEditorScene extends slScene {
    public slLevelEditorScene() { }

    @Override
    public void init() {
        int retVal = -1;

        // inherited property
        this.sceneCamera = new slCamera(new Vector3f(-250.0f, 0.0f, 20.0f));

        int X_OFFSET = 10, Y_OFFSET = 10;
        int WIDTH = 800, HEIGHT = 800;  // size of the scene we are rendering
        int X_MAX = 10, Y_MAX = 20;   // we are creating 10 x 20 squares == 200 square tiles
        float PADDING = 0.0f;

        float totalWidth  = (float)(WIDTH - X_OFFSET * 2);  // total width of the square
        float totalHeight = (float)(HEIGHT - Y_OFFSET * 2); // total height of the square
        final float sizeX = totalWidth/(float)X_MAX;
        final float sizeY = totalHeight/(float)Y_MAX;

        for (int x=0; x < X_MAX; ++x) {  // total X_MAX tiles along x-axis
            for (int y=0; y < Y_MAX; ++y) {  // total Y_MAX tiles along y-axis
                float xPos = X_OFFSET + (x * sizeX) + (PADDING * x);
                float yPos = Y_OFFSET + (y * sizeY) + (PADDING * y);

                slGameObject my_go = new slGameObject("MyGO (" + x + ", " + y +")",
                                            new slVectorTransformer(
                                                    new Vector2f(xPos, yPos),       // position,
                                                    new Vector2f(sizeX, sizeY) ));  // scale
                // colors of the vertices are scaled by the position of the vertex:
                my_go.addComponent( new slBillboardVisibles(
                            new Vector4f(xPos/totalWidth, yPos/totalHeight, 1.0f, 1.0f)) );
                // addGameObjectToScene(...) <-- inherited method
                // adds my_go to slMultipleBatchRenderer object: mb_renderer (inherited)
                this.addGameObjectToScene(my_go);
            }
        }
    }

    @Override
    public void update(float dt) {
        System.out.println("Current Frames per Second: " + (1.0f/dt));

        for (slGameObject my_go : this.listGameObjects) {
            my_go.update(dt);
        }
        // All game objects created were added to slScene::mb_renderer --> the render() is
        // delegated to slMultipleBatchRenderer::render() --> slSingleBatchRenderer::render()
        this.mb_renderer.render();
    }

}   // public class slLevelEditorScene extends slScene
