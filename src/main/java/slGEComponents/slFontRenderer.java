package slGEComponents;

import slge.slComponent;

public class slFontRenderer extends slComponent{

    /*
    // No need for a constructor for this class - may change later ...
    public slFontRenderer() {

    }
    */

    @Override
    public void start() {
        if (thisGO.getComponent(slBillboardVisibles.class) != null) {
            System.out.println("Font Renderer class is here!!");
        }
    }
    @Override
    public void update(float time_delta) {

    }
}
