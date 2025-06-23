package fish.crafting.fimfabric.keybind;

import fish.crafting.fimfabric.connection.ConnectionManager;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class SwitchToIntelliJKeybind extends CustomKeybind{

    public SwitchToIntelliJKeybind() {
        super("switch", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_I, KeybindCategory.INTELLIJ);
    }

    @Override
    public void onPressed() {
        ConnectionManager.get().focusIntelliJ();
    }
}
