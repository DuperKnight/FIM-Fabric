package fish.crafting.fimfabric.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;

public class MouseUtil {

    public static double x(){
        return MinecraftClient.getInstance().mouse.getX();
    }

    public static double y(){
        return MinecraftClient.getInstance().mouse.getY();
    }

    public static int xInt(){
        return (int) x();
    }

    public static int yInt(){
        return (int) y();
    }

    public static int scaledXInt(){
        Window window = MinecraftClient.getInstance().getWindow();
        return (int) MinecraftClient.getInstance().mouse.getScaledX(window);
    }

    public static int scaledYInt(){
        Window window = MinecraftClient.getInstance().getWindow();
        return (int) MinecraftClient.getInstance().mouse.getScaledY(window);
    }

}
