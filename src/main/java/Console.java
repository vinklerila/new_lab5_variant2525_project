import commands.AddCommand;
import commands.AddIfMaxCommand;
import commands.AddIfMinCommand;
import commands.ClearCommand;
import commands.ExecuteScriptCommand;
import commands.ExitCommand;
import commands.FilterGreaterThanOwnerCommand;
import commands.FilterLessThanOwnerCommand;
import commands.FilterStartsWithNameCommand;
import commands.HelpCommand;
import commands.InfoCommand;
import commands.RemoveByIdCommand;
import commands.RemoveLowerCommand;
import commands.SaveCommand;
import commands.ShowCommand;
import commands.UpdateCommand;
import io.InputManager;
import io.OutputManager;
import managers.CollectionManager;
import managers.CommandManager;
import managers.FileManager;
import util.ExecutionContext;

import java.nio.file.Path;

/**
 * Консольная оболочка приложения.
 *
 * <p>Класс инициализирует менеджеры приложения, загружает коллекцию из файла, регистрирует
 * доступные команды и запускает основной цикл чтения и исполнения команд.</p>
 */
public final class Console {
    private final String dataFileName;
    private final InputManager in;
    private final OutputManager out;

    /**
     * Создаёт консольную оболочку.
     *
     * @param dataFileName путь к JSON-файлу с коллекцией
     * @param in менеджер ввода
     * @param out менеджер вывода
     */
    public Console(String dataFileName, InputManager in, OutputManager out) {
        this.dataFileName = dataFileName;
        this.in = in;
        this.out = out;
    }

    /**
     * Запускает основной цикл приложения.
     */
    public void run() {
        FileManager fileManager = new FileManager(Path.of(dataFileName), out);
        CollectionManager collectionManager = new CollectionManager(fileManager, out);
        CommandManager commandManager = new CommandManager(out);

        collectionManager.load();
        registerCommands(commandManager);

        ExecutionContext context = new ExecutionContext(in, out, collectionManager, commandManager);

        out.println("Коллекция инициализирована.");
        out.println("Введите 'help' для просмотра списка доступных команд.");

        while (true) {
            if (!in.isScriptMode()) {
                out.print("> ");
            }

            String line = in.readLine();
            if (line == null) {
                out.println("");
                out.println("Ввод завершён. Программа остановлена.");
                break;
            }

            String trimmed = line.trim();
            if (trimmed.isEmpty()) {
                continue;
            }

            boolean shouldExit = commandManager.executeLine(trimmed, context);
            if (shouldExit) {
                break;
            }
        }
    }

    /**
     * Регистрирует команды текущего варианта.
     *
     * @param commandManager менеджер команд
     */
    private void registerCommands(CommandManager commandManager) {
        commandManager.register(new HelpCommand(commandManager));
        commandManager.register(new InfoCommand());
        commandManager.register(new ShowCommand());
        commandManager.register(new AddCommand());
        commandManager.register(new UpdateCommand());
        commandManager.register(new RemoveByIdCommand());
        commandManager.register(new ClearCommand());
        commandManager.register(new SaveCommand());
        commandManager.register(new ExecuteScriptCommand());
        commandManager.register(new ExitCommand());
        commandManager.register(new AddIfMaxCommand());
        commandManager.register(new AddIfMinCommand());
        commandManager.register(new RemoveLowerCommand());
        commandManager.register(new FilterStartsWithNameCommand());
        commandManager.register(new FilterLessThanOwnerCommand());
        commandManager.register(new FilterGreaterThanOwnerCommand());
    }
}
