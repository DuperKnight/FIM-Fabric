package fish.crafting.fimfabric.tools.selector;

import fish.crafting.fimfabric.rendering.world.WorldRenderingManager;
import lombok.Getter;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import static fish.crafting.fimfabric.tools.selector.WorldSelectorManager.LAST_RENDER_FRAME;

public abstract class WorldSelector {

    @Getter
    private boolean pressed = false;
    @Getter
    private boolean hovered = false;
    public int lastRenderFrame = -1;

    public abstract Box getBox();

    public @Nullable Vec3d raycast(Vec3d from, Vec3d to){
        return getBox().raycast(from, to).orElse(null);
    }

    public final void handleStatus(boolean enabled){
        if(enabled) {
            onEnabled();
        }else{
            onDisabled();
        }
    }

    public boolean canBeSelectedInCamera(){
        return true;
    }

    public final void handlePress(int button, int mods){
        pressed = true;
        onPress(button, mods);
    }

    public final void handleDepress(){
        pressed = false;
        onUnPress();
    }

    public final void handleHoverStatus(boolean hover){
        if(hover == hovered) return;

        hovered = hover;
        onHoverStatus(hover);
    }

    protected void onPress(int button, int mods){

    }

    protected void onUnPress(){

    }

    protected void onEnabled(){

    }

    protected void onDisabled(){
        hovered = false;
        pressed = false;
    }

    protected void onHoverStatus(boolean hover){

    }

    public final void update(){
        if(this.lastRenderFrame < LAST_RENDER_FRAME);{
            WorldSelectorManager.get().add(this);
        }

        this.lastRenderFrame = WorldRenderingManager.RENDER_FRAME;
    }
}
