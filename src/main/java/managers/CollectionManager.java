package managers;

import io.OutputManager;
import io.ProductInputManager;
import models.Person;
import models.Product;

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
    private int nextId = 1;

    public CollectionManager(FileManager fileManager, OutputManager out) {
        this.fileManager = fileManager;
        this.out = out;
    }

    public void load() {
        HashSet<Product> loadedProducts = fileManager.load();
        products.clear();
        products.addAll(loadedProducts);

        int maxId = products.stream()
                .map(Product::getId)
                .max(Integer::compareTo)
                .orElse(0);

        if (maxId == Integer.MAX_VALUE) {
            nextId = -1;
        } else {
            nextId = maxId + 1;
        }
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
        products.add(product);
    }

    public void update(int id, Product updated) {
        products.removeIf(product -> product.getId() == id);
        products.add(updated);
    }

    public boolean removeById(int id) {
        return products.removeIf(product -> product.getId() == id);
    }

    public void clear() {
        products.clear();
        nextId = 1;
    }

    public Product buildNewProduct(ProductInputManager.ProductFields fields) {
        return new Product(
                generateNextId(),
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
        Optional<Product> max = products.stream().max(Comparator.naturalOrder());
        if (max.isEmpty() || candidate.compareTo(max.get()) > 0) {
            products.add(candidate);
            return true;
        }
        return false;
    }

    public boolean addIfMin(Product candidate) {
        Optional<Product> min = products.stream().min(Comparator.naturalOrder());
        if (min.isEmpty() || candidate.compareTo(min.get()) < 0) {
            products.add(candidate);
            return true;
        }
        return false;
    }

    public int removeLower(Product reference) {
        int before = products.size();
        products.removeIf(product -> product.compareTo(reference) < 0);
        return before - products.size();
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
        return "Тип коллекции: " + products.getClass().getName() + System.lineSeparator()
                + "Дата инициализации: " + format.format(initializationDate) + System.lineSeparator()
                + "Количество элементов: " + products.size() + System.lineSeparator()
                + "Следующий id: " + nextId;
    }

    private int generateNextId() {
        if (nextId <= 0) {
            throw new IllegalStateException(
                    "Невозможно сгенерировать новый id: диапазон положительных int id исчерпан."
            );
        }

        if (nextId == Integer.MAX_VALUE) {
            int lastId = nextId;
            nextId = -1;
            return lastId;
        }

        return nextId++;
    }
}