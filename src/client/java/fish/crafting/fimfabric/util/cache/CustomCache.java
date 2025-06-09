package fish.crafting.fimfabric.util.cache;

import java.util.function.Supplier;

public abstract class CustomCache<Key, Value> {

    protected Key key;
    protected Value value;

    public CustomCache(Key initialKey, Value initialValue){
        this.key = initialKey;
        this.value = initialValue;
    }

    public boolean isCachedForKey(Key key){
        return !isNewKeyValid(this.key, key);
    }

    public Value store(Key key, Value value){
        this.key = key;
        return this.value = value;
    }

    public Value get(){
        return value;
    }

    public Value computeIfAbsent(Key key, Supplier<Value> valueSupplier){
        if(isCachedForKey(key)) return get();
        return store(key, valueSupplier.get());
    }

    protected abstract boolean isNewKeyValid(Key current, Key newKey);

}
