package fish.crafting.fimfabric.util;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public class BoundingBox {

    private double minX = 0.0;
    private double minY = 0.0;
    private double minZ = 0.0;
    private double maxX = 0.0;
    private double maxY = 0.0;
    private double maxZ = 0.0;

    public BoundingBox(){
        this(0, 0, 0, 0, 0, 0);
    }

    public BoundingBox(double x1, double y1, double z1, double x2, double y2, double z2){
        change(x1, y1, z1, x2, y2, z2);
    }

    public void change(double x1, double y1, double z1, double x2, double y2, double z2){
        this.minX = Math.min(x1, x2);
        this.minY = Math.min(y1, y2);
        this.minZ = Math.min(z1, z2);

        this.maxX = Math.max(x1, x2);
        this.maxY = Math.max(y1, y2);
        this.maxZ = Math.max(z1, z2);
    }

    public void change(Vec3d vec1, Vec3d vec2){
        change(vec1.x, vec1.y, vec1.z, vec2.x, vec2.y, vec2.z);
    }

    public Box toMCBox() {
        return new Box(
                minX, minY, minZ,
                maxX, maxY, maxZ
        );
    }

    public Optional<Vec3d> raycast(Vec3d vec3d, Vec3d vec3d2) {
        return Box.raycast(
                minX, minY, minZ,
                maxX, maxY, maxZ,
                vec3d, vec3d2);
    }
}
