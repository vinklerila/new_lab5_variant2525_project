package io;

import models.Coordinates;
import models.EyeColor;
import models.HairColor;
import models.Location;
import models.Person;
import models.UnitOfMeasure;
import util.InputCancelledException;

import java.util.Arrays;

/**
 * Менеджер интерактивного ввода составных объектов.
 */
public final class ProductInputManager {
    private final InputManager in;
    private final OutputManager out;

    public record ProductFields(
            String name,
            Coordinates coordinates,
            float price,
            UnitOfMeasure unitOfMeasure,
            Person owner
    ) {
    }

    public ProductInputManager(InputManager in, OutputManager out) {
        this.in = in;
        this.out = out;
    }

    public ProductFields readProductFields() {
        String name = readRequiredString("Введите name: ");
        long coordinatesX = readLong("Введите coordinates.x: ");
        double coordinatesY = readDouble("Введите coordinates.y: ");
        float price = readFloatGreaterThanZero("Введите price: ");
        UnitOfMeasure unitOfMeasure = readEnum(UnitOfMeasure.class,
                "Введите unitOfMeasure " + Arrays.toString(UnitOfMeasure.values()) + ": ");

        String ownerName = readOptionalString("Введите owner.name (пустая строка = null owner): ");
        Person owner = ownerName == null ? null : readPersonAfterName(ownerName);

        return new ProductFields(name, new Coordinates(coordinatesX, coordinatesY), price, unitOfMeasure, owner);
    }

    public Person readOwnerForFilter() {
        String ownerName = readRequiredString("Введите owner.name: ");
        return readPersonAfterName(ownerName);
    }

    private Person readPersonAfterName(String ownerName) {
        Integer height = readNullableIntegerGreaterThanZero("Введите owner.height (пустая строка = null): ");
        EyeColor eyeColor = readEnum(EyeColor.class,
                "Введите owner.eyeColor " + Arrays.toString(EyeColor.values()) + ": ");
        HairColor hairColor = readEnum(HairColor.class,
                "Введите owner.hairColor " + Arrays.toString(HairColor.values()) + ": ");
        Double locationX = readRequiredDoubleObject("Введите owner.location.x: ");
        long locationY = readLong("Введите owner.location.y: ");
        double locationZ = readDouble("Введите owner.location.z: ");
        String locationName = readRequiredString("Введите owner.location.name: ");

        return new Person(
                ownerName,
                height,
                eyeColor,
                hairColor,
                new Location(locationX, locationY, locationZ, locationName)
        );
    }

    private String readRequiredString(String prompt) {
        while (true) {
            String line = readField(prompt);
            if (line.trim().isEmpty()) {
                out.error("Значение не может быть пустым.");
                continue;
            }
            return line;
        }
    }

    private String readOptionalString(String prompt) {
        String line = readField(prompt);
        return line.trim().isEmpty() ? null : line;
    }

    private long readLong(String prompt) {
        while (true) {
            String line = readField(prompt);
            try {
                return Long.parseLong(line.trim());
            } catch (NumberFormatException exception) {
                out.error("Ожидалось целое число типа long.");
            }
        }
    }

    private double readDouble(String prompt) {
        while (true) {
            String line = readField(prompt);
            try {
                return Double.parseDouble(line.trim());
            } catch (NumberFormatException exception) {
                out.error("Ожидалось число типа double.");
            }
        }
    }

    private Double readRequiredDoubleObject(String prompt) {
        while (true) {
            String line = readField(prompt);
            if (line.trim().isEmpty()) {
                out.error("Значение не может быть null.");
                continue;
            }
            try {
                return Double.valueOf(line.trim());
            } catch (NumberFormatException exception) {
                out.error("Ожидалось число типа Double.");
            }
        }
    }

    private float readFloatGreaterThanZero(String prompt) {
        while (true) {
            String line = readField(prompt);
            try {
                float value = Float.parseFloat(line.trim());
                if (value <= 0) {
                    out.error("Значение должно быть больше 0.");
                    continue;
                }
                return value;
            } catch (NumberFormatException exception) {
                out.error("Ожидалось число типа float.");
            }
        }
    }

    private Integer readNullableIntegerGreaterThanZero(String prompt) {
        while (true) {
            String line = readField(prompt).trim();
            if (line.isEmpty()) {
                return null;
            }
            try {
                int value = Integer.parseInt(line);
                if (value <= 0) {
                    out.error("Значение должно быть больше 0.");
                    continue;
                }
                return value;
            } catch (NumberFormatException exception) {
                out.error("Ожидалось число типа Integer.");
            }
        }
    }

    private <E extends Enum<E>> E readEnum(Class<E> enumClass, String prompt) {
        while (true) {
            String line = readField(prompt).trim();
            try {
                return Enum.valueOf(enumClass, line);
            } catch (IllegalArgumentException exception) {
                out.error("Некорректное значение enum. Допустимые значения: "
                        + Arrays.toString(enumClass.getEnumConstants()));
            }
        }
    }

    private String readField(String prompt) {
        out.print(prompt);
        String line = in.readLine();
        if (line == null) {
            throw new InputCancelledException("Ввод поля прерван.");
        }
        return line;
    }
}
