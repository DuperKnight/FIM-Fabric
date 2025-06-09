package fish.crafting.fimfabric.tools.worldselector;

import fish.crafting.fimfabric.util.VectorUtils;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

//NOTE: Make sure the returned BOX is of same width and length, circle-wise!
public abstract class CircularSelector extends WorldSelector{

    private final Vec3d normal;
    private final double outerRadiusPercent;
    protected double maxRadiusExtend = 0.0;

    /**
     * @param outerRadiusPercent Percent of the outer circle radius cut
     */
    public CircularSelector(Vec3d normal, double outerRadiusPercent){
        this.normal = normal;
        this.outerRadiusPercent = outerRadiusPercent;
    }

    @Override
    public @Nullable Vec3d raycast(Vec3d from, Vec3d to) {
        Vec3d raycast = super.raycast(from, to);
        if(raycast == null) return null;

        Box box = getBox();
        Vec3d invertedNormal = VectorUtils.invertZeros(normal);

        Vec3d center = box.getCenter().multiply(invertedNormal);
        Vec3d point = raycast.multiply(invertedNormal);

        double sqD = center.squaredDistanceTo(point);
        double maxR = (normal.y > 0.0 ? box.getLengthX() : box.getLengthY()) / 2.0;
        double minR = outerRadiusPercent * maxR;

        maxR += maxRadiusExtend;

        maxR *= maxR; //square
        minR *= minR; //yup

        if(sqD <= maxR && sqD >= minR) return raycast;
        return null;
    }
}
