package fish.crafting.fimfabric.tools.snapping;

import fish.crafting.fimfabric.tools.PosScaled;
import fish.crafting.fimfabric.tools.render.ToolAxis;
import fish.crafting.fimfabric.util.VectorUtils;
import fish.crafting.fimfabric.util.render.CombinedFadeTracker;
import lombok.Getter;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class ScaleSnapping {


    public static ScaleSnapping
    RELATIVE_HALF_BLOCK = new ScaleSnapping("Relative 0.5") {
        @Override
        public double snap(PosScaled scaled, ToolAxis axis, double pos) {
            double coord = axis.getCoordFromUnit(scaled.getPos());

            pos -= coord;
            pos = MathHelper.floor(pos) + 0.5;
            pos += coord;

            return pos;
        }
    },
    RELATIVE_FULL_BLOCK = new ScaleSnapping("Relative 1.0") {
        @Override
        public double snap(PosScaled scaled, ToolAxis axis, double pos) {
            double coord = axis.getCoordFromUnit(scaled.getPos());

            pos -= coord;
            pos = (int) Math.round(pos / 2) * 2;
            pos += coord;

            return pos;
        }
    },
    HALF_BLOCK = new ScaleSnapping("Block 0.5") {
        @Override
        public double snap(PosScaled scaled, ToolAxis axis, double pos) {
            return MathHelper.floor(pos);
        }
    },
    FULL_BLOCK = new ScaleSnapping("Block 1.0") {
        @Override
        public double snap(PosScaled scaled, ToolAxis axis, double pos) {
            return (int) Math.round(pos / 2) * 2;
        }
    },
    DONT_SNAP = new ScaleSnapping("Free Scale") {
        @Override
        public double snap(PosScaled scaled, ToolAxis axis, double pos) {
            return pos;
        }
    };

    public static final ScaleSnapping[] ALL_SNAPPINGS = {RELATIVE_HALF_BLOCK, RELATIVE_FULL_BLOCK, HALF_BLOCK, FULL_BLOCK, DONT_SNAP};
    public final CombinedFadeTracker fade = new CombinedFadeTracker(0.2);
    @NotNull @Getter
    private final String name;

    public ScaleSnapping(@NotNull String name) {
        this.name = name;
    }

    public abstract double snap(PosScaled scaled, ToolAxis axis, double pos);

}
