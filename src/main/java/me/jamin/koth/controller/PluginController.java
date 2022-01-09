package me.jamin.koth.controller;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * An instance of a controller that handles some sort of internal logic
 */
public interface PluginController extends Listener {

    /**
     * Starts up our controller
     *
     * @param plugin The plugin starting the controller
     */
    void initialize(final Plugin plugin);

    /**
     * Stops our controller
     *
     * @param plugin The plugin stopping the controller
     */
    void stop(final Plugin plugin);

}
