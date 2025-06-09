package fish.crafting.fimfabric.keybind;

import fish.crafting.fimfabric.ui.InterfaceManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.InputUtil;

public class QuickActionsKeybind extends CustomKeybind{

    public QuickActionsKeybind() {
        super("quick_actions", InputUtil.Type.KEYSYM, InputUtil.UNKNOWN_KEY.getCode(), KeybindCategory.IN_GAME_EDITOR);
    }

    @Override
    public void onPressed() {
        boolean returnToGame = false;

        if(MinecraftClient.getInstance().mouse.isCursorLocked()) {
            MinecraftClient.getInstance().setScreen(new ChatScreen(""));
            returnToGame = true;
        }

        InterfaceManager.get().openQuickActions(returnToGame);
    }
}
