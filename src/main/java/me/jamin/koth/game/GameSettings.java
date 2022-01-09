package me.jamin.koth.game;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * An instance of game settings to control certain aspects
 */
@NoArgsConstructor
public class GameSettings {

    /**
     * Game run time (ticks)
     */
    public int GAME_TIME = 900 * 20;

    /**
     * Time that the game is in the starting state before
     * it actually begins (ticks)
     */
    public int GAME_START_TIME = 60 * 20;

}
