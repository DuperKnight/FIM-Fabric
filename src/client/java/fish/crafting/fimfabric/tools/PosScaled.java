package fish.crafting.fimfabric.tools;

import net.minecraft.util.math.Vec3d;

public interface PosScaled extends Positioned{

    double scaleX();
    double scaleY();
    double scaleZ();

    void setScale(double x, double y, double z);

    default Vec3d scaleVec(){
        return new Vec3d(scaleX(), scaleY(), scaleZ());
    }
}
