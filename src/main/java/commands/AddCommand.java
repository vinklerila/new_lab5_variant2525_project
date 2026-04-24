package commands;

import io.ProductInputManager;
import models.Product;
import util.ExecutionContext;

/**
 * Команда добавления нового элемента в коллекцию.
 */
public final class AddCommand implements Command {
    @Override
    public String getName() {
        return "add";
    }

    @Override
    public String getDescription() {
        return "добавить новый элемент в коллекцию";
    }

    @Override
    public boolean execute(String arguments, ExecutionContext context) {
        if (!arguments.isBlank()) {
            context.getOut().error("Составной объект Product вводится построчно после команды add.");
            return false;
        }

        ProductInputManager input = new ProductInputManager(context.getIn(), context.getOut());
        Product product = context.getCollectionManager().buildNewProduct(input.readProductFields());
        context.getCollectionManager().add(product);
        context.getOut().println("Элемент успешно добавлен.");
        return false;
    }
}
