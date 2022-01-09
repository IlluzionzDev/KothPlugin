package me.jamin.koth;

import me.jamin.koth.command.KothCommand;
import me.jamin.koth.command.manager.CommandController;
import me.jamin.koth.game.GameController;
import me.jamin.koth.game.arena.ArenaController;
import me.jamin.koth.kit.KitController;
import me.jamin.koth.scheduler.MinecraftScheduler;
import me.jamin.koth.scheduler.bukkit.BukkitScheduler;
import me.jamin.koth.ui.InterfaceController;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

/**
 * The main plugin class
 */
public class KothPlugin extends JavaPlugin {

    /**
     * Singleton instance of our {@link KothPlugin}
     */
    private static volatile KothPlugin INSTANCE;

    /**
     * Return our instance of the {@link KothPlugin}
     *
     * Should be overridden in your own {@link KothPlugin} class
     * as a way to implement your own methods per plugin
     *
     * @return This instance of the plugin
     */
    public static KothPlugin getInstance() {
        // Assign if null
        if (INSTANCE == null) {
            INSTANCE = JavaPlugin.getPlugin(KothPlugin.class);

            Objects.requireNonNull(INSTANCE, "Cannot create instance of plugin. Did you reload?");
        }

        return INSTANCE;
    }

    @Override
    public void onEnable() {
        // Scheduler
        new BukkitScheduler(this).initialize();

        // Setup controllers
        CommandController.INSTANCE.initialize(this);
        GameController.INSTANCE.initialize(this);
        KitController.INSTANCE.initialize(this);
        InterfaceController.INSTANCE.initialize(this);
        ArenaController.INSTANCE.initialize(this);

        CommandController.INSTANCE.register(new KothCommand());
    }

    @Override
    public void onDisable() {
        // Stop scheduler
        MinecraftScheduler.get().stopInvocation();
    }

}
