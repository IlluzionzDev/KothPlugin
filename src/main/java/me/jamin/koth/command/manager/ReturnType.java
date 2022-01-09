package me.jamin.koth.command.manager;

/**
 * Return types for a command
 */
public enum ReturnType {

    /**
     * Yay! The command worked perfectly
     */
    SUCCESS,

    /**
     * Encountered an error, display error executing command
     */
    ERROR,

    /**
     * Arguments were given wrongly
     */
    INVALID_ARGUMENTS,

    /**
     * Attempting to find a player failed
     */
    PLAYER_NOT_FOUND,

    /**
     * If any reason the command is used for both
     * console and player, we may need to say when
     * a certain point of the command can only be used
     * for players
     */
    PLAYER_ONLY

}
