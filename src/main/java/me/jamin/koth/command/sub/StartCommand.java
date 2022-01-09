package me.jamin.koth.command.sub;

import me.jamin.koth.command.manager.ReturnType;
import me.jamin.koth.command.manager.type.AbstractCommand;
import me.jamin.koth.game.GameController;
import me.jamin.koth.game.GameSettings;
import me.jamin.koth.game.arena.Arena;
import me.jamin.koth.game.arena.ArenaController;
import me.jamin.koth.util.MistString;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class StartCommand extends AbstractCommand {

    public StartCommand() {
        super("start");
    }

    @Override
    public ReturnType onCommand(String label, String[] args) {
        Arena arena = ArenaController.INSTANCE.getKothArena();

        if (arena.getCaptureRegion() == null) {
            new MistString("&cYou must setup an arena before starting the game!").sendMessage(player);
            return ReturnType.SUCCESS;
        }

        GameController.INSTANCE.createGame(arena, new GameSettings(), Bukkit.getOnlinePlayers().toArray(Player[]::new));
        return ReturnType.SUCCESS;
    }

    @Override
    public boolean isConsoleAllowed() {
        return true;
    }
}
