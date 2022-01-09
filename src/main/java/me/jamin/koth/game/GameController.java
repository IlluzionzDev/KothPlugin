package me.jamin.koth.game;

import com.cryptomorin.xseries.XSound;
import lombok.Getter;
import me.jamin.koth.controller.PluginController;
import me.jamin.koth.game.arena.Arena;
import me.jamin.koth.game.instance.GameState;
import me.jamin.koth.game.instance.KothInstance;
import me.jamin.koth.player.GamePlayer;
import me.jamin.koth.player.Team;
import me.jamin.koth.scheduler.MinecraftScheduler;
import me.jamin.koth.scheduler.rate.Rate;
import me.jamin.koth.scheduler.rate.Sync;
import me.jamin.koth.util.Logger;
import me.jamin.koth.util.LootTable;
import me.jamin.koth.util.MistString;
import me.jamin.koth.util.Valid;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Main controller for game instances and keeping track of things within the game
 */
public enum GameController implements PluginController {
    INSTANCE;

    /**
     * Map of all active instances
     */
    public final Map<UUID, GameInstance> gameInstances = new ConcurrentHashMap<>();

    /**
     * Map of game players for accessing by game
     */
    public final Map<UUID, Set<GamePlayer>> gamePlayers = new ConcurrentHashMap<>();

    /**
     * Indicates which team to use
     */
    private boolean teamPicker = false;

    @Override
    public void initialize(Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        MinecraftScheduler.get().registerSynchronizationService(this);
    }

    @Override
    public void stop(Plugin plugin) {
        MinecraftScheduler.get().dismissSynchronizationService(this);
    }

    /**
     * Handles ticking all game instances
     */
    @Sync(rate = Rate.TICK)
    public void tickInstances() {
        this.gameInstances.forEach((uuid, instance) -> instance.tick());
    }

    public Team pickTeam() {
        Team team = teamPicker ? Team.RED : Team.BLUE;
        teamPicker = !teamPicker;
        return team;
    }

    /**
     * Start up a new game instance
     *
     * @param arena The arena to use
     * @param settings Settings to use for the game
     * @param players Players to add to the game
     */
    public void createGame(final Arena arena, final GameSettings settings, final Player... players) {
        UUID id = UUID.randomUUID();
        KothInstance instance = new KothInstance(id, arena, settings);
        this.gameInstances.put(id, instance);
        gamePlayers.put(id, new HashSet<>());
        for (Player player : players) {
            gamePlayers.get(id).add(new GamePlayer(player.getUniqueId()));
        }
        instance.startGame();
    }

    /**
     * Dispose a game instance
     */
    public void disposeGame(final UUID uuid) {
        Valid.checkBoolean(gameInstances.containsKey(uuid), "Game instance does not exist!");
        gameInstances.remove(uuid);
        gamePlayers.remove(uuid);
    }

    public GamePlayer getPlayer(final UUID uuid) {
        GamePlayer found = null;

        for (Set<GamePlayer> set : gamePlayers.values()) {
            Optional<GamePlayer> lookup = set.stream().filter(gamePlayer -> gamePlayer.getPlayer().getUniqueId() == uuid).findAny();
            if (lookup.isPresent()) {
                found = lookup.get();
            }
        }

        return found;
    }

    // General game events
    @EventHandler
    public void onFoodChange(final FoodLevelChangeEvent event) {
        event.setFoodLevel(20);
        event.setCancelled(true);
    }

    @EventHandler
    public void onDrop(final PlayerDropItemEvent event) {
        GamePlayer player = getPlayer(event.getPlayer().getUniqueId());
        if (player == null) return;

        event.setCancelled(true);
    }

    /**
     * Stop moving armour around
     */
    @EventHandler
    public void onArmourClick(final InventoryClickEvent event) {
        GamePlayer player = getPlayer(event.getWhoClicked().getUniqueId());
        if (player == null) return;

        if (event.getSlotType() == InventoryType.SlotType.ARMOR) event.setCancelled(true);
    }

    /**
     * Make sure team can't damage each other etc
     */
    @EventHandler
    public void onDamage(final EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player || event.getDamager() instanceof Projectile)) return;
        if (!(event.getEntity() instanceof Player)) return;

        GamePlayer damager = getPlayer(event.getDamager().getUniqueId());

        // If projectile
        if (event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
            Projectile projectile = (Projectile) event.getDamager();
            if (projectile.getShooter() instanceof Player) damager = getPlayer(((Player) projectile.getShooter()).getUniqueId());
        }

        GamePlayer player = getPlayer(event.getEntity().getUniqueId());
        if (damager == null || player == null) return;

        // If game starting cancel
        if (damager.getGame().getState() == GameState.STARTING) event.setCancelled(true);

        // If same team
        if (damager.getTeam() == player.getTeam()) event.setCancelled(true);

        // Check if should die and handle respawn and stats
        double damage = event.getFinalDamage();
        boolean shouldDie = player.getPlayer().getHealth() - damage <= 0;
        if (shouldDie) {
            event.setCancelled(true);

            // Stats
            player.setDeaths(player.getDeaths() + 1);
            damager.setKills(damager.getKills() + 1);

            // Death messages
            GamePlayer finalDamager = damager;
            gamePlayers.get(player.getGame().getUuid()).forEach(all -> {
                new MistString("&c{player} was killed by {damager}").toString("player", player.getPlayer().getName()).toString("damager", finalDamager.getPlayer().getName()).sendMessage(all.getPlayer());
            });

            // Death particles
            player.getPlayer().getWorld().spawnParticle(Particle.FIREWORKS_SPARK, player.getPlayer().getLocation(), 10);
            XSound.ENTITY_CHICKEN_DEATH.play(damager.getPlayer());

            // Handle respawn
            player.respawn();
        }
    }

}
