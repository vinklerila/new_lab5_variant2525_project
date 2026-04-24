package commands;

import io.ProductInputManager;
import models.Product;
import util.ExecutionContext;

/**
 * Команда добавления элемента, если он больше максимального элемента коллекции.
 */
public final class AddIfMaxCommand implements Command {
    @Override
    public String getName() {
        return "add_if_max";
    }

    @Override
    public String getDescription() {
        return "добавить новый элемент, если он больше наибольшего элемента коллекции";
    }

    @Override
    public boolean execute(String arguments, ExecutionContext context) {
        if (!arguments.isBlank()) {
            context.getOut().error("Составной объект Product вводится построчно после команды add_if_max.");
            return false;
        }

        ProductInputManager input = new ProductInputManager(context.getIn(), context.getOut());
        Product candidate = context.getCollectionManager().buildNewProduct(input.readProductFields());

        if (context.getCollectionManager().addIfMax(candidate)) {
            context.getOut().println("Элемент добавлен.");
        } else {
            context.getOut().println("Элемент не добавлен: он не больше максимального.");
        }
        return false;
    }
}
