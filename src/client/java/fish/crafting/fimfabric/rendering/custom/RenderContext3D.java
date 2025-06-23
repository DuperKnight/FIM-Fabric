package fish.crafting.fimfabric.rendering.custom;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public interface RenderContext3D {

    /**
     * Called at the beginning of the custom FIM rendering pipeline
     */
    void beginRender();

    /**
     * Called at the end of the custom FIM rendering pipeline
     */
    void endRender();

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

    void renderFilledBox(double minX, double minY, double minZ,
                         double maxX, double maxY, double maxZ,
                         float red, float green, float blue, float alpha);

    default void renderBoxOutline(Box box, float red, float green, float blue, float alpha) {
        renderBoxOutline(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, red, green, blue, alpha);
    }

    void renderBoxOutline(double minX, double minY, double minZ,
                         double maxX, double maxY, double maxZ,
                         float red, float green, float blue, float alpha);


    Vec3d camera();

    void translate(double x, double y, double z);

    default void translate(float x, float y, float z) {
        translate((double) x, y, z);
    }

    default void translateCamera() {
        Vec3d vec3d = camera();
        translate(-vec3d.x, -vec3d.y, -vec3d.z);
    }

    void vertex(float x, float y, float z, int rgb);

    void push();
    void pop();

    float tickDelta();
}
