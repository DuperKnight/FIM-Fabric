package fish.crafting.fimfabric.ui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FancyText {
    public final @NotNull String name;
    public final @Nullable Identifier icon;
    private boolean offsetEmptyIcon = true;

    public FancyText(@NotNull String name, @Nullable Identifier icon){
        this.name = name;
        this.icon = icon;
    }

    public static FancyText vector(@NotNull String name){
        return of(name, TexRegistry.VECTOR);
    }

    public static FancyText coords(@NotNull String name){
        return of(name, TexRegistry.COORDINATES);
    }

    public static FancyText location(@NotNull String name){
        return of(name, TexRegistry.LOCATION);
    }

    public static FancyText of(@NotNull String name) {
        return new FancyText(name, null);
    }

    public static FancyText of(@NotNull String name, Identifier icon) {
        return new FancyText(name, icon);
    }

    public FancyText offsetEmptyIcon(boolean offsetEmptyIcon){
        this.offsetEmptyIcon = offsetEmptyIcon;
        return this;
    }

    public void render(DrawContext context, int x, int y, int color, boolean shadow){
        render(context, x, y, color, shadow, MinecraftClient.getInstance().textRenderer);
    }

    public void render(DrawContext context, int x, int y, int color, boolean shadow, TextRenderer renderer){
        if(icon != null){
            int size = renderer.fontHeight;
            context.drawGuiTexture(
                    RenderLayer::getGuiTextured,
                    icon,
                    x,
                    y - 1,
                    size,
                    size,
                    color
            );
        }

        int textX = x;
        if(offsetEmptyIcon) {
            textX += (int) (renderer.fontHeight * 1.5);
        }

        context.drawText(
                renderer,
                name,
                textX,
                y,
                color,
                shadow
        );
    }
}
