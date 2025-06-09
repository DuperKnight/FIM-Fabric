package fish.crafting.fimfabric.util;

import org.lwjgl.glfw.GLFW;

public class Cursors {

    public static final long
            NORMAL = GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR),
            HORIZONTAL = GLFW.glfwCreateStandardCursor(GLFW.GLFW_HRESIZE_CURSOR),
            VERTICAL = GLFW.glfwCreateStandardCursor(GLFW.GLFW_VRESIZE_CURSOR),
            SELECT = GLFW.glfwCreateStandardCursor(GLFW.GLFW_RESIZE_ALL_CURSOR),
            POINTING = GLFW.glfwCreateStandardCursor(GLFW.GLFW_HAND_CURSOR);

}
