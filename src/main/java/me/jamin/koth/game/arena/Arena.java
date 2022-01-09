package me.jamin.koth.game.arena;

import lombok.Getter;
import lombok.Setter;
import me.jamin.koth.player.Team;
import me.jamin.koth.util.Region;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

/**
 * Store information about a game arena such as spawn locations etc. Just holds
 * map information really so we can spawn stuff in certain places
 */
public class Arena {

    /**
     * Map of team spawn locations for when they die etc
     */
    @Getter
    public final Map<Team, Location> spawnLocations = new HashMap<>();

    /**
     * Actual region player needs to be in to capture
     */
    @Getter
    @Setter
    public Region captureRegion;

}
