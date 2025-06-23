package fish.crafting.fimfabric.ui.actions;

import fish.crafting.fimfabric.ui.FancyText;
import fish.crafting.fimfabric.util.ClickContext;

public abstract class ExpandActionElement extends ActionElement {

    public ExpandActionElement(FancyText text) {
        super(text);
    }

    @Override
    protected void activate(ClickContext context) {
        if(!context.isLeftClick()) return;
        expand();
    }

    public void expand(){
        UIActionList parent = getListParent();
        if(parent == null) return;

        UIActionList expandedList = getExpandedList();
        expandedList.move(parent.getX() + parent.getWidth(), this.y);

        parent.expand(expandedList, this);
    }

    @Override
    protected boolean closeAfterClick() {
        return false;
    }

    protected abstract UIActionList getExpandedList();
}
