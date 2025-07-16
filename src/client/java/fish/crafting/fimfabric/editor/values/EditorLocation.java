package fish.crafting.fimfabric.editor.values;

import fish.crafting.fimfabric.editor.EditorReference;
import fish.crafting.fimfabric.editor.Referenced;
import fish.crafting.fimfabric.settings.VectorSettings;
import fish.crafting.fimfabric.tools.MoveTool;
import fish.crafting.fimfabric.tools.PosRotated;
import fish.crafting.fimfabric.tools.RotateTool;
import fish.crafting.fimfabric.tools.ToolManager;
import fish.crafting.fimfabric.tools.selector.WorldSelector;
import fish.crafting.fimfabric.util.VectorUtils;
import lombok.Getter;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

public class EditorLocation implements Referenced, PosRotated {

    private final WorldSelector selector = new Selector();
    public @NotNull Vec3d vector = new Vec3d(0, 0, 0);
    public float pitch = 0f, yaw = 0f;
    public String world = "";

    @Getter
    private @NotNull Vec3d direction = new Vec3d(1.0, 0.0, 0.0);
    private @NotNull Box box = new Box(0, 0, 0, 0, 0, 0);
    private final EditorReference reference = new EditorReference();
    public int lastRenderFrame = 0;

    public EditorLocation(double x, double y, double z, float pitch, float yaw, String world){
        setVector(x, y, z);
        setRotation(pitch, yaw);
        this.world = world;
    }

    public void setRotation(float pitch, float yaw){
        this.pitch = pitch;
        this.yaw = yaw;
        updateRotation();
    }

    public void setVector(double x, double y, double z){
        this.vector = new Vec3d(x, y, z);
        updateBox();
    }

    public void updateBox(){
        double d = VectorSettings.renderSize();
        this.box = new Box(
                vector.x - d,
                vector.y - d,
                vector.z - d,
                vector.x + d,
                vector.y + d,
                vector.z + d
        );
    }

    public void updateRotation(){
        this.direction = VectorUtils.getDirection(pitch, yaw);
    }

    @Override
    public WorldSelector selector() {
        return this.selector;
    }

    @Override
    public Vec3d getPos() {
        return vector;
    }

    @Override
    public void setPos(double x, double y, double z) {
        setVector(x, y, z);
    }

    @Override
    public EditorReference reference() {
        return reference;
    }

    @Override
    public float pitch() {
        return this.pitch;
    }

    @Override
    public float yaw() {
        return this.yaw;
    }

    private class Selector extends WorldSelector {

        @Override
        protected void onPress(int button, int mods) {
            toolCallback(true);
        }

        @Override
        protected void onUnPress() {
            toolCallback(false);
        }

        private void toolCallback(boolean press){
            if(ToolManager.get().getEditing() != EditorLocation.this) return;
            if(ToolManager.get().getSelectedTool() instanceof MoveTool moveTool){
                moveTool.vectorClickCallback(EditorLocation.this, press);
            }
            if(ToolManager.get().getSelectedTool() instanceof RotateTool rotateTool){
                rotateTool.locationClickCallback(EditorLocation.this, press);
            }
        }

        @Override
        public Box getBox() {
            return box;
        }
    }
}
