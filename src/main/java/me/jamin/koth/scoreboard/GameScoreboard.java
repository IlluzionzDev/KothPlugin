package me.jamin.koth.scoreboard;

import lombok.Getter;
import me.jamin.koth.player.GamePlayer;
import me.jamin.koth.scheduler.Tickable;
import me.jamin.koth.scheduler.rate.Sync;
import me.jamin.koth.util.MistString;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

/**
 * Represents the scoreboard for the game
 */
public abstract class GameScoreboard implements Tickable {

    /**
     * Line cache
     */
    protected final Map<Integer, String> sidebarTexts = new HashMap<>();

    /**
     * Lines to display for the scoreboard
     */
    protected final List<String> lines = new ArrayList<>();

    @Getter
    protected Scoreboard scoreboard;

    protected final GamePlayer player;

    public GameScoreboard(final GamePlayer player) {
        this.player = player;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("game", "dummy");
        objective.setDisplayName(new MistString("&4&lKOTH").toString());
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        player.getPlayer().setScoreboard(scoreboard);
    }

    public void clearScore(String name) {
        scoreboard.resetScores(name);
    }

    public void clearSidebarSlot(int slot) {
        String text = sidebarTexts.remove(slot);
        if (text != null) {
            clearScore(text);
        }
    }

    public void updateDisplayName(final String text) {
        Objective existing = scoreboard.getObjective(DisplaySlot.SIDEBAR);
        if (existing != null) {
            existing.setDisplayName(new MistString(text).toString());
        }
    }

    public void updateText(int slot, String text) {
        Objective existing = scoreboard.getObjective(DisplaySlot.SIDEBAR);
        if (existing != null) {
            String oldText = sidebarTexts.put(slot, text);
            // Don't update
            if (text.equals(oldText)) return;
            if (oldText != null) clearScore(oldText);
            existing.getScore(new MistString(text).toString()).setScore(slot);
        }
    }

    @Sync
    @Override
    public void tick() {
        lines.clear();
        updateText();

        // Display in correct order
        List<String> reversedLines = lines;
        Collections.reverse(reversedLines);
        for (int i = 0; i < lines.size(); i++) {
            updateText(i, new MistString(reversedLines.get(i)).toString());
        }
    }

    /**
     * Dynamic set lines
     */
    public abstract void updateText();

}
