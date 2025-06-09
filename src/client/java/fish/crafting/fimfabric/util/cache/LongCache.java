package fish.crafting.fimfabric.util.cache;

public class LongCache<Value> extends CustomCache<Long, Value> {

    public LongCache(long initialKey, Value initialValue) {
        super(initialKey, initialValue);
    }

    @Override
    protected boolean isNewKeyValid(Long current, Long newKey) {
        return false;
    }
}
