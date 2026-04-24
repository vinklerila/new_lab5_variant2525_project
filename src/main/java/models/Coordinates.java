package models;

import java.util.Objects;

/**
 * Координаты продукта.
 */
public final class Coordinates implements Comparable<Coordinates> {
    private final long x;
    private final double y;

    public Coordinates(long x, double y) {
        this.x = x;
        this.y = y;
    }

    public long getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public int compareTo(Coordinates other) {
        int byX = Long.compare(x, other.x);
        return byX != 0 ? byX : Double.compare(y, other.y);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Coordinates that)) {
            return false;
        }
        return x == that.x && Double.compare(y, that.y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Coordinates{x=" + x + ", y=" + y + "}";
    }
}
