package fish.crafting.fimfabric.util;

import net.minecraft.client.MinecraftClient;

public class WindowUtil {

    public static int scaledWidth(){
        return MinecraftClient.getInstance().getWindow().getScaledWidth();
    }

    public static int scaledHeight(){
        return MinecraftClient.getInstance().getWindow().getScaledHeight();
    }

    public static int normalWidth(){
        return MinecraftClient.getInstance().getWindow().getWidth();
    }

    public static int normalHeight(){
        return MinecraftClient.getInstance().getWindow().getHeight();
    }

    public static int guiScale(){
        Integer value = MinecraftClient.getInstance().options.getGuiScale().getValue();
        return MinecraftClient.getInstance().getWindow().calculateScaleFactor(value, false);
    }

}
