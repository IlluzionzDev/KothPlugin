package me.jamin.koth.util;

import me.jamin.koth.KothPlugin;

import java.util.logging.Level;

/**
 * Plugin logger to handle logging
 */
public final class Logger {

    //  -------------------------------------------------------------------------
    //  Main logging functions
    //  -------------------------------------------------------------------------

    /**
     * Debug an object as output
     *
     * @param message Prints the string version of on object as debug
     */
    public static void debug(Object message) {
        log(Level.WARNING, "[DEBUG] " + message);
    }

    /**
     * Print debug messages to the console. Warns as their
     * easier to see
     *
     * @param message Message to send
     * @param parameters Formatting parameters
     */
    public static void debug(String message, Object... parameters) {
        log(Level.WARNING, "[DEBUG] " + message, parameters);
    }

    /**
     * Send a warning to the console
     *
     * @param message Message to send
     * @param parameters Formatting parameters
     */
    public static void warn(String message, Object... parameters) {
        log(Level.WARNING, "[WARN] " + message, parameters);
    }

    /**
     * Log a error to the console
     *
     * @param message Message to send
     * @param parameters Formatting parameters
     */
    public static void severe(String message, Object... parameters) {
        log(Level.SEVERE, "[SEVERE] " + message, parameters);
    }

    /**
     * Basic method to report information to the console
     *
     * @param message Message to send
     * @param parameters Formatting parameters
     */
    public static void info(String message, Object... parameters) {
        log(Level.INFO, message, parameters);
    }

    /**
     * Base method to log output to the console at a certain logging level
     *
     * @param level Logging level
     * @param message The object/message to log
     * @param parameters Formatting parameters
     */
    private static void log(Level level, Object message, Object... parameters) {
        KothPlugin.getInstance().getLogger().log(level, String.format(message.toString(), parameters));
    }

}
