package fish.crafting.fimfabric.ui.custom.quickactions;

import fish.crafting.fimfabric.ui.FancyText;
import fish.crafting.fimfabric.ui.actions.ActionElement;
import fish.crafting.fimfabric.ui.actions.BlockPosActionElement;
import fish.crafting.fimfabric.ui.actions.UIActionList;
import fish.crafting.fimfabric.util.ActionUtils;
import fish.crafting.fimfabric.util.ClickContext;
import fish.crafting.fimfabric.util.VectorUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class VectorActions extends UIActionList {

    public VectorActions(int width) {
        super(width);

        addElement(new CopyPlayerVector());
        addSeparator();
        addElement(new CopyTargetBlockVector());
    }

    public static class CopyPlayerVector extends ActionElement {
        public CopyPlayerVector() {
            super(FancyText.of("Copy Player Vector"));
        }

        @Override
        protected void activate(ClickContext context) {
            MinecraftClient instance = MinecraftClient.getInstance();
            ClientPlayerEntity player = instance.player;
            if(player == null) return;

            ActionUtils.copyIfClick(context, () -> VectorUtils.toString(player.getPos()));
        }
    }

    public static class CopyTargetBlockVector extends BlockPosActionElement {

        public CopyTargetBlockVector() {
            super(FancyText.of("Copy Target-Block Vector"));
        }

        @Override
        protected void activate(ClickContext context, BlockPos pos) {
            ActionUtils.copyIfClick(context, () -> VectorUtils.toString(Vec3d.of(pos)));
        }
    }
}