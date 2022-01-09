package me.jamin.koth.game.instance;

/**
 * Indicates the state the game is in
 */
public enum GameState {

    /**
     * Indicates in starting phase where players can select stuff
     */
    STARTING,

    /**
     * The game is currently running
     */
    RUNNING,

    /**
     * The game is now over
     */
    ENDED

}
