package fish.crafting.fimfabric.ui.list;

import fish.crafting.fimfabric.rendering.custom.ScreenRenderContext;
import fish.crafting.fimfabric.ui.UIBox;
import fish.crafting.fimfabric.ui.UIComponent;
import fish.crafting.fimfabric.ui.scroll.Scroller;
import fish.crafting.fimfabric.ui.scroll.UIScrollable;

import java.util.ArrayList;
import java.util.List;

public abstract class UIList<Element> extends UIBox implements UIScrollable {

    private final Scroller scroller = new Scroller() {
        @Override
        public float maxScroll() {
            return cachedElements.size() * heightPerElement();
        }
    };

    protected int paddingX = 2;
    protected int paddingY = 4;
    private final List<Element> cachedElements = new ArrayList<>();

    public UIList(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    protected UIList<Element> padding(int x, int y){
        this.paddingX = x;
        this.paddingY = y;
        return this;
    }

    public void reloadElements(){
        cachedElements.clear();
        cachedElements.addAll(getElements());

        removeAllChildren(false); //Should be fine
        for (Element cachedElement : cachedElements) {
            addChildren(createForElement(cachedElement));
        }
    }

    @Override
    protected void render(ScreenRenderContext context) {
        super.render(context);

        scroller.checkScroll();

        int width = this.width - paddingX * 2;
        int height = heightPerElement();

        float scroll = scroller.getScroll();

    }

    @Override
    public Scroller scroller() {
        return scroller;
    }

    protected abstract List<Element> getElements();
    protected abstract int heightPerElement();
    protected abstract UIComponent createForElement(Element element);
}
