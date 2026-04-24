package commands;

import managers.CommandManager;
import util.ExecutionContext;

/**
 * Команда вывода справки по доступным командам.
 */
public final class HelpCommand implements Command {
    private final CommandManager commandManager;

    public HelpCommand(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "вывести справку по доступным командам";
    }

    @Override
    public boolean execute(String arguments, ExecutionContext context) {
        if (!arguments.isBlank()) {
            context.getOut().error("Команда help не принимает аргументы.");
            return false;
        }

        context.getOut().println("Доступные команды:");
        for (Command command : commandManager.getCommands()) {
            context.getOut().println("  " + command.getName() + " : " + command.getDescription());
        }
        return false;
    }
}
