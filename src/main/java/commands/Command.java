package commands;

import util.ExecutionContext;

/**
 * Базовый интерфейс консольной команды.
 */
public interface Command {
    String getName();
    String getDescription();
    boolean execute(String arguments, ExecutionContext context);
}
