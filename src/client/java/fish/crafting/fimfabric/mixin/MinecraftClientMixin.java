package fish.crafting.fimfabric.mixin;

import fish.crafting.fimfabric.ui.InterfaceManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Shadow private static MinecraftClient instance;

    @Inject(method = "setScreen", at = @At("HEAD"))
    public void screen(Screen screen, CallbackInfo ci){
        if(screen instanceof ChatScreen) {
            InterfaceManager.get().onChatOpenState(true);
        }else {
            InterfaceManager.get().onChatOpenState(false);
        }
    }

}
