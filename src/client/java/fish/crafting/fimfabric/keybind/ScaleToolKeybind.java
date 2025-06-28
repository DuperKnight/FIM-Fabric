package fish.crafting.fimfabric.keybind;

import fish.crafting.fimfabric.tools.EditorTools;
import fish.crafting.fimfabric.tools.ToolManager;
import net.minecraft.client.util.InputUtil;

public class ScaleToolKeybind extends CustomKeybind{

    public ScaleToolKeybind() {
        super("scale_tool", InputUtil.Type.KEYSYM, InputUtil.UNKNOWN_KEY.getCode(), KeybindCategory.IN_GAME_EDITOR);
    }

    @Override
    public void onPressed() {
        ToolManager.get().setSelectedTool(EditorTools.SCALE);
    }
}
