package fish.crafting.fimfabric.util;

public class CooldownTracker {

    private long start = Long.MIN_VALUE;
    private final long duration;

    public CooldownTracker(long durationMS){
        this.duration = durationMS;
    }

    public static CooldownTracker seconds(double seconds){
        return new CooldownTracker((long) (seconds * 1000L));
    }

    public boolean isOnCooldown(){
        return System.currentTimeMillis() < cooldownEndedAt();
    }

    private long cooldownEndedAt(){
        return start + duration;
    }

    /**
     * Returns whether this tracker is on cooldown right now.
     * If it's not on cooldown, begin the cooldown.
     */
    public boolean getAndStart(){
        if(isOnCooldown()) return true;

        startCooldown();
        return false;
    }

    public void startCooldown(){
        start = System.currentTimeMillis();
    }

}
