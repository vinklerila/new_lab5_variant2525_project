package commands;

import models.Product;
import util.ExecutionContext;

import java.util.List;

/**
 * Команда вывода всех элементов коллекции.
 */
public final class ShowCommand implements Command {
    @Override
    public String getName() {
        return "show";
    }

    @Override
    public String getDescription() {
        return "вывести все элементы коллекции";
    }

    @Override
    public boolean execute(String arguments, ExecutionContext context) {
        if (!arguments.isBlank()) {
            context.getOut().error("Команда show не принимает аргументы.");
            return false;
        }

        List<Product> products = context.getCollectionManager().getSortedProducts();
        if (products.isEmpty()) {
            context.getOut().println("Коллекция пуста.");
            return false;
        }

        for (Product product : products) {
            context.getOut().println(product.toDisplayString());
            context.getOut().println("");
        }
        return false;
    }
}
