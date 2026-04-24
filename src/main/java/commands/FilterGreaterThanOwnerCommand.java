package commands;

import io.ProductInputManager;
import models.Person;
import models.Product;
import util.ExecutionContext;

import java.util.List;

/**
 * Команда вывода элементов, значение поля owner которых больше заданного.
 */
public final class FilterGreaterThanOwnerCommand implements Command {
    @Override
    public String getName() {
        return "filter_greater_than_owner";
    }

    @Override
    public String getDescription() {
        return "вывести элементы, значение поля owner которых больше заданного";
    }

    @Override
    public boolean execute(String arguments, ExecutionContext context) {
        if (!arguments.isBlank()) {
            context.getOut().error("Объект owner вводится построчно после команды filter_greater_than_owner.");
            return false;
        }

        ProductInputManager input = new ProductInputManager(context.getIn(), context.getOut());
        Person owner = input.readOwnerForFilter();
        List<Product> products = context.getCollectionManager().filterGreaterThanOwner(owner);

        if (products.isEmpty()) {
            context.getOut().println("Подходящих элементов не найдено.");
            return false;
        }

        for (Product product : products) {
            context.getOut().println(product.toDisplayString());
            context.getOut().println("");
        }
        return false;
    }
}
