package fish.crafting.fimfabric.rendering.custom;

public interface IRenderContext3D {

    void setLineWidth(float lineWidth);

    void renderLineGradient(float x1, float y1, float z1,
                            float x2, float y2, float z2,
                            float r1, float g1, float b1,
                            float r2, float g2, float b2);

    default void renderLine(float x1, float y1, float z1,
                            float x2, float y2, float z2,
                            float r,
                            float g,
                            float b){
        renderLineGradient(x1, y1, z1, x2, y2, z2, r, g, b, r, g, b);
    }

}
