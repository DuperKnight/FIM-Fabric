package fish.crafting.fimfabric.util;

import fish.crafting.fimfabric.rendering.InformationFeedManager;
import net.minecraft.client.MinecraftClient;

import java.util.function.Supplier;

public class ActionUtils {

    public static void copyIfClick(ClickContext context, Supplier<String> copy){
        if(!context.isLeftClickPress()) return;

        MinecraftClient.getInstance().keyboard.setClipboard(copy.get());
        InformationFeedManager.success("Copied!", false);
        SoundUtil.clickSound();
    }

}
