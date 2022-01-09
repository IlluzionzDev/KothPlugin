package me.jamin.koth.game.arena;

import lombok.Getter;
import me.jamin.koth.controller.PluginController;
import me.jamin.koth.player.Team;
import me.jamin.koth.util.Region;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

/**
 * Handle setting the arena
 */
public enum ArenaController implements PluginController {
    INSTANCE;

    /**
     * Arena to use
     */
    @Getter
    private final Arena kothArena = new Arena();

    @Override
    public void initialize(Plugin plugin) {
    }

    @Override
    public void stop(Plugin plugin) {
    }

    public void setSpawnLocation(final Team team, final Location location) {
        this.kothArena.getSpawnLocations().put(team, location);
    }

    public void setCaptureRegion1(final Location location) {
        Region region = kothArena.getCaptureRegion() == null ? new Region() : kothArena.getCaptureRegion();
        region.setFirstLocation(location);
        this.kothArena.setCaptureRegion(region);
    }

    public void setCaptureRegion2(final Location location) {
        Region region = kothArena.getCaptureRegion() == null ? new Region() : kothArena.getCaptureRegion();
        region.setSecondLocation(location);
        this.kothArena.setCaptureRegion(region);
    }
}
