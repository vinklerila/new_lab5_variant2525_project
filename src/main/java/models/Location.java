package models;

import java.util.Objects;

/**
 * Локация владельца.
 */
public final class Location implements Comparable<Location> {
    private final Double x;
    private final long y;
    private final double z;
    private final String name;

    public Location(Double x, long y, double z, String name) {
        if (x == null) {
            throw new IllegalArgumentException("Location.x не может быть null.");
        }
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Location.name не может быть null или пустым.");
        }
        this.x = x;
        this.y = y;
        this.z = z;
        this.name = name;
    }

    public Double getX() {
        return x;
    }

    public long getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(Location other) {
        int byName = name.compareTo(other.name);
        if (byName != 0) {
            return byName;
        }
        int byX = Double.compare(x, other.x);
        if (byX != 0) {
            return byX;
        }
        int byY = Long.compare(y, other.y);
        return byY != 0 ? byY : Double.compare(z, other.z);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Location location)) {
            return false;
        }
        return y == location.y
                && Double.compare(z, location.z) == 0
                && Objects.equals(x, location.x)
                && Objects.equals(name, location.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, name);
    }

    @Override
    public String toString() {
        return "Location{x=" + x + ", y=" + y + ", z=" + z + ", name='" + name + "'}";
    }
}
