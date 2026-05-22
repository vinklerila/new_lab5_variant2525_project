package commands;

import io.ProductInputManager;
import models.Product;
import util.ExecutionContext;

/**
 * Команда добавления элемента, если он меньше минимального элемента коллекции.
 */
public final class AddIfMinCommand implements Command {
    @Override
    public String getName() {
        return "add_if_min";
    }

    @Override
    public String getDescription() {
        return "добавить новый элемент, если он меньше наименьшего элемента коллекции";
    }

    @Override
    public boolean execute(String arguments, ExecutionContext context) {
        if (!arguments.isBlank()) {
            context.getOut().error("Составной объект Product вводится построчно после команды add_if_min.");
            return false;
        }

        ProductInputManager input = new ProductInputManager(context.getIn(), context.getOut());
        ProductInputManager.ProductFields fields = input.readProductFields();

        try {
            Product candidate = context.getCollectionManager().buildCandidateProduct(fields);

            if (context.getCollectionManager().canAddIfMin(candidate)) {
                Product product = context.getCollectionManager().buildNewProduct(fields);
                context.getCollectionManager().add(product);
                context.getOut().println("Элемент добавлен.");
            } else {
                context.getOut().println("Элемент не добавлен: он не меньше минимального.");
            }
        } catch (IllegalStateException exception) {
            context.getOut().error(exception.getMessage());
        }

        return false;
    }
}