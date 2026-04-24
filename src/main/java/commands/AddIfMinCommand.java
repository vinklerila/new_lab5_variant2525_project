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
        Product candidate = context.getCollectionManager().buildNewProduct(input.readProductFields());

        if (context.getCollectionManager().addIfMin(candidate)) {
            context.getOut().println("Элемент добавлен.");
        } else {
            context.getOut().println("Элемент не добавлен: он не меньше минимального.");
        }
        return false;
    }
}
