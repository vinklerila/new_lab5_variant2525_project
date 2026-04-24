package models;

import java.util.Comparator;
import java.util.Objects;

/**
 * Владелец продукта.
 */
public final class Person implements Comparable<Person> {
    private final String name;
    private final Integer height;
    private final EyeColor eyeColor;
    private final HairColor hairColor;
    private final Location location;

    public Person(String name, Integer height, EyeColor eyeColor, HairColor hairColor, Location location) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Person.name не может быть null или пустым.");
        }
        if (height != null && height <= 0) {
            throw new IllegalArgumentException("Person.height должен быть больше 0.");
        }
        if (eyeColor == null) {
            throw new IllegalArgumentException("Person.eyeColor не может быть null.");
        }
        if (hairColor == null) {
            throw new IllegalArgumentException("Person.hairColor не может быть null.");
        }
        if (location == null) {
            throw new IllegalArgumentException("Person.location не может быть null.");
        }

        this.name = name;
        this.height = height;
        this.eyeColor = eyeColor;
        this.hairColor = hairColor;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public Integer getHeight() {
        return height;
    }

    public EyeColor getEyeColor() {
        return eyeColor;
    }

    public HairColor getHairColor() {
        return hairColor;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public int compareTo(Person other) {
        Comparator<Integer> heightComparator = Comparator.nullsLast(Integer::compareTo);

        int byName = name.compareTo(other.name);
        if (byName != 0) {
            return byName;
        }
        int byHeight = heightComparator.compare(height, other.height);
        if (byHeight != 0) {
            return byHeight;
        }
        int byEyeColor = eyeColor.compareTo(other.eyeColor);
        if (byEyeColor != 0) {
            return byEyeColor;
        }
        int byHairColor = hairColor.compareTo(other.hairColor);
        return byHairColor != 0 ? byHairColor : location.compareTo(other.location);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Person person)) {
            return false;
        }
        return Objects.equals(name, person.name)
                && Objects.equals(height, person.height)
                && eyeColor == person.eyeColor
                && hairColor == person.hairColor
                && Objects.equals(location, person.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, height, eyeColor, hairColor, location);
    }

    @Override
    public String toString() {
        return "Person{name='" + name + "', height=" + height + ", eyeColor=" + eyeColor
                + ", hairColor=" + hairColor + ", location=" + location + "}";
    }
}
