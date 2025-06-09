package fish.crafting.fimfabric.util;

import fish.crafting.fimfabric.rendering.world.WorldRenderingManager;
import fish.crafting.fimfabric.util.cache.LongCache;
import fish.crafting.fimfabric.util.cache.RenderFrameCache;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockStateRaycastContext;
import net.minecraft.world.RaycastContext;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BlockUtils {

    private static final int MAX_RAY_DISTANCE = 64;

    private static final RenderFrameCache<BlockPos> PLAYER_TARGET_CACHE = new RenderFrameCache<>(null);

    public static BlockPos getTargetBlockPos() {
        return PLAYER_TARGET_CACHE.computeIfAbsent(BlockUtils::getPlayerTargetPos);
    }

    private static BlockPos getPlayerTargetPos(){
        MinecraftClient client = MinecraftClient.getInstance();
        ClientWorld world = client.world;
        if(world == null) return null;

        ShapeContext context = (client.player == null ? ShapeContext.absent() : ShapeContext.of(client.player));

        Camera camera = client.gameRenderer.getCamera();
        Vec3d pos = camera.getPos();
        Vec3d rot = client.cameraEntity.getRotationVecClient();
        Vec3d pos2 = pos.add(rot.multiply(MAX_RAY_DISTANCE));

        var fluidHandling = RaycastContext.FluidHandling.NONE;
        var shapeType = RaycastContext.ShapeType.COLLIDER;
        if(KeyUtil.isShiftPressed()) {
            fluidHandling = RaycastContext.FluidHandling.ANY;
            shapeType = RaycastContext.ShapeType.OUTLINE;
        }

        BlockHitResult raycast = world.raycast(new RaycastContext(pos, pos2, shapeType, fluidHandling, context));
        return raycast.getBlockPos();
    }

}
