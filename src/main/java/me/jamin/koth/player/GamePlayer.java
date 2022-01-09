package me.jamin.koth.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.jamin.koth.game.instance.KothInstance;
import me.jamin.koth.kit.PlayerKit;
import me.jamin.koth.scheduler.MinecraftScheduler;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * A custom instance of a player that is in our game. We can store instance data
 * on here such as team, kits, kills etc. This will be created when a player joins the KoTH so
 * instances will be assigned to the KoTH instance
 */
@RequiredArgsConstructor
public class GamePlayer {

    /**
     * UUID of the player so we can refer to the player object
     */
    @Getter
    private final UUID uuid;

    /**
     * The team the player is on
     */
    @Getter
    @Setter
    private Team team = Team.UNASSIGNED;

    /**
     * Kit player will use
     */
    @Getter
    @Setter
    private PlayerKit selectedKit;

    // Game player is apart of
    @Getter
    @Setter
    private KothInstance game;

    // Stats
    @Getter
    @Setter
    private int kills = 0;
    @Getter
    @Setter
    private int deaths = 0;
    /**
     * Score gained by this player
     */
    @Getter
    @Setter
    private int score = 0;

    /**
     * Handle respawn of the player
     */
    public void respawn() {
        getPlayer().setGameMode(GameMode.SPECTATOR);

        // Respawn after 5 seconds
        MinecraftScheduler.get().synchronize(() -> {
            getPlayer().setGameMode(GameMode.SURVIVAL);
            getPlayer().setHealth(20);
            getPlayer().teleport(game.getArena().getSpawnLocations().get(team));

            // Reapply kit
            getSelectedKit().applyKit(this);
        }, 5 * 20);
    }

    /**
     * @return The player object
     */
    public Player getPlayer() {
        return Bukkit.getPlayer(this.uuid);
    }

}
