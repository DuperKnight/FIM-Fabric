package fish.crafting.fimfabric.tools;

import fish.crafting.fimfabric.client.FIMModClient;
import fish.crafting.fimfabric.tools.worldselector.WorldSelector;
import fish.crafting.fimfabric.util.Constants;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public abstract class CustomTool<T extends Positioned> {

    public void onEnable() {

    }

    public void render2D(DrawContext context, RenderTickCounter counter) {

    }

    public final void handleRender(WorldRenderContext context, Positioned obj){
        render(context, (T) obj); //Oh well;
    }

    protected abstract void render(WorldRenderContext context, T obj);

    public abstract boolean isAccessibleFor(Object object);
    public abstract Identifier getTexture();

    protected final double calculateToolZoom(Vec3d camera, Positioned positioned) {
        Vec3d pos = positioned.getPos();
        double d = camera.squaredDistanceTo(pos) / Constants.TOOL_OPTIMAL_RENDER_DISTANCE;

        return d <= 1.0 ? 1.0 : Math.sqrt(d);
    }

    public List<WorldSelector> getSelectors() {
        return null;
    }

    public boolean onScroll(double scroll) {
        return false;
    }

    public final void handleConfirmEdit(Positioned positioned){
        try{
            T cast = (T) positioned;
            confirmEdit(cast);
        }catch (Exception e) {
            FIMModClient.LOGGER.error("Unable to confirm edit for object '" + positioned + "', for tool " + this + "!", e);
        }
    }

    protected abstract void confirmEdit(T positioned);
}
