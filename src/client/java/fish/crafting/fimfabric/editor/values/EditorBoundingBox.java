package fish.crafting.fimfabric.editor.values;

import fish.crafting.fimfabric.editor.EditorReference;
import fish.crafting.fimfabric.editor.Referenced;
import fish.crafting.fimfabric.settings.BoundingBoxSettings;
import fish.crafting.fimfabric.settings.VectorSettings;
import fish.crafting.fimfabric.tools.*;
import fish.crafting.fimfabric.tools.worldselector.WorldSelector;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

public class EditorBoundingBox implements Referenced, Positioned {

    public Vec3d min, max;

    private final WorldSelector selector = new Selector();
    public @NotNull Vec3d center = new Vec3d(0, 0, 0);

    private @NotNull Box renderBox = new Box(0, 0, 0, 0, 0, 0);
    private final EditorReference reference = new EditorReference();
    public int lastRenderFrame = 0;

    public EditorBoundingBox(double x1, double y1, double z1, double x2, double y2, double z2){
        setValues(x1, y1, z1, x2, y2, z2);
    }

    public void setValues(double x1, double y1, double z1, double x2, double y2, double z2){
        min = new Vec3d(
                Math.min(x1, x2),
                Math.min(y1, y2),
                Math.min(z1, z2));

        max = new Vec3d(
                Math.max(x1, x2),
                Math.max(y1, y2),
                Math.max(z1, z2));

        updateCenter();
    }

    public void updateCenter(){
        center = new Vec3d(
                min.x + xLength() / 2.0,
                min.y + yLength() / 2.0,
                min.z + zLength() / 2.0
        );

        double d = BoundingBoxSettings.renderSize();
        renderBox = new Box(
                center.x - d,
                center.y - d,
                center.z - d,
                center.x + d,
                center.y + d,
                center.z + d
        );
    }

    @Override
    public WorldSelector selector() {
        return this.selector;
    }

    @Override
    public Vec3d getPos() {
        return center;
    }

    @Override //This sets the center
    public void setPos(double x, double y, double z) {
        double _x = xLength() / 2.0;
        double _y = yLength() / 2.0;
        double _z = zLength() / 2.0;


        setValues(
                x - _x, y - _y, z - _z,
                x + _x, y + _y, z + _z
        );

        updateCenter();
    }

    public double xLength(){
        return max.x - min.x;
    }

    public double yLength(){
        return max.y - min.y;
    }

    public double zLength(){
        return max.z - min.z;
    }

    @Override
    public EditorReference reference() {
        return reference;
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
            if(ToolManager.get().getEditing() != EditorBoundingBox.this) return;
            if(ToolManager.get().getSelectedTool() instanceof MoveTool moveTool){
                moveTool.vectorClickCallback(EditorBoundingBox.this, press);
            }
        }

        @Override
        public Box getBox() {
            return renderBox;
        }
    }
}
