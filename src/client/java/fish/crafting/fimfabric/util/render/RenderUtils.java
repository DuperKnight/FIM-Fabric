package fish.crafting.fimfabric.util.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderTickCounter;

public class RenderUtils {

    public static float tickDelta(){
        return tickDelta(false);
    }

    public static float tickDelta(boolean ignoreFreeze){
        RenderTickCounter counter = MinecraftClient.getInstance().getRenderTickCounter();

        //#if MC<=12104
        //$$ return counter.getTickDelta(ignoreFreeze);
        //#endif

        //#if MC>=12105
        return counter.getTickProgress(ignoreFreeze);
        //#endif
    }

}
