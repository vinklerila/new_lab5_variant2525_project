package util;

import io.InputManager;
import io.OutputManager;
import managers.CollectionManager;
import managers.CommandManager;

/**
 * Контекст выполнения команды.
 */
public final class ExecutionContext {
    private final InputManager in;
    private final OutputManager out;
    private final CollectionManager collectionManager;
    private final CommandManager commandManager;

    public ExecutionContext(
            InputManager in,
            OutputManager out,
            CollectionManager collectionManager,
            CommandManager commandManager
    ) {
        this.in = in;
        this.out = out;
        this.collectionManager = collectionManager;
        this.commandManager = commandManager;
    }

    public InputManager getIn() {
        return in;
    }

    public OutputManager getOut() {
        return out;
    }

    public CollectionManager getCollectionManager() {
        return collectionManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }
}
