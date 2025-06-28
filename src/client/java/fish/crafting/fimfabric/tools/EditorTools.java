package fish.crafting.fimfabric.tools;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditorTools {

    private static final Map<Integer, CustomTool<?>> tools = new HashMap<>();

    public static MoveTool MOVE = register(0, new MoveTool());
    public static RotateTool ROTATE = register(1, new RotateTool());
    public static ScaleTool SCALE = register(2, new ScaleTool());

    private static <T extends CustomTool<?>> T register(int id, T tool){
        tools.put(id, tool);
        return tool;
    }

    public static @Nullable CustomTool<?> getToolFromID(int id){
        if(id == -1) return null;

        return tools.get(id);
    }

    public static List<CustomTool<?>> getForObject(Object object){
        return tools.values().stream().filter(tool-> tool.isAccessibleFor(object)).toList();
    }

    public static List<CustomTool<?>> getAll() {
        return tools.values().stream().toList();
    }
}
