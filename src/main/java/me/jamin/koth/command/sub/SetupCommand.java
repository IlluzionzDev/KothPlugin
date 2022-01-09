package me.jamin.koth.command.sub;

import me.jamin.koth.command.manager.ReturnType;
import me.jamin.koth.command.manager.type.AbstractCommand;
import me.jamin.koth.game.arena.ArenaController;
import me.jamin.koth.player.Team;
import me.jamin.koth.util.MistString;

/**
 * Command to setup arena by setting certain locations of the arena
 */
public class SetupCommand extends AbstractCommand {

    public SetupCommand() {
        super("setloc");
    }

    @Override
    public ReturnType onCommand(String label, String[] args) {
        if (args.length <= 1) {
            new MistString("&cLocations to set are, bluespawn|redspawn|capture1|capture2").sendMessage(player);
            return ReturnType.SUCCESS;
        }

        // Get location to set
        String locationToSet = args[1];

        // Chain to set locations based on args
        if (locationToSet.equalsIgnoreCase("bluespawn")) {
            ArenaController.INSTANCE.setSpawnLocation(Team.BLUE, player.getLocation());
            new MistString("&aSet location").sendMessage(player);
        } else if (locationToSet.equalsIgnoreCase("redspawn")) {
            ArenaController.INSTANCE.setSpawnLocation(Team.RED, player.getLocation());
            new MistString("&aSet location").sendMessage(player);
        } else if (locationToSet.equalsIgnoreCase("capture1")) {
            ArenaController.INSTANCE.setCaptureRegion1(player.getLocation().clone().subtract(0, 1, 0));
            new MistString("&aSet location").sendMessage(player);
        } else if (locationToSet.equalsIgnoreCase("capture2")) {
            ArenaController.INSTANCE.setCaptureRegion2(player.getLocation().clone().subtract(0, 1, 0));
            new MistString("&aSet location").sendMessage(player);
        } else {
            new MistString("&cNot a valid location").sendMessage(player);
        }

        return ReturnType.SUCCESS;
    }

    @Override
    public boolean isConsoleAllowed() {
        return false;
    }
}
