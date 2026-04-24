package commands;

import util.ExecutionContext;

/**
 * Команда сохранения коллекции в файл.
 */
public final class SaveCommand implements Command {
    @Override
    public String getName() {
        return "save";
    }

    @Override
    public String getDescription() {
        return "сохранить коллекцию в файл";
    }

    @Override
    public boolean execute(String arguments, ExecutionContext context) {
        if (!arguments.isBlank()) {
            context.getOut().error("Команда save не принимает аргументы.");
            return false;
        }

        context.getCollectionManager().save();
        return false;
    }
}
