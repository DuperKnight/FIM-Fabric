package fish.crafting.fimfabric.mixin;

import fish.crafting.fimfabric.connection.packets.F2IMinecraftFocusedPacket;
import fish.crafting.fimfabric.ui.InterfaceManager;
import fish.crafting.fimfabric.util.WindowUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Window.class)
public class WindowMixin {

    @Inject(method = "onFramebufferSizeChanged", at = @At("HEAD"))
    public void onWindowResize(long window, int width, int height, CallbackInfo ci){
        var g = WindowUtil.guiScale();
        InterfaceManager.get().onResize(width / g, height / g);
    }

    @Inject(method = "onWindowFocusChanged", at = @At("HEAD"))
    public void onFocus(long window, boolean focused, CallbackInfo ci){
        if(focused && window == MinecraftClient.getInstance().getWindow().getHandle()) {
            new F2IMinecraftFocusedPacket().send();
        }
    }

}
