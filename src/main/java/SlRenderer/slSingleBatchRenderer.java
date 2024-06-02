package SlRenderer;


import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20C.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import slGEComponents.slBillboardVisibles;
import slge.slWindow;
import org.joml.Vector4f;

/*
    This is instantiated in slMultipleBatchRenderer
    A slSingleBatchRenderer has at most batchSizeMax PRIMITIVES i.e. squares.
*/

public class slSingleBatchRenderer {
    // Data Layout Constants:

    // Vertex layout
    // float - float : position,  float - float - float - float: color
    private final static int VERTICES_PER_QUAD = 4;
    final int INDICES_PER_QUAD = 6;
    private final int POSITION_SIZE = 2;
    private final int COLOR_SIZE = 4;
    private final int POSITION_OFFSET = 0;
    private final int COLOR_OFFSET = POSITION_OFFSET + POSITION_SIZE * Float.BYTES;
    // 1 vertex = x, y, r, g, b, a --> shader is expecting a z coordinate as well: we make up the
    // fourth coordinate in the shader
    private final int VERTEX_SIZE = 6;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private slBillboardVisibles[] billboards;
    private int numBillboards;
    private boolean spaceAvailable;
    private float[] vertices;

    private int vaoID;
    private int vboID;
    private int maxBatchSize;
    private slShaderManager shader_manager;

    public slSingleBatchRenderer(int batchSizeMax) {
        // This is set in slMultipleBatchRenderer
        this.maxBatchSize = batchSizeMax;
        this.billboards = new slBillboardVisibles[maxBatchSize];
        // all primitives in the batch share the same shader:
        this.shader_manager = new slShaderManager("assets/shaders/default.glsl");
        int vsIndex = 0, fsIndex = 1;
        this.shader_manager.compose_shader_program(vsIndex, fsIndex);

        // vertices for a quad for each primitive in the batch - we allocate enough space
        // to hold the floats required for all vertices in the batch:
        this.vertices = new float[maxBatchSize * VERTICES_PER_QUAD * VERTEX_SIZE];

        this.numBillboards = 0;
        this.spaceAvailable = true;
    }

    public boolean spaceAvailable() {
        return this.spaceAvailable;
    }
    private void loadElementIndices(int[] elements, int index) {
        int offsetArrayIndex = INDICES_PER_QUAD * index;
        int offset = VERTICES_PER_QUAD * index;

        // triangle 1: 3 --> 2 --> 0  ||  7 --> 6 --> 4
        elements[offsetArrayIndex]   = offset + 3;
        elements[offsetArrayIndex+1] = offset + 2;
        elements[offsetArrayIndex+2] = offset;

        // triangle 2: 0 --> 2 --> 1  ||  4 --> 6 --> 5
        elements[offsetArrayIndex + 3] = offset;
        elements[offsetArrayIndex + 4] = offset + 2;
        elements[offsetArrayIndex + 5] = offset + 1;

        return;
    }

    public void addBillboard(slBillboardVisibles bv) {
        int index = this.numBillboards;
        this.billboards[index] = bv;
        ++this.numBillboards;

        loadVertexProperties(index);

        if (this.numBillboards >= this.maxBatchSize) {
            this.spaceAvailable = false;
        }
    }

    // If we had 1000 squares per batch, then we need 6 indices * 1000 elements:
    private int[] generateIndices() {
        int[] elements = new int[INDICES_PER_QUAD * maxBatchSize];
        for (int i=0; i < maxBatchSize; ++i) {
            loadElementIndices(elements, i);
        }

        return elements;
    }

    // Allocate resources in the OpenGL Context
    public void start() {
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // allocate for the vertices on the context --> GPU
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        // allocate for the index buffer --> data is STATIC:
        int eboID = glGenBuffers();
        int[] indices = generateIndices();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        int vaI0 = 0, vaI1 = 1; // vertex attribute index
        glVertexAttribPointer(vaI0, POSITION_SIZE, GL_FLOAT, false,
                                    VERTEX_SIZE_BYTES, POSITION_OFFSET);
        glEnableVertexAttribArray(vaI0);

        glVertexAttribPointer(vaI1, COLOR_SIZE, GL_FLOAT, false,
                                    VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(vaI1);

    }

    // this.vertices[] is populated by this function.
    // --> vertices to be rendered --> [(x, y, r, g, b, a}]
    // (r, g, b, a) == slBillboardVisibles::color.  this.addBillboard(bv) adds the
    // billboard visible to this.billboards list and calls this funciton.
    private void loadVertexProperties(int index) {

        slBillboardVisibles billboard = this.billboards[index];
        int offset = index * VERTICES_PER_QUAD * VERTEX_SIZE;

        Vector4f color = billboard.getColor();

        float xCoord = 1.0f; float yCoord = 1.0f; // these values good for 0th vertex!
        // qv --> "quad vertex":
        for (int qv = 0; qv < VERTICES_PER_QUAD; ++qv) {
            if (qv == 1) {
                yCoord = 0.0f;
            } else if (qv == 2) {
                xCoord = 0.0f;
            } else if (qv == 3) {
                yCoord = 1.0f;
            }

            // position (x, y) coordinates:
            vertices[offset] = billboard.thisGO.vector_transformer.position.x
                                + (xCoord * billboard.thisGO.vector_transformer.scale.x);
            vertices[offset+1] = billboard.thisGO.vector_transformer.position.y
                                + (yCoord * billboard.thisGO.vector_transformer.scale.y);
            // color components:
            vertices[offset+2] = color.x;
            vertices[offset+3] = color.y;
            vertices[offset+4] = color.z;
            vertices[offset+5] = color.w;

            offset += VERTEX_SIZE;
        }  //  for (int i = 0; i < VERTICES_PER_QUAD; ++i)
    }

    // Draw whatever is in the vertex buffer i.e. "vertex" here:
    public void render() {
        // First get something working here - we may optimize later!
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        int vertOffset = 0;
        glBufferSubData(GL_ARRAY_BUFFER, vertOffset, vertices);

        shader_manager.set_shader_program();
        shader_manager.loadMatrix4f("uViewMatrix", slWindow.getScene().getCamera().getViewMatrix());
        shader_manager.loadMatrix4f("uProjMatrix", slWindow.getScene().getCamera().getProjectionMatrix());

        glBindVertexArray(vaoID);
        int vaIndex0 = 0, vaIndex1 = 1;
        glEnableVertexAttribArray(vaIndex0);
        glEnableVertexAttribArray(vaIndex1);

        glDrawElements(GL_TRIANGLES, this.numBillboards * VERTEX_SIZE, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(vaIndex0);
        glDisableVertexAttribArray(vaIndex1);
        glBindVertexArray(0);

        shader_manager.detach_shader();
    }

}





























