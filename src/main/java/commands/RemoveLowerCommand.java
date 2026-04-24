package commands;

import io.ProductInputManager;
import models.Product;
import util.ExecutionContext;

/**
 * Команда удаления всех элементов, меньших заданного.
 */
public final class RemoveLowerCommand implements Command {
    @Override
    public String getName() {
        return "remove_lower";
    }

    @Override
    public String getDescription() {
        return "удалить из коллекции все элементы, меньшие заданного";
    }

    @Override
    public boolean execute(String arguments, ExecutionContext context) {
        if (!arguments.isBlank()) {
            context.getOut().error("Составной объект Product вводится построчно после команды remove_lower.");
            return false;
        }

        ProductInputManager input = new ProductInputManager(context.getIn(), context.getOut());
        Product reference = context.getCollectionManager().buildNewProduct(input.readProductFields());

        int removedCount = context.getCollectionManager().removeLower(reference);
        context.getOut().println("Удалено элементов: " + removedCount);
        return false;
    }
}
