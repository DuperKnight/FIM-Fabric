package fish.crafting.fimfabric.mixin;

import fish.crafting.fimfabric.tools.ToolManager;
import fish.crafting.fimfabric.tools.selector.WorldSelectorManager;
import fish.crafting.fimfabric.ui.InterfaceManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {

    @Inject(method = "onCursorPos", at = @At("HEAD"))
    public void cursorPos(long window, double x, double y, CallbackInfo ci){
        InterfaceManager.get().handleCursorPos((int) x, (int) y);
    }

    @Inject(method = "onMouseButton", at = @At("HEAD"), cancellable = true)
    private void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        if (window != MinecraftClient.getInstance().getWindow().getHandle()) return;

        boolean cancel = false;
        Mouse mouse = (Mouse) (Object) this;
        if(!mouse.isCursorLocked()){
            InterfaceManager.get().handleClick(button, action, mods);
        }else{
            cancel = cancel || WorldSelectorManager.get().handlePress(button, action, mods);
        }

        if(cancel){
            ci.cancel();
        }
    }

    @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
    private void onMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (window != MinecraftClient.getInstance().getWindow().getHandle()) return;

        boolean cancel = false;
        Mouse mouse = (Mouse) (Object) this;
        if (!mouse.isCursorLocked()) {
           InterfaceManager.get().handleScroll(vertical);
        }else{
            cancel = cancel || ToolManager.get().onScroll(vertical);
        }

        if(cancel) {
            ci.cancel();
        }
    }

    @Inject(method = "lockCursor", at = @At("HEAD"))
    public void lock(CallbackInfo ci){
        InterfaceManager.get().handleMouseLock(true);
    }

    @Inject(method = "unlockCursor", at = @At("HEAD"))
    public void unlock(CallbackInfo ci){
        InterfaceManager.get().handleMouseLock(false);
    }

}
