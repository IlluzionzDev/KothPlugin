package me.jamin.koth.game.instance;

import com.cryptomorin.xseries.XSound;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.jamin.koth.game.GameController;
import me.jamin.koth.game.GameInstance;
import me.jamin.koth.game.GameSettings;
import me.jamin.koth.game.arena.Arena;
import me.jamin.koth.kit.KitController;
import me.jamin.koth.kit.KitInterface;
import me.jamin.koth.player.GamePlayer;
import me.jamin.koth.player.Team;
import me.jamin.koth.scheduler.MinecraftScheduler;
import me.jamin.koth.scheduler.timer.Cooldown;
import me.jamin.koth.scheduler.timer.PresetCooldown;
import me.jamin.koth.scoreboard.KothScoreboard;
import me.jamin.koth.util.Logger;
import me.jamin.koth.util.MistString;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * An instance of a koth game that tracks all information for the instance. Also
 * handles points, checks etc. This is disposed at the end of a game. Also handles all
 * major game logic.
 *
 * When this game is started it gives a brief period of time for players to select their team etc
 */
public class KothInstance implements GameInstance {

    /**
     * Id of this game instance
     */
    @Getter
    private final UUID uuid;

    /**
     * Arena for this game
     */
    @Getter
    private final Arena arena;

    /**
     * Settings for the game
     */
    @Getter
    private final GameSettings settings;

    /**
     * Period of time where the game is starting
     */
    @Getter
    private final PresetCooldown startTime;

    /**
     * Period of time the game occurs in
     */
    @Getter
    private final PresetCooldown gameTime;

    // Score Keeping

    /**
     * A map of teams scores. Is incremented for every second a team is capturing
     * the region
     */
    @Getter
    private final Map<Team, Integer> teamScore = new HashMap<>();

    /**
     * The current team capturing the koth, in order for it to change their must
     * be no players of the capturing team on the koth
     */
    @Getter
    private Team capturingTeam = Team.UNASSIGNED;

    public KothInstance(final UUID uuid, final Arena arena, final GameSettings settings) {
        this.uuid = uuid;
        this.arena = arena;
        this.settings = settings;

        // Assign countdowns for certain times in the game
        this.startTime = new PresetCooldown(settings.GAME_START_TIME);
        this.gameTime = new PresetCooldown(settings.GAME_TIME + settings.GAME_START_TIME);

        // Start game
        this.startTime.go();
        this.gameTime.go();

        // Default score
        for (Team value : Team.values()) {
            teamScore.put(value, 0);
        }
    }

    /**
     * Indicates what state the game is in
     */
    @Getter
    private GameState state = GameState.STARTING;

    /**
     * Called on creation of game
     */
    public void startGame() {
        Logger.debug(arena.getCaptureRegion());
        getPlayers().forEach(player -> {
            // Assign game
            player.setGame(this);

            new MistString("&4&lKOTH &cStarting a new koth game with {players} players!").toString("players", getPlayers().size()).sendMessage(player.getPlayer());

            // Assign team
            Team toAssign = GameController.INSTANCE.pickTeam();
            player.setTeam(toAssign);

            Location spawnPoint = arena.spawnLocations.get(player.getTeam());
            player.getPlayer().teleport(spawnPoint);

            // Make sure player is in the right mode etc
            player.getPlayer().setGameMode(GameMode.SURVIVAL);
            player.getPlayer().setAllowFlight(false);
            player.getPlayer().setHealth(20);
            player.getPlayer().getInventory().clear();

            player.setSelectedKit(KitController.INSTANCE.getLoadedKits().get("warrior"));
            new KitInterface(player).show(player.getPlayer());

            MinecraftScheduler.get().registerSynchronizationService(new KothScoreboard(player));
        });
    }

    /**
     * Called when the game is over
     */
    public void endGame() {
        int winningTeam = teamScore.get(Team.BLUE).compareTo(teamScore.get(Team.RED));
        Team winner = winningTeam > 0 ? Team.BLUE : Team.RED;

        getPlayers().forEach(player -> {
            new MistString("&4&lKOTH &cGame Over! {team} &cteam won!").toString("team", winner.getName()).sendMessage(player.getPlayer());
            XSound.ENTITY_ENDER_DRAGON_GROWL.play(player.getPlayer());

            player.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        });

        // Rewards
        getPlayers(winner).forEach(player -> {
            player.getPlayer().getInventory().addItem(new ItemStack(Material.DIAMOND, 5));
        });

        GameController.INSTANCE.disposeGame(this.uuid);
    }

    /**
     * Tick the game instance
     */
    public void tick() {
        // End game if time has expired
        if (startTime.isReady() && gameTime.isReady()) {
            this.state = GameState.ENDED;
            endGame();
        }

        // Action based on state
        if (state == GameState.STARTING) {
            // Start phase over
            if (startTime.isReady() && !gameTime.isReady()) {
                getPlayers().forEach(player -> {
                    new MistString("&4&lKOTH &cLet the games begin!").sendMessage(player.getPlayer());

                    // Spawn at team spawnpoint
                    Location spawnPoint = arena.spawnLocations.get(player.getTeam());
                    player.getPlayer().teleport(spawnPoint);

                    // Make sure doesn't have selection gui open
                    player.getPlayer().closeInventory();

                    // Spawn with kit
                    player.getSelectedKit().applyKit(player);
                });

                state = GameState.RUNNING;
            }

            // Kit selection logic
            if (MinecraftScheduler.get().hasElapsed(20) && startTime.getTickLeft() <= 10 * 20) {
                getPlayers().forEach(player -> {
                    new MistString("&4&lKOTH &cGame starting in " + startTime.getFormattedTimeLeft(false)).sendMessage(player.getPlayer());
                    XSound.BLOCK_STONE_BUTTON_CLICK_ON.play(player.getPlayer());
                });
            }
        } else if (state == GameState.RUNNING) {
            if (MinecraftScheduler.get().hasElapsed(20)) {
                // Capture logic
                getPlayers().forEach(player -> {
                    if (arena.getCaptureRegion().inRegion(player.getPlayer().getLocation())) {
                        // Attempt capture
                        if (player.getTeam() != capturingTeam) {
                            // Check if any players of opposite team are on
                            if (getPlayers().stream().noneMatch(p -> isPlayerCapturing(p) && p.getTeam() == capturingTeam)) {
                                GameController.INSTANCE.gamePlayers.get(this.uuid).forEach(all -> {
                                    new MistString("&4&lKOTH {team} is capturing the koth!").toString("team", player.getTeam().getName()).sendMessage(all.getPlayer());
                                });

                                capturingTeam = player.getTeam();
                            }
                        } else if (player.getTeam() == capturingTeam) {
                            // Part of capturing team
                            player.setScore(player.getScore() + 1);
                        }
                    }
                });

                // Increment score
                incrementScore(capturingTeam);
            }
        }
    }

    /**
     * Increase score for a team
     */
    private void incrementScore(final Team team) {
        int currentScore = teamScore.getOrDefault(team, 0);
        teamScore.put(team, currentScore + 1);
    }

    /**
     * See if player is trying to capture koth
     */
    public boolean isPlayerCapturing(final GamePlayer player) {
        boolean inArena = arena.getCaptureRegion().inRegion(player.getPlayer().getLocation());
        boolean notDead = player.getPlayer().getGameMode() == GameMode.SURVIVAL;

        return inArena && notDead;
    }

    /**
     * Get a player from this game by UUID
     */
    public GamePlayer getPlayer(final UUID uuid) {
        return getPlayers().stream().filter(player -> player.getPlayer().getUniqueId().equals(uuid)).findAny().get();
    }

    /**
     * Get players in a team
     */
    public Set<GamePlayer> getPlayers(final Team team) {
        return getPlayers().stream().filter(player -> player.getTeam() == team).collect(Collectors.toSet());
    }

    public Set<GamePlayer> getPlayers() {
        return GameController.INSTANCE.gamePlayers.get(this.uuid);
    }

}
