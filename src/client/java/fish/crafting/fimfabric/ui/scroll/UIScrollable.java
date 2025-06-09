package fish.crafting.fimfabric.ui.scroll;

public interface UIScrollable {

    Scroller scroller();

    default ScrollerPosition scrollerPosition(){
        return ScrollerPosition.RIGHT;
    }

}
