package models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * Элемент управляемой коллекции.
 */
public final class Product implements Comparable<Product> {
    private final int id;
    private final String name;
    private final Coordinates coordinates;
    private final Date creationDate;
    private final float price;
    private final UnitOfMeasure unitOfMeasure;
    private final Person owner;

    public Product(
            int id,
            String name,
            Coordinates coordinates,
            Date creationDate,
            float price,
            UnitOfMeasure unitOfMeasure,
            Person owner
    ) {
        if (id <= 0) {
            throw new IllegalArgumentException("Product.id должен быть больше 0.");
        }
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Product.name не может быть null или пустым.");
        }
        if (coordinates == null) {
            throw new IllegalArgumentException("Product.coordinates не может быть null.");
        }
        if (creationDate == null) {
            throw new IllegalArgumentException("Product.creationDate не может быть null.");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Product.price должен быть больше 0.");
        }
        if (unitOfMeasure == null) {
            throw new IllegalArgumentException("Product.unitOfMeasure не может быть null.");
        }

        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = new Date(creationDate.getTime());
        this.price = price;
        this.unitOfMeasure = unitOfMeasure;
        this.owner = owner;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public Date getCreationDate() {
        return new Date(creationDate.getTime());
    }

    public float getPrice() {
        return price;
    }

    public UnitOfMeasure getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public Person getOwner() {
        return owner;
    }

    public String toDisplayString() {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.ROOT);
        return "Product {\n"
                + "  id = " + id + ",\n"
                + "  name = '" + name + "',\n"
                + "  coordinates = " + coordinates + ",\n"
                + "  creationDate = " + format.format(creationDate) + ",\n"
                + "  price = " + price + ",\n"
                + "  unitOfMeasure = " + unitOfMeasure + ",\n"
                + "  owner = " + owner + "\n"
                + "}";
    }

    @Override
    public int compareTo(Product other) {
        int byPrice = Float.compare(price, other.price);
        if (byPrice != 0) {
            return byPrice;
        }
        int byName = name.compareTo(other.name);
        if (byName != 0) {
            return byName;
        }
        return Integer.compare(id, other.id);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Product product)) {
            return false;
        }
        return id == product.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return toDisplayString();
    }
}
