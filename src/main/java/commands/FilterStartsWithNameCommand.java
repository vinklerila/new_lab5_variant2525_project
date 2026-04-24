package commands;

import models.Product;
import util.ExecutionContext;

import java.util.List;

/**
 * Команда вывода элементов, имя которых начинается с заданной подстроки.
 */
public final class FilterStartsWithNameCommand implements Command {
    @Override
    public String getName() {
        return "filter_starts_with_name";
    }

    @Override
    public String getDescription() {
        return "вывести элементы, значение поля name которых начинается с заданной подстроки";
    }

    @Override
    public boolean execute(String arguments, ExecutionContext context) {
        String prefix = arguments.trim();
        if (prefix.isEmpty()) {
            context.getOut().error("Необходимо указать подстроку имени.");
            return false;
        }

        List<Product> products = context.getCollectionManager().filterStartsWithName(prefix);
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
