package me.jamin.koth.game;

import me.jamin.koth.player.GamePlayer;
import me.jamin.koth.scheduler.Tickable;

/**
 * Provides a game instance where we handle game logic
 */
public interface GameInstance extends Tickable {

    void startGame();
    void endGame();

}
