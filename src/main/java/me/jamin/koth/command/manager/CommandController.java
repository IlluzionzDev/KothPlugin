package me.jamin.koth.command.manager;

import me.jamin.koth.command.manager.type.AbstractCommand;
import me.jamin.koth.controller.PluginController;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Field;
import java.util.HashMap;

public enum CommandController implements PluginController {

    INSTANCE;

    @Override
    public void initialize(Plugin plugin) {
    }

    @Override
    public void stop(Plugin plugin) {
    }

    /**
     * Registers a command
     *
     * @param command Base command
     */
    public void register(AbstractCommand command) {
        try {
            // Prepare command map
            Field cMap = SimplePluginManager.class.getDeclaredField("commandMap");
            cMap.setAccessible(true);
            CommandMap map = (CommandMap) cMap.get(Bukkit.getPluginManager());


            // Remove existing commands
            Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");

            knownCommandsField.setAccessible(true);
            HashMap<String, Command> knownCommands = (HashMap<String, Command>) knownCommandsField.get(map);
            knownCommandsField.setAccessible(false);

            if (map.getCommand(command.getLabel()) != null) {
                knownCommands.remove(command.getLabel());
            }

            for (String alias : command.getAliases()) {
                if (map.getCommand(alias) != null) {
                    knownCommands.remove(alias);
                }
            }

            // Register the new command & aliases
            map.register(command.getLabel(), command);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
