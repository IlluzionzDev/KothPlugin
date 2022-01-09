package me.jamin.koth.scheduler;

/**
 * Represents an object that can be ticked. Errors and everything
 * else will be handled in the ticker
 */
public interface Tickable {

    /**
     * Calls tick operation
     * Should be ran safely!
     */
    void tick();

}
