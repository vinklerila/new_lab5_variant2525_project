package managers;

import commands.Command;
import io.OutputManager;
import util.ExecutionContext;
import util.InputCancelledException;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Менеджер регистрации и исполнения консольных команд.
 */
public final class CommandManager {
    private final Map<String, Command> commands = new LinkedHashMap<>();
    private final OutputManager out;

    public CommandManager(OutputManager out) {
        this.out = out;
    }

    public void register(Command command) {
        commands.put(command.getName(), command);
    }

    public Collection<Command> getCommands() {
        return commands.values();
    }

    public boolean executeLine(String line, ExecutionContext context) {
        String[] parts = line.split("\\s+", 2);
        String commandName = parts[0];
        String arguments = parts.length > 1 ? parts[1] : "";

        Command command = commands.get(commandName);
        if (command == null) {
            out.error("Неизвестная команда: " + commandName);
            out.error("Введите help для просмотра списка доступных команд.");
            return false;
        }

        try {
            return command.execute(arguments, context);
        } catch (InputCancelledException exception) {
            out.error("Команда прервана: " + exception.getMessage());
            return false;
        } catch (IllegalArgumentException exception) {
            out.error("Ошибка: " + exception.getMessage());
            return false;
        }
    }
}
