package me.jamin.koth.scheduler.timer;

import lombok.NoArgsConstructor;
import lombok.ToString;
import me.jamin.koth.scheduler.MinecraftScheduler;
import me.jamin.koth.util.TextUtil;

/**
 * A set cooldown that checks if a set amount of time has passed
 */
@ToString
@NoArgsConstructor
public class Cooldown {

    /**
     * Represents the time (in ticks) when the time will expire
     */
    private long expireTicks = 0;

    /**
     * Represents the time (in mills) when the time will expire
     */
    private long expireTime = 0;

    /**
     * @param ticks How many ticks from current time will expire
     */
    public Cooldown(int ticks) {
        setWait(ticks);
    }

    /**
     * @param ticks Set in x ticks after current time, {@link #isReady()}
     */
    public void setWait(int ticks) {
        this.expireTicks = MinecraftScheduler.getCurrentTick() + ticks;
        this.expireTime = System.currentTimeMillis() + (ticks * 50);
    }

    /**
     * Reset the timer to check for time again
     */
    public void reset() {
        this.expireTicks = 0;
    }

    /**
     * @return If the set time has passed as ticks
     */
    public boolean isReady() {
        return getTickLeft() <= 0;
    }

    /**
     * @return If set millis time has passed
     */
    public boolean isReadyRealTime() {
        return getMillisecondsLeft() <= 0;
    }

    /**
     * @return Get millis left before expire time
     */
    private long getMillisecondsLeft() {
        return expireTime - System.currentTimeMillis();
    }

    /**
     * @return Get ticks left before expire time
     */
    public long getTickLeft() {
        return expireTicks - MinecraftScheduler.getCurrentTick();
    }

    /**
     * Formatted version of {@link #getMillisecondsLeft()}
     *
     * @param verbose If to set full name for scale, eg if true
     *                "days" over "d"
     * @return The formatted time as a {@link String}
     */
    public String getFormattedTimeLeft(boolean verbose) {
        return TextUtil.getFormattedTime(getMillisecondsLeft(), verbose);
    }

}
