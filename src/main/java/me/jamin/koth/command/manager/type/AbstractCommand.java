package me.jamin.koth.command.manager.type;

import me.jamin.koth.command.manager.ReturnType;
import me.jamin.koth.util.MistString;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Abstract implementation of a command. Quick setup for easy commands
 */
public abstract class AbstractCommand extends Command {

    /**
     * The instance that executed this command
     */
    protected CommandSender commandSender;

    /**
     * This is only set if the {@link #commandSender} is a {@link Player}
     */
    protected Player player;

    /**
     * Minimum arguments required to execute the command
     */
    protected int minArgs = 0;

    /**
     * String list of stored arguments given to the executor
     */
    protected List<String> args = new ArrayList<>();

    /**
     * Unique list of sub command executors
     * run when matches certain argument
     */
    private HashSet<AbstractCommand> subCommands = new HashSet<>();

    public AbstractCommand(String name, String... aliases) {
        super(name, "", "", Arrays.asList(aliases));
    }

    public AbstractCommand(String name) {
        super(name);
    }

    /**
     * Register a new sub command to the command
     *
     * @param command The sub command to add
     */
    public void addSubCommand(AbstractCommand command) {
        this.subCommands.add(command);
    }

    /**
     * Implemented to provide functionality to the command
     *
     * @param label Name of the command
     * @param args Parsed arguments
     * @return The return type for post processing
     */
    public abstract ReturnType onCommand(String label, String[] args);

    /**
     * @return Whether the command can be run by console
     */
    public abstract boolean isConsoleAllowed();

    /**
     * Ran when attempting to execute command
     */
    @Override
    public final boolean execute(CommandSender commandSender, String label, String[] args) {
        // Check if being executed by console
        if (!this.isConsoleAllowed() && commandSender instanceof ConsoleCommandSender) {
            commandSender.sendMessage(ChatColor.DARK_RED + "The console cannot execute this command.");
            return true;
        }

        // Store parsed args
        this.args = Arrays.asList(args);

        // Provided too few arguments
        if (this.args.size() < minArgs) {
            new MistString(getUsage()).sendMessage(commandSender);
            return true;
        }

        // Command instance executing
        // Could be a sub command which variables
        // will be dealt with
        AbstractCommand command = this;

        // Parse a sub command
        if (args.length >= 1) {
            command = findSubCommand(args[0]);

            if (command != null) {
                // Remove the first argument
                String[] newArgs = Arrays.copyOfRange(args, 1, args.length);

                // Set these new arguments to the parsed args
                command.args = Arrays.asList(newArgs);

                // Set instances executing command
                command.commandSender = commandSender;

                if (!(command.commandSender instanceof ConsoleCommandSender)) {
                    command.player = (Player) commandSender;
                }

                // Provided too few sub arguments
                if (newArgs.length < command.minArgs) {
                    new MistString(getUsage()).sendMessage(commandSender);
                    return true;
                }
            }
        } else {
            // Only set local arguments if not a sub command
            this.commandSender = commandSender;

            if (!(commandSender instanceof ConsoleCommandSender)) {
                this.player = (Player) commandSender;
            }
        }

        try {
            // Parse return types
            // Ignoring SUCCESS as we just let the command
            // run as normal
            switch (command.onCommand(label, args)) {
                case ERROR:
                    new MistString("&cA fatal error occurred executing this command").sendMessage(commandSender);
                    break;
                case INVALID_ARGUMENTS:
                    new MistString(getUsage()).sendMessage(commandSender);
                    break;
                case PLAYER_NOT_FOUND:
                    new MistString("&cCould not find that player").sendMessage(commandSender);
                case PLAYER_ONLY:
                    new MistString("&cOnly a player can execute the command like this").sendMessage(commandSender);
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return true;
    }

    /**
     * Find sub command from string
     * By default return this command
     *
     * @param name The name or alias of command
     */
    public AbstractCommand findSubCommand(String name) {
        // Return null if there are no sub commands
        if (this.subCommands == null || this.subCommands.isEmpty()) return this;

        for (AbstractCommand cmd : this.subCommands) {
            // Check if it equals name or alias
            if (cmd.getName().equalsIgnoreCase(name) || cmd.getAliases().contains(name.toLowerCase())) return cmd;
        }

        return this;
    }
}
