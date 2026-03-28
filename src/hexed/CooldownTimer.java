package hexed;

/**
 * Very small replacement for Timekeeper.
 * Uses System.nanoTime() which is the most accurate and lowest-overhead timer in Java.
 */
public class CooldownTimer {

    private final long cooldownNanos;
    private long lastTime;

    public CooldownTimer(long cooldownSeconds){
        this.cooldownNanos = cooldownSeconds * 1_000_000_000L;
        this.lastTime = 0L;
    }

    /** Returns true if the cooldown has expired */
    public boolean ready(){
        long now = System.nanoTime();
        return now - lastTime >= cooldownNanos;
    }

    /** Same idea as Timekeeper.get() */
    public boolean get(){
        return ready();
    }

    /** Starts/restarts the cooldown */
    public void reset(){
        lastTime = System.nanoTime();
    }

    /** Optional: how many seconds remaining */
    public long secondsRemaining(){
        long remaining = cooldownNanos - (System.nanoTime() - lastTime);
        return Math.max(0, remaining / 1_000_000_000L);
    }
}