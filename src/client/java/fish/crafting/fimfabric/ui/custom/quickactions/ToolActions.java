package fish.crafting.fimfabric.ui.custom.quickactions;

import fish.crafting.fimfabric.tools.PosRotated;
import fish.crafting.fimfabric.tools.PosScaled;
import fish.crafting.fimfabric.tools.Positioned;
import fish.crafting.fimfabric.tools.ToolManager;
import fish.crafting.fimfabric.ui.FancyText;
import fish.crafting.fimfabric.ui.TexRegistry;
import fish.crafting.fimfabric.ui.actions.ActionElement;
import fish.crafting.fimfabric.ui.actions.BlockPosActionElement;
import fish.crafting.fimfabric.ui.actions.UIActionList;
import fish.crafting.fimfabric.util.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EntityPose;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class ToolActions extends UIActionList {

    public ToolActions(int width) {
        super(width);

        addElement(new TeleportToCenter());
        addElement(new MoveToPlayer());
        addElement(new CenterPos());
        addSeparator();
        addElement(new MoveToPlayerLooking());
        addSeparator();
        addElement(new SetBoxTo111());
    }

    private static class TeleportToCenter extends ActionElement {
        public TeleportToCenter() {
            super(FancyText.of("Teleport to Center", TexRegistry.TELEPORT));
            setUpdateStrategy(UpdateStrategy.ACTIVE_IF_EDITING_ANY);
        }

        @Override
        protected void activate(ClickContext context) {
            MinecraftClient instance = MinecraftClient.getInstance();
            ClientPlayerEntity player = instance.player;
            if(player == null) return;

            Positioned editing = ToolManager.get().getEditing();
            if(editing == null) return;

            Vec3d pos = editing.getPos();
            PlayerUtil.teleportTo(pos.x, pos.y, pos.z);

            SoundUtil.clickSound();
        }
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

    private static class CenterPos extends ActionElement {
        public CenterPos() {
            super(FancyText.of("Center to Block"));
            setUpdateStrategy(UpdateStrategy.ACTIVE_IF_EDITING_ANY);
        }

        @Override
        protected void activate(ClickContext context) {
            MinecraftClient instance = MinecraftClient.getInstance();
            ClientPlayerEntity player = instance.player;
            if(player == null) return;

            Positioned editing = ToolManager.get().getEditing();
            if(editing == null) return;

            Vec3d blockPos = VectorUtils.toBlockPos(editing.getPos());

            editing.setPos(
                    blockPos.x + 0.5,
                    blockPos.y + 0.5,
                    blockPos.z + 0.5);

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

    private static class SetBoxTo111 extends ActionElement {

        public SetBoxTo111() {
            super(FancyText.boundingBox("Scale to 1x1x1"));
            setUpdateStrategy(UpdateStrategy.ACTIVE_IF_EDITING_POSSCALE);
        }

        @Override
        protected void activate(ClickContext context) {
            MinecraftClient instance = MinecraftClient.getInstance();
            ClientPlayerEntity player = instance.player;
            if(player == null) return;

            Positioned editing = ToolManager.get().getEditing();
            if(!(editing instanceof PosScaled posScaled)) return;

            posScaled.setScale(1, 1, 1);
            SoundUtil.clickSound();
        }
    }
}