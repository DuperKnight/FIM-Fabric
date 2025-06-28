package fish.crafting.fimfabric.ui.custom.quickactions;

import fish.crafting.fimfabric.tools.PosRotated;
import fish.crafting.fimfabric.tools.Positioned;
import fish.crafting.fimfabric.tools.ToolManager;
import fish.crafting.fimfabric.ui.FancyText;
import fish.crafting.fimfabric.ui.actions.ActionElement;
import fish.crafting.fimfabric.ui.actions.BlockPosActionElement;
import fish.crafting.fimfabric.ui.actions.UIActionList;
import fish.crafting.fimfabric.util.ActionUtils;
import fish.crafting.fimfabric.util.ClickContext;
import fish.crafting.fimfabric.util.SoundUtil;
import fish.crafting.fimfabric.util.VectorUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EntityPose;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class ToolActions extends UIActionList {

    public ToolActions(int width) {
        super(width);

        addElement(new MoveToPlayer());
        addSeparator();
        addElement(new MoveToPlayerLooking());
    }

    private static class MoveToPlayer extends ActionElement {
        public MoveToPlayer() {
            super(FancyText.of("Move to Player"));
            setUpdateStrategy(UpdateStrategy.ACTIVE_IF_EDITING_ANY);
        }

        @Override
        protected void activate(ClickContext context) {
            MinecraftClient instance = MinecraftClient.getInstance();
            ClientPlayerEntity player = instance.player;
            if(player == null) return;

            Positioned editing = ToolManager.get().getEditing();
            if(editing == null) return;

            editing.setPos(
                    player.getX(),
                    player.getY(),
                    player.getZ());

            SoundUtil.clickSound();
        }
    }

    private static class MoveToPlayerLooking extends ActionElement {

        public MoveToPlayerLooking() {
            super(FancyText.location("Set to Player Eyes"));
            setUpdateStrategy(UpdateStrategy.ACTIVE_IF_EDITING_POSROT);
        }

        @Override
        protected void activate(ClickContext context) {
            MinecraftClient instance = MinecraftClient.getInstance();
            ClientPlayerEntity player = instance.player;
            if(player == null) return;

            Positioned editing = ToolManager.get().getEditing();
            if(!(editing instanceof PosRotated posRotated)) return;

            posRotated.setPos(
                    player.getX(),
                    player.getY() + player.getEyeHeight(player.getPose()),
                    player.getZ());

            posRotated.setRotation(
                    player.getPitch(),
                    player.getYaw()
            );

            SoundUtil.clickSound();
        }
    }
}