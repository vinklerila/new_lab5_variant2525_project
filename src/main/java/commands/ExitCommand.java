package commands;

import util.ExecutionContext;

/**
 * Команда завершения программы без сохранения в файл.
 */
public final class ExitCommand implements Command {
    @Override
    public String getName() {
        return "exit";
    }

    @Override
    public String getDescription() {
        return "завершить программу без сохранения в файл";
    }

    @Override
    public boolean execute(String arguments, ExecutionContext context) {
        if (!arguments.isBlank()) {
            context.getOut().error("Команда exit не принимает аргументы.");
            return false;
        }

        context.getOut().println("Завершение программы без сохранения.");
        return true;
    }
}
