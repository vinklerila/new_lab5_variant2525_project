package commands;

import util.ExecutionContext;

import java.nio.file.Path;

/**
 * Команда исполнения скрипта из файла.
 */
public final class ExecuteScriptCommand implements Command {
    @Override
    public String getName() {
        return "execute_script";
    }

    @Override
    public String getDescription() {
        return "считать и исполнить скрипт из указанного файла";
    }

    @Override
    public boolean execute(String arguments, ExecutionContext context) {
        String fileName = arguments.trim();
        if (fileName.isEmpty()) {
            context.getOut().error("Необходимо указать имя файла скрипта.");
            return false;
        }

        boolean pushed = context.getIn().pushScript(Path.of(fileName));
        if (pushed) {
            context.getOut().println("Скрипт подключён: " + fileName);
        }
        return false;
    }
}
