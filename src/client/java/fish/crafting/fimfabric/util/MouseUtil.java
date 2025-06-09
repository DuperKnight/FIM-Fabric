package fish.crafting.fimfabric.util;

import net.minecraft.client.MinecraftClient;

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

}
