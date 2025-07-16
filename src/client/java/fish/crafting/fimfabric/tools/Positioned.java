package fish.crafting.fimfabric.tools;

import fish.crafting.fimfabric.tools.selector.WorldSelector;
import net.minecraft.util.math.Vec3d;

public interface Positioned {

    WorldSelector selector();
    Vec3d getPos();
    void setPos(double x, double y, double z);

}
