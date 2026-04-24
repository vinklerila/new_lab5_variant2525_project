package managers;

import io.OutputManager;
import models.Coordinates;
import models.EyeColor;
import models.HairColor;
import models.Location;
import models.Person;
import models.Product;
import models.UnitOfMeasure;
import util.JsonParseException;
import util.SimpleJsonParser;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * Менеджер загрузки и сохранения коллекции в JSON-файл.
 */
public final class FileManager {
    private final Path filePath;
    private final OutputManager out;

    public FileManager(Path filePath, OutputManager out) {
        this.filePath = filePath;
        this.out = out;
    }

    public HashSet<Product> load() {
        HashSet<Product> products = new HashSet<>();

        if (!Files.exists(filePath)) {
            out.error("Файл не найден. Будет создана пустая коллекция: " + filePath);
            return products;
        }
        if (!Files.isReadable(filePath)) {
            out.error("Нет прав на чтение файла: " + filePath);
            return products;
        }

        String content;
        try (Scanner scanner = new Scanner(filePath, StandardCharsets.UTF_8)) {
            StringBuilder builder = new StringBuilder();
            while (scanner.hasNextLine()) {
                builder.append(scanner.nextLine());
                if (scanner.hasNextLine()) {
                    builder.append(System.lineSeparator());
                }
            }
            content = builder.toString().trim();
        } catch (IOException exception) {
            out.error("Ошибка чтения файла: " + exception.getMessage());
            return products;
        } catch (SecurityException exception) {
            out.error("Доступ к файлу запрещён: " + exception.getMessage());
            return products;
        }

        if (content.isEmpty()) {
            return products;
        }

        try {
            Object parsed = new SimpleJsonParser(content).parse();
            if (!(parsed instanceof List<?> list)) {
                out.error("Корневой элемент JSON должен быть массивом.");
                return products;
            }

            Set<Integer> ids = new LinkedHashSet<>();
            int index = 0;
            for (Object element : list) {
                index++;
                try {
                    if (!(element instanceof Map<?, ?> rawMap)) {
                        throw new IllegalArgumentException("элемент не является JSON-объектом");
                    }
                    Product product = parseProduct(castMap(rawMap));
                    if (!ids.add(product.getId())) {
                        throw new IllegalArgumentException("обнаружен повторяющийся id = " + product.getId());
                    }
                    products.add(product);
                } catch (IllegalArgumentException exception) {
                    out.error("Элемент #" + index + " пропущен: " + exception.getMessage());
                }
            }
        } catch (JsonParseException exception) {
            out.error("Ошибка разбора JSON: " + exception.getMessage());
        }

        return products;
    }

    public void save(Set<Product> products) {
        try {
            Path parent = filePath.toAbsolutePath().normalize().getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }

            try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
                writer.write(serializeProducts(products));
            }

            out.println("Коллекция сохранена в файл: " + filePath);
        } catch (IOException exception) {
            out.error("Ошибка записи файла: " + exception.getMessage());
        } catch (SecurityException exception) {
            out.error("Нет прав на запись файла: " + exception.getMessage());
        }
    }

    private Product parseProduct(Map<String, Object> map) {
        int id = readInt(map, "id", false);
        if (id <= 0) {
            throw new IllegalArgumentException("id должен быть больше 0");
        }

        String name = readString(map, "name", false);
        Coordinates coordinates = parseCoordinates(readObject(map, "coordinates", false));
        long creationDateMillis = readLong(map, "creationDate", false);
        float price = (float) readDouble(map, "price", false);
        if (price <= 0) {
            throw new IllegalArgumentException("price должен быть больше 0");
        }

        UnitOfMeasure unitOfMeasure = parseEnum(UnitOfMeasure.class, map, "unitOfMeasure", false);
        Person owner = map.get("owner") == null ? null : parsePerson(readObject(map, "owner", false));

        return new Product(id, name, coordinates, new Date(creationDateMillis), price, unitOfMeasure, owner);
    }

    private Coordinates parseCoordinates(Map<String, Object> map) {
        return new Coordinates(readLong(map, "x", false), readDouble(map, "y", false));
    }

    private Person parsePerson(Map<String, Object> map) {
        Integer height = map.get("height") == null ? null : readInt(map, "height", true);
        if (height != null && height <= 0) {
            throw new IllegalArgumentException("height должен быть больше 0");
        }

        return new Person(
                readString(map, "name", false),
                height,
                parseEnum(EyeColor.class, map, "eyeColor", false),
                parseEnum(HairColor.class, map, "hairColor", false),
                parseLocation(readObject(map, "location", false))
        );
    }

    private Location parseLocation(Map<String, Object> map) {
        return new Location(
                readDoubleObject(map, "x", false),
                readLong(map, "y", false),
                readDouble(map, "z", false),
                readString(map, "name", false)
        );
    }

    private String serializeProducts(Set<Product> products) {
        StringBuilder builder = new StringBuilder();
        builder.append("[\n");

        List<Product> sorted = products.stream().sorted().toList();
        for (int i = 0; i < sorted.size(); i++) {
            Product product = sorted.get(i);
            builder.append("  {\n");
            builder.append("    \"id\": ").append(product.getId()).append(",\n");
            builder.append("    \"name\": ").append(quote(product.getName())).append(",\n");
            builder.append("    \"coordinates\": {\n");
            builder.append("      \"x\": ").append(product.getCoordinates().getX()).append(",\n");
            builder.append("      \"y\": ").append(product.getCoordinates().getY()).append("\n");
            builder.append("    },\n");
            builder.append("    \"creationDate\": ").append(product.getCreationDate().getTime()).append(",\n");
            builder.append("    \"price\": ").append(product.getPrice()).append(",\n");
            builder.append("    \"unitOfMeasure\": ").append(quote(product.getUnitOfMeasure().name())).append(",\n");
            builder.append("    \"owner\": ");

            if (product.getOwner() == null) {
                builder.append("null\n");
            } else {
                builder.append("{\n");
                builder.append("      \"name\": ").append(quote(product.getOwner().getName())).append(",\n");
                builder.append("      \"height\": ")
                        .append(product.getOwner().getHeight() == null ? "null" : product.getOwner().getHeight())
                        .append(",\n");
                builder.append("      \"eyeColor\": ").append(quote(product.getOwner().getEyeColor().name())).append(",\n");
                builder.append("      \"hairColor\": ").append(quote(product.getOwner().getHairColor().name())).append(",\n");
                builder.append("      \"location\": {\n");
                builder.append("        \"x\": ").append(product.getOwner().getLocation().getX()).append(",\n");
                builder.append("        \"y\": ").append(product.getOwner().getLocation().getY()).append(",\n");
                builder.append("        \"z\": ").append(product.getOwner().getLocation().getZ()).append(",\n");
                builder.append("        \"name\": ").append(quote(product.getOwner().getLocation().getName())).append("\n");
                builder.append("      }\n");
                builder.append("    }\n");
            }

            builder.append("  }");
            if (i + 1 < sorted.size()) {
                builder.append(",");
            }
            builder.append("\n");
        }

        builder.append("]");
        return builder.toString();
    }

    private String quote(String value) {
        return "\"" + value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t") + "\"";
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> castMap(Map<?, ?> map) {
        return (Map<String, Object>) map;
    }

    private Map<String, Object> readObject(Map<String, Object> map, String key, boolean nullable) {
        Object value = map.get(key);
        if (value == null) {
            if (nullable) {
                return null;
            }
            throw new IllegalArgumentException("поле '" + key + "' отсутствует или равно null");
        }
        if (!(value instanceof Map<?, ?> rawMap)) {
            throw new IllegalArgumentException("поле '" + key + "' должно быть объектом");
        }
        return castMap(rawMap);
    }

    private String readString(Map<String, Object> map, String key, boolean nullable) {
        Object value = map.get(key);
        if (value == null) {
            if (nullable) {
                return null;
            }
            throw new IllegalArgumentException("поле '" + key + "' отсутствует или равно null");
        }
        if (!(value instanceof String stringValue)) {
            throw new IllegalArgumentException("поле '" + key + "' должно быть строкой");
        }
        if (stringValue.isEmpty()) {
            throw new IllegalArgumentException("поле '" + key + "' не может быть пустым");
        }
        return stringValue;
    }

    private int readInt(Map<String, Object> map, String key, boolean nullable) {
        Number number = readNumber(map, key, nullable);
        return number == null ? 0 : number.intValue();
    }

    private long readLong(Map<String, Object> map, String key, boolean nullable) {
        Number number = readNumber(map, key, nullable);
        return number == null ? 0L : number.longValue();
    }

    private double readDouble(Map<String, Object> map, String key, boolean nullable) {
        Number number = readNumber(map, key, nullable);
        return number == null ? 0.0 : number.doubleValue();
    }

    private Double readDoubleObject(Map<String, Object> map, String key, boolean nullable) {
        Number number = readNumber(map, key, nullable);
        return number == null ? null : number.doubleValue();
    }

    private Number readNumber(Map<String, Object> map, String key, boolean nullable) {
        Object value = map.get(key);
        if (value == null) {
            if (nullable) {
                return null;
            }
            throw new IllegalArgumentException("поле '" + key + "' отсутствует или равно null");
        }
        if (!(value instanceof Number number)) {
            throw new IllegalArgumentException("поле '" + key + "' должно быть числом");
        }
        return number;
    }

    private <E extends Enum<E>> E parseEnum(Class<E> enumClass, Map<String, Object> map, String key, boolean nullable) {
        String rawValue = readString(map, key, nullable);
        if (rawValue == null) {
            return null;
        }
        try {
            return Enum.valueOf(enumClass, rawValue);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("поле '" + key + "' содержит неизвестную константу enum");
        }
    }
}
