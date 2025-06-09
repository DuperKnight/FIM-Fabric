package fish.crafting.fimfabric.ui.custom.quickactions;

import fish.crafting.fimfabric.ui.FancyText;
import fish.crafting.fimfabric.ui.actions.UIActionList;

public class UIQuickActions extends UIActionList {
    public UIQuickActions() {
        super(WIDTH_SMALL);

        title(FancyText.of("Quick Actions"));
        addExpandElement(FancyText.vector("Vectors"), () -> new VectorActions(WIDTH_MEDIUM));
    }
}
