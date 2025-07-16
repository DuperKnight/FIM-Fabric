package fish.crafting.fimfabric.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
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
        Mouse mouse = MinecraftClient.getInstance().mouse;
        return (int) scaleX(window, mouse.getX());
    }

    public static int scaledYInt(){
        Window window = MinecraftClient.getInstance().getWindow();
        Mouse mouse = MinecraftClient.getInstance().mouse;
        return (int) scaleY(window, mouse.getY());
    }

    private static double scaleX(Window window, double x) {
        return x * (double)window.getScaledWidth() / (double)window.getWidth();
    }

    private static double scaleY(Window window, double y) {
        return y * (double)window.getScaledHeight() / (double)window.getHeight();
    }

}
