package fish.crafting.fimfabric.tools;

import fish.crafting.fimfabric.connection.ConnectionManager;
import fish.crafting.fimfabric.util.KeyUtil;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class ToolManager {

    private static ToolManager instance;

    private final List<CustomTool<?>> availableTools = new ArrayList<>();
    @Getter
    private CustomTool<?> selectedTool = null;
    @Getter
    private Positioned editing = null;

    private ToolManager(){
        instance = this;
    }

    public static ToolManager get(){
        return instance == null ? new ToolManager() : instance;
    }

    /**
     * @return True, if this should cancel scroll
     */
    public boolean onScroll(double scroll){
        if(selectedTool == null) return false;
        return selectedTool.onScroll(scroll);
    }

    public void setEditing(Object object){
        if(!(object instanceof Positioned positioned)){
            removeEditing();
            return;
        }

        List<CustomTool<?>> tools = EditorTools.getForObject(object);
        if(tools.isEmpty()) {
            removeEditing();
            return;
        }

        editing = positioned;
        availableTools.addAll(tools);

        if(!availableTools.contains(selectedTool)){
            setSelectedTool0(null);
        }
    }

    public void ijFocused() {
        setEditing(null);
    }

    public void setSelectedTool(CustomTool<?> tool){
        if(tool == null || availableTools.contains(tool)) setSelectedTool0(tool);
    }

    private void setSelectedTool0(CustomTool<?> tool){
        if(tool != null) tool.onEnable();
        if(tool == selectedTool) return;
        selectedTool = tool;
    }

    private void removeEditing() {
        editing = null;
        availableTools.clear();
        selectedTool = null;
    }

    public void confirmEdit() {
        if(this.editing == null || this.selectedTool == null) return;
        this.selectedTool.handleConfirmEdit(this.editing);

        if(!KeyUtil.isShiftPressed()){
            ConnectionManager.get().focusIntelliJ();
            setEditing(null);
        }
    }
}
