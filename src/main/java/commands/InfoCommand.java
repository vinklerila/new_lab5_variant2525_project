package commands;

import util.ExecutionContext;

/**
 * Команда вывода информации о коллекции.
 */
public final class InfoCommand implements Command {
    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return "вывести информацию о коллекции";
    }

    @Override
    public boolean execute(String arguments, ExecutionContext context) {
        if (!arguments.isBlank()) {
            context.getOut().error("Команда info не принимает аргументы.");
            return false;
        }

        context.getOut().println(context.getCollectionManager().buildInfo());
        return false;
    }
}
