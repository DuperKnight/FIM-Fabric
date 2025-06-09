package fish.crafting.fimfabric.ui.scroll;

import fish.crafting.fimfabric.ui.UIComponent;

import java.util.function.Function;

public enum ScrollerPosition {

    LEFT(c -> c.renderX - 1),
    RIGHT(c -> c.renderX + c.renderWidth);

    private final Function<UIComponent, Integer> function;

    ScrollerPosition(Function<UIComponent, Integer> function){
        this.function = function;
    }

    public int getX(UIComponent component){
        return function.apply(component);
    }
}
