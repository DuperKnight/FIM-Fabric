package fish.crafting.fimfabric.rendering;

import fish.crafting.fimfabric.tools.CustomTool;
import fish.crafting.fimfabric.tools.ToolManager;
import fish.crafting.fimfabric.ui.InterfaceManager;
import fish.crafting.fimfabric.ui.UIComponent;
import fish.crafting.fimfabric.util.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Queue;

public class RenderingManager {

    private static RenderingManager instance;
    private final List<Runnable> taskQueue = new ArrayList<>();

    private RenderingManager(){
        instance = this;
    }

    public static RenderingManager get(){
        return instance == null ? new RenderingManager() : instance;
    }

    public void renderOverlay(RenderTickCounter tickCounter, DrawContext drawContext) {
        if(MinecraftClient.getInstance().getOverlay() != null) return; //Reloading textures bugfix

        taskQueue.forEach(Runnable::run);
        taskQueue.clear();

        InformationFeedManager.get().render(drawContext);
        InterfaceManager.get().render(drawContext);

        drawContext.draw();
    }

    public void renderInGameOverlay(DrawContext drawContext, RenderTickCounter tickCounter) {
        if(MinecraftClient.getInstance().getOverlay() != null) return; //mhm

        CustomTool<?> tool = ToolManager.get().getSelectedTool();
        if(tool != null) tool.render2D(drawContext, tickCounter);
    }

    public void addTask(Runnable runnable){
        taskQueue.add(runnable);
    }
}
