package commands;

import io.ProductInputManager;
import models.Product;
import util.ExecutionContext;

/**
 * Команда обновления элемента коллекции по идентификатору.
 */
public final class UpdateCommand implements Command {
    @Override
    public String getName() {
        return "update";
    }

    @Override
    public String getDescription() {
        return "обновить значение элемента коллекции по id";
    }

    @Override
    public boolean execute(String arguments, ExecutionContext context) {
        String trimmed = arguments.trim();
        if (trimmed.isEmpty()) {
            context.getOut().error("Необходимо указать id элемента: update id");
            return false;
        }

        String[] parts = trimmed.split("\\s+", 2);
        int id;
        try {
            id = Integer.parseInt(parts[0]);
        } catch (NumberFormatException exception) {
            context.getOut().error("id должен быть целым числом.");
            return false;
        }

        Product existing = context.getCollectionManager().findById(id);
        if (existing == null) {
            context.getOut().error("Элемент с id = " + id + " не найден.");
            return false;
        }

        if (parts.length > 1 && !parts[1].isBlank()) {
            context.getOut().error("Составной объект Product вводится построчно после id.");
            return false;
        }

        ProductInputManager input = new ProductInputManager(context.getIn(), context.getOut());
        Product updated = context.getCollectionManager().buildUpdatedProduct(existing, input.readProductFields());
        context.getCollectionManager().update(id, updated);
        context.getOut().println("Элемент успешно обновлён.");
        return false;
    }
}
