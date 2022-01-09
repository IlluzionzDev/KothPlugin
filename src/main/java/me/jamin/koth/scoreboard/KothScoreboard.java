package me.jamin.koth.scoreboard;

import me.jamin.koth.player.GamePlayer;
import me.jamin.koth.util.MistString;

/**
 * Scoreboard for the koth game
 */
public class KothScoreboard extends GameScoreboard {

    public KothScoreboard(final GamePlayer player) {
        super(player);
        updateDisplayName("&4&lKOTH");
    }

    @Override
    public void updateText() {
        lines.add("&c&lPlayer");
        lines.add("  &7Team: " + player.getTeam().getName());
        lines.add("  &7Team Score: " + player.getGame().getTeamScore().get(player.getTeam()));
        lines.add("  &7Kills: " + player.getKills());
        lines.add("  &7Deaths: " + player.getDeaths());
        lines.add("&r ");

        lines.add("&c&lGame");
        if (player.getGame().getStartTime().isReady())
            lines.add("  &7Time Left: " + player.getGame().getGameTime().getFormattedTimeLeft(false));
        else
            lines.add("  &7Starting in " + player.getGame().getStartTime().getFormattedTimeLeft(false));
        lines.add("  &7Capturing: " + player.getGame().getCapturingTeam().getName());


    }
}
