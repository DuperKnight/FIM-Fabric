package fish.crafting.fimfabric.util;

import org.lwjgl.glfw.GLFW;

public record ClickContext(int button, int action, int mods) {

    public boolean isLeftClick(){
        return button == GLFW.GLFW_MOUSE_BUTTON_LEFT;
    }
    public boolean isRightClick(){
        return button == GLFW.GLFW_MOUSE_BUTTON_RIGHT;
    }
    public boolean isMiddleClick(){
        return button == GLFW.GLFW_MOUSE_BUTTON_MIDDLE;
    }

    public boolean isPress(){
        return action == 1;
    }

    public boolean isRelease(){
        return action == 0;
    }

    public boolean isLeftClickPress(){
        return isLeftClick() && isPress();
    }
}
