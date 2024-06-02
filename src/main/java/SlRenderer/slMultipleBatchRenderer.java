package SlRenderer;

import slGEComponents.slBillboardVisibles;
import slge.slGameObject;

import java.util.ArrayList;
import java.util.List;

public class slMultipleBatchRenderer {
    // How many triangle pairs i.e. how many rectangles per batch:
    private static final int BATCH_SIZE_MAX = 1000;
    private List<slSingleBatchRenderer> listOfSBRs;

    // Program does not limit the number of single batch renderers we can create;
    // it is resource limited.
    public slMultipleBatchRenderer() {
        this.listOfSBRs = new ArrayList<>();
    }

    // This is called by add(slGameObject go).  We add the go to  either existing
    private void add(slBillboardVisibles my_br) {
        boolean addSuccess = false;
        for (slSingleBatchRenderer sbr : listOfSBRs) {
            // does this SBR have enough space in its slSingleBatchRenderer::vertices list?
            if (sbr.spaceAvailable()) {
                sbr.addBillboard(my_br);
                addSuccess = true;
                break;
            }
        }
        if (!addSuccess) {
            // The SBR is out of space in its "vertices" list - create a new SBR object
            // and add it to this.listOfSBRs and add the billboard object to it:
            slSingleBatchRenderer newSBR = new slSingleBatchRenderer(BATCH_SIZE_MAX);
            newSBR.start();
            listOfSBRs.add(newSBR);
            newSBR.addBillboard(my_br);
        }
    }
    public void add(slGameObject my_go) {
        slBillboardVisibles br = my_go.getComponent(slBillboardVisibles.class);
        if (br != null) {
            add(br);
        }
    }

    public void render() {
        for (slSingleBatchRenderer my_srb : listOfSBRs) {
            my_srb.render();
        }
    }
}
