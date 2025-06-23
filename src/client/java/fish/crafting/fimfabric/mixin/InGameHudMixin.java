package fish.crafting.fimfabric.mixin;

import fish.crafting.fimfabric.rendering.RenderingManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(method = "render", at = @At("TAIL"))
    public void render(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci){
        RenderingManager.get().renderInGameOverlay(context, tickCounter);
    }


}
