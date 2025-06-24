package fish.crafting.fimfabric.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import fish.crafting.fimfabric.rendering.RenderingManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    //#if MC<12106
    //$$ @Inject(method = "render", at = @At(value = "INVOKE", target = "Lorg/joml/Matrix4fStack;popMatrix()Lorg/joml/Matrix4fStack;"))
    //$$ public void render(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci, @Local DrawContext drawContext) {
    //#else
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;scoped(Ljava/lang/String;)Lnet/minecraft/util/profiler/ScopedProfiler;"))
    public void render(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci, @Local DrawContext drawContext) {
    //#endif

        //Make sure this is ALWAYS getting called on EACH RENDER!!
        RenderingManager.get().renderOverlay(tickCounter, drawContext);
    }
}
