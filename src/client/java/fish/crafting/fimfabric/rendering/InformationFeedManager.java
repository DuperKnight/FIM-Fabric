package fish.crafting.fimfabric.rendering;

import fish.crafting.fimfabric.ui.TexRegistry;
import fish.crafting.fimfabric.util.ColorUtil;
import fish.crafting.fimfabric.util.NumUtil;
import fish.crafting.fimfabric.util.SoundUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InformationFeedManager {

    private static InformationFeedManager instance;
    private static final long FADE_DURATION = 1_000_000_000;
    private static final long DROP_DURATION = 500_000_000;

    private final List<FeedLine> lines = new ArrayList<>();

    private InformationFeedManager(){
        instance = this;
    }

    public static InformationFeedManager get(){
        return instance == null ? new InformationFeedManager() : instance;
    }

    public void add(FeedLine line) {
        lines.add(line);
    }

    public void render(DrawContext context) {
        if(lines.isEmpty()) return;

        context.getMatrices().push();

        Iterator<FeedLine> iterator = lines.iterator();
        long now = System.nanoTime();

        int x = 20;
        int y = 10;

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int addY = (int) (textRenderer.fontHeight * 1.5);

        while(iterator.hasNext()) {
            FeedLine next = iterator.next();

            long stopRenderingTime = next.startMS + next.stay;
            if((stopRenderingTime + DROP_DURATION) <= now) {
                iterator.remove();
                continue;
            }

            if(stopRenderingTime <= now){
                context.getMatrices().translate(0, addY * NumUtil.sinLerpCurrentTime(1.0, 0.0, stopRenderingTime, DROP_DURATION), 0);
                //y += (int) (addY * NumUtil.sinLerpCurrentTime(1.0, 0.0, stopRenderingTime, DROP_DURATION));
                continue;
            }

            int alpha = 255;
            long fadeTime = now - (stopRenderingTime - FADE_DURATION);
            if(fadeTime > 0) { //Fade started
                alpha = (int) (alpha * (1.0 - NumUtil.clamp(fadeTime / (double) FADE_DURATION, 0.0, 1.0)));
            }

            if(alpha > 3){
                next.render(context, x, y, alpha);
            }

            y += addY;
        }

        context.draw();
        context.getMatrices().pop();
    }

    public static FeedLine success(String text, boolean sound){
        var line = new Success(text);
        if(sound) line.sound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
        get().add(line);

        return line;
    }

    public static FeedLine error(String text, boolean sound){
        var line = new Error(text);
        if(sound) line.sound(SoundEvents.ENTITY_IRON_GOLEM_REPAIR, 1f);
        get().add(line);

        return line;
    }

    public static FeedLine warn(String text, boolean sound){
        var line = new Warn(text);
        if(sound) line.uiSound();
        get().add(line);

        return line;
    }

    public static FeedLine info(String text, boolean sound){
        var line = new Info(text);
        if(sound) line.uiSound();
        get().add(line);

        return line;
    }

    private static class Error extends SimpleColoredFeedLine {
        public Error(String text) {
            super(text, 0xFFCE2928, TexRegistry.FEED_ERROR);
        }
    }

    private static class Warn extends SimpleColoredFeedLine {
        public Warn(String text) {
            super(text, 0xFFFFC62B, TexRegistry.FEED_WARN);
        }
    }

    private static class Success extends SimpleColoredFeedLine {
        public Success(String text) {
            super(text, 0xFF79EA38, TexRegistry.FEED_SUCCESS);
        }
    }

    private static class Info extends SimpleColoredFeedLine {
        public Info(String text) {
            super(text, 0xFFABDAE8, TexRegistry.FEED_QUESTION);
        }
    }

    private static abstract class SimpleColoredFeedLine extends FeedLine {

        private final String text;
        private final int color;
        private final Identifier icon;

        public SimpleColoredFeedLine(String text, int color, Identifier icon){
            this.text = text;
            this.color = color;
            this.icon = icon;
        }

        @Override
        protected void render0(DrawContext context, int x, int y, int alpha) {
            int clr = ColorUtil.alpha(color, alpha);

            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            int size = textRenderer.fontHeight;

            context.drawGuiTexture(
                    RenderLayer::getGuiTextured,
                    icon,
                    x - size - 2,
                    y,
                    size, size,
                    clr
            );

            context.drawText(textRenderer,
                    text,
                    x,
                    y,
                    clr,
                    true);
        }
    }

    public static abstract class FeedLine {

        private float pitch = 1.0f;
        private SoundEvent sound = null;

        private boolean playedSound = false;
        public final long startMS = System.nanoTime();
        private long stay = 5_000_000_000L; //5s

        public final void render(DrawContext context, int x, int y, int alpha) {
            render0(context, x, y, alpha);
            if(!playedSound && sound != null) {
                playedSound = true;
                SoundUtil.play(sound, pitch);
            }
        }

        public void durationSeconds(int seconds){
            stay = seconds * 1_000_000_000L;
        }

        public FeedLine sound(SoundEvent sound, float pitch) {
            this.sound = sound;
            this.pitch = pitch;
            return this;
        }

        public FeedLine uiSound() {
            return sound(SoundEvents.UI_TOAST_IN, 1.0f);
        }

        protected abstract void render0(DrawContext context, int x, int y, int alpha);

    }

}
