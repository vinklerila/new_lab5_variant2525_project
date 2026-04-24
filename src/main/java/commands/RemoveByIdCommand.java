package commands;

import util.ExecutionContext;

/**
 * Команда удаления элемента по идентификатору.
 */
public final class RemoveByIdCommand implements Command {
    @Override
    public String getName() {
        return "remove_by_id";
    }

    @Override
    public String getDescription() {
        return "удалить элемент из коллекции по его id";
    }

    @Override
    public boolean execute(String arguments, ExecutionContext context) {
        String trimmed = arguments.trim();
        if (trimmed.isEmpty()) {
            context.getOut().error("Необходимо указать id элемента.");
            return false;
        }

        try {
            int id = Integer.parseInt(trimmed);
            boolean removed = context.getCollectionManager().removeById(id);
            if (removed) {
                context.getOut().println("Элемент удалён.");
            } else {
                context.getOut().error("Элемент с id = " + id + " не найден.");
            }
        } catch (NumberFormatException exception) {
            context.getOut().error("id должен быть целым числом.");
        }
        return false;
    }
}
