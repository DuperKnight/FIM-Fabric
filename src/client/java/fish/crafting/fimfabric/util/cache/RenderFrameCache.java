package fish.crafting.fimfabric.util.cache;

import fish.crafting.fimfabric.rendering.world.WorldRenderingManager;

import java.util.Objects;
import java.util.function.Supplier;

public class RenderFrameCache<Value> extends CustomCache<Integer, Value> {

    public RenderFrameCache(Value initialValue) {
        super(-1, initialValue);
    }

    public Value computeIfAbsent(Supplier<Value> value){
        return computeIfAbsent(WorldRenderingManager.RENDER_FRAME, value);
    }

    @Override
    protected boolean isNewKeyValid(Integer current, Integer newKey) {
        return !Objects.equals(newKey, current);
    }
}
