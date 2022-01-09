package me.jamin.koth.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Indicates the team the player is on
 */
@RequiredArgsConstructor
public enum Team {

    RED("&cRed"),
    BLUE("&9Blue"),
    UNASSIGNED("&7None");

    /**
     * Display name of the team
     */
    @Getter
    private final String name;

}
