package fish.crafting.fimfabric.ui.actions;

import fish.crafting.fimfabric.connection.ConnectionManager;
import fish.crafting.fimfabric.ui.FancyText;
import fish.crafting.fimfabric.ui.InterfaceManager;
import fish.crafting.fimfabric.ui.UIBox;
import fish.crafting.fimfabric.ui.UIComponent;
import fish.crafting.fimfabric.util.ClickContext;
import fish.crafting.fimfabric.util.KeyUtil;
import fish.crafting.fimfabric.util.NanoUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicReference;

import static fish.crafting.fimfabric.ui.actions.ActionElement.UpdateStrategy.ALWAYS;

@Accessors(chain = true)
public abstract class ActionElement extends UIBox {

    public static final int X_PADDING = 3;

    private final @Nullable FancyText text;
    @Getter
    protected boolean active = true;
    @Getter
    protected boolean visible = true;
    @Setter
    private @Nullable UpdateStrategy updateStrategy = ALWAYS;
    private boolean separator;

    public ActionElement(@Nullable FancyText text) {
        super(0, 0, 0, 0);
        this.text = text;
    }

    @Override
    protected void render(DrawContext context) {
        UIActionList listParent = getListParent();
        if(listParent == null) return;

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        fill(context, 0x99000000);
        if(separator){
            context.drawHorizontalLine(renderX, renderX + renderWidth, renderY - 1, 0x22FFFFFF);
        }
        if(this.active){
            renderHover(context);
        }

        if(isHovered()) {
            if(this instanceof ExpandActionElement expandElement){
                //listParent.hasExpanded() is a bandaid implementation
                //There should be a small delay between the user hovering an action, before it opens up the expanded display.
                //The check here is done to make sure .expand() only gets called once.

                //The problem would be if the user were switching between expand elements, for example, going from 'Locations' to 'Vectors'
                //Vectors wouldn't expand, since there would already be a Locations expansions. But because it's set to null on first hover
                //that doesn't happen.

                //holy yap
                if(NanoUtils.secondsSince(this.startedHoveringAt) > 0.1){ //Should expand
                    if(!listParent.hasExpanded()){
                        InterfaceManager.get().addPostRenderTask(expandElement::expand);
                    }

                }else if(listParent.getExpandedWhoCalled() != this){ //Should briefly close expansions, unless this element is already expanded
                    InterfaceManager.get().addPostRenderTask(() -> {
                        listParent.expand(null, null);
                    });
                }
            }
        }

        int clr = active ? 0xFFFFFFFF : 0x99FFFFFF;
        int y = centerRenderedY() - textRenderer.fontHeight / 2;

        if(this instanceof ExpandActionElement) {
            context.drawText(
                    textRenderer,
                    ">",
                    renderX + renderWidth - X_PADDING - 5,
                    y,
                    clr,
                    false
            );
        }

        if(text != null){
            text.render(
                    context,
                    renderX + X_PADDING,
                    y,
                    clr,
                    false
            );
        }
    }

    public final void update() {
        if(updateStrategy != null){
            updateStrategy.update(this);
        }
    }

    protected UIActionList getListParent(){
        if(getParent() instanceof UIActionList list) return list;
        return null;
    }

    protected abstract void activate(ClickContext context);

    @Override
    public void onClick(ClickContext context) {
        if(this.active){
            activate(context);

            if(closeAfterClick() && !KeyUtil.isControlPressed()){ //Close

                AtomicReference<UIComponent> topMostParent = new AtomicReference<>();
                visitParents(comp -> {
                    if(comp instanceof UIActionList) topMostParent.set(comp);
                }, false);


                UIComponent topMostActionList = topMostParent.get();
                if(topMostActionList != null) topMostActionList.disable();
            }
        }
    }

    protected boolean closeAfterClick(){
        return true;
    }

    public void separator(boolean separator) {
        this.separator = separator;
    }

    public interface UpdateStrategy {

        void update(ActionElement element);

        UpdateStrategy ALWAYS = element -> {
            element.active = true;
            element.visible = true;
        };

        UpdateStrategy VISIBLE_DISABLED = element -> {
            element.active = false;
            element.visible = true;
        };

        UpdateStrategy IJ_CONNECTED = element -> {
            if(ConnectionManager.get().isConnected()) {
                element.active = true;
                element.visible = true;
            }else{
                element.active = false;
                element.visible = false;
            }
        };

    }
}
