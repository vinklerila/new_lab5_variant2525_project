package commands;

import util.ExecutionContext;

/**
 * Команда очистки коллекции.
 */
public final class ClearCommand implements Command {
    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public String getDescription() {
        return "очистить коллекцию";
    }

    @Override
    public boolean execute(String arguments, ExecutionContext context) {
        if (!arguments.isBlank()) {
            context.getOut().error("Команда clear не принимает аргументы.");
            return false;
        }

        context.getCollectionManager().clear();
        context.getOut().println("Коллекция очищена.");
        return false;
    }
}
