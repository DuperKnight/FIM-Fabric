package fish.crafting.fimfabric.tools.selector;

public abstract class ScreenSelector extends WorldSelector {
    @Override
    public boolean canBeSelectedInCamera() {
        return false;
    }
}
