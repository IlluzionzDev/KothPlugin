package me.jamin.koth.command;

import me.jamin.koth.command.manager.ReturnType;
import me.jamin.koth.command.manager.type.AbstractCommand;
import me.jamin.koth.command.sub.SetupCommand;
import me.jamin.koth.command.sub.StartCommand;

/**
 * Main koth command
 */
public class KothCommand extends AbstractCommand {

    public KothCommand() {
        super("koth");

        setUsage("&c/koth <start|setup>");
        minArgs = 1;
        addSubCommand(new StartCommand());
        addSubCommand(new SetupCommand());
    }

    @Override
    public ReturnType onCommand(String label, String[] args) {
        return ReturnType.SUCCESS;
    }

    @Override
    public boolean isConsoleAllowed() {
        return false;
    }
}

