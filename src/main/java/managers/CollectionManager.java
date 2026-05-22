package managers;

import io.OutputManager;
import io.ProductInputManager;
import models.Person;
import models.Product;
import util.IdGenerator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Менеджер коллекции {@link java.util.HashSet} с объектами {@link Product}.
 */
public final class CollectionManager {
    private final HashSet<Product> products = new HashSet<>();
    private final Date initializationDate = new Date();
    private final FileManager fileManager;
    private final OutputManager out;
    private final IdGenerator idGenerator = new IdGenerator();

    public CollectionManager(FileManager fileManager, OutputManager out) {
        this.fileManager = fileManager;
        this.out = out;
    }

    public void load() {
        HashSet<Product> loadedProducts = fileManager.load();
        products.clear();
        products.addAll(loadedProducts);
        idGenerator.sync(products);
    }

    public void save() {
        fileManager.save(products);
    }

    public Product findById(int id) {
        return products.stream()
                .filter(product -> product.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public List<Product> getSortedProducts() {
        List<Product> result = new ArrayList<>(products);
        result.sort(Comparator.naturalOrder());
        return result;
    }

    public void add(Product product) {
        boolean added = products.add(product);

        if (!added) {
            idGenerator.releaseId(product.getId());
        }
    }

    public void update(int id, Product updated) {
        products.removeIf(product -> product.getId() == id);
        products.add(updated);
    }

    public boolean removeById(int id) {
        boolean removed = products.removeIf(product -> product.getId() == id);

        if (removed) {
            idGenerator.releaseId(id);
        }

        return removed;
    }

    public void clear() {
        products.clear();
        idGenerator.clear();
    }

    public Product buildNewProduct(ProductInputManager.ProductFields fields) {
        return new Product(
                idGenerator.nextId(),
                fields.name(),
                fields.coordinates(),
                new Date(),
                fields.price(),
                fields.unitOfMeasure(),
                fields.owner()
        );
    }

    /**
     * Создаёт объект только для сравнения, не занимая новый id.
     *
     * <p>Команды {@code add_if_max}, {@code add_if_min} и {@code remove_lower}
     * используют введённый пользователем элемент как значение для сравнения.
     * Поэтому настоящий id не должен расходоваться, пока объект реально не добавлен.</p>
     *
     * @param fields введённые поля Product
     * @return временный Product с минимальным свободным id
     */
    public Product buildCandidateProduct(ProductInputManager.ProductFields fields) {
        return new Product(
                idGenerator.peekNextId(),
                fields.name(),
                fields.coordinates(),
                new Date(),
                fields.price(),
                fields.unitOfMeasure(),
                fields.owner()
        );
    }

    public Product buildUpdatedProduct(Product existing, ProductInputManager.ProductFields fields) {
        return new Product(
                existing.getId(),
                fields.name(),
                fields.coordinates(),
                existing.getCreationDate(),
                fields.price(),
                fields.unitOfMeasure(),
                fields.owner()
        );
    }

    public boolean addIfMax(Product candidate) {
        if (canAddIfMax(candidate)) {
            products.add(candidate);
            return true;
        }

        idGenerator.releaseId(candidate.getId());
        return false;
    }

    public boolean addIfMin(Product candidate) {
        if (canAddIfMin(candidate)) {
            products.add(candidate);
            return true;
        }

        idGenerator.releaseId(candidate.getId());
        return false;
    }

    public boolean canAddIfMax(Product candidate) {
        Optional<Product> max = products.stream().max(Comparator.naturalOrder());
        return max.isEmpty() || candidate.compareTo(max.get()) > 0;
    }

    public boolean canAddIfMin(Product candidate) {
        Optional<Product> min = products.stream().min(Comparator.naturalOrder());
        return min.isEmpty() || candidate.compareTo(min.get()) < 0;
    }

    public int removeLower(Product reference) {
        List<Product> productsToRemove = products.stream()
                .filter(product -> product.compareTo(reference) < 0)
                .toList();

        for (Product product : productsToRemove) {
            products.remove(product);
            idGenerator.releaseId(product.getId());
        }

        return productsToRemove.size();
    }

    public List<Product> filterStartsWithName(String prefix) {
        return products.stream()
                .filter(product -> product.getName().startsWith(prefix))
                .sorted()
                .toList();
    }

    public List<Product> filterLessThanOwner(Person owner) {
        return products.stream()
                .filter(product -> product.getOwner() != null)
                .filter(product -> product.getOwner().compareTo(owner) < 0)
                .sorted()
                .toList();
    }

    public List<Product> filterGreaterThanOwner(Person owner) {
        return products.stream()
                .filter(product -> product.getOwner() != null)
                .filter(product -> product.getOwner().compareTo(owner) > 0)
                .sorted()
                .toList();
    }

    public String buildInfo() {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.ROOT);

        String nextIdInfo;

        try {
            nextIdInfo = String.valueOf(idGenerator.peekNextId());
        } catch (IllegalStateException exception) {
            nextIdInfo = exception.getMessage();
        }

        return "Тип коллекции: " + products.getClass().getName() + System.lineSeparator()
                + "Дата инициализации: " + format.format(initializationDate) + System.lineSeparator()
                + "Количество элементов: " + products.size() + System.lineSeparator()
                + "Следующий id: " + nextIdInfo;
    }
}