package util;

import models.Product;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Генерирует уникальные положительные идентификаторы для объектов Product.
 *
 * <p>Всегда возвращает минимальный свободный положительный id.</p>
 */
public final class IdGenerator {
    private final List<Integer> usedIds = new ArrayList<>();

    /**
     * Синхронизирует генератор с уже существующей коллекцией.
     *
     * @param products коллекция продуктов
     */
    public void sync(Collection<Product> products) {
        usedIds.clear();

        for (Product product : products) {
            if (product == null) {
                throw new IllegalArgumentException("В коллекции найден null вместо Product");
            }

            int id = product.getId();

            if (id <= 0) {
                throw new IllegalArgumentException("В коллекции найден элемент с некорректным id: " + id);
            }

            if (usedIds.contains(id)) {
                throw new IllegalArgumentException("В коллекции найден дублирующийся id: " + id);
            }

            usedIds.add(id);
        }

        Collections.sort(usedIds);
    }

    /**
     * Возвращает минимальный свободный положительный id и помечает его занятым.
     *
     * @return новый id
     */
    public int nextId() {
        int id = peekNextId();
        usedIds.add(id);
        Collections.sort(usedIds);
        return id;
    }

    /**
     * Возвращает минимальный свободный положительный id, но не занимает его.
     *
     * <p>Этот метод нужен для временных объектов в командах
     * {@code add_if_max}, {@code add_if_min}, {@code remove_lower},
     * чтобы не расходовать id впустую.</p>
     *
     * @return минимальный свободный положительный id
     */
    public int peekNextId() {
        int expected = 1;

        for (int id : usedIds) {
            if (id == expected) {
                if (expected == Integer.MAX_VALUE) {
                    throw new IllegalStateException("Достигнут предел значений id для типа Integer");
                }

                expected++;
            } else if (id > expected) {
                break;
            }
        }

        return expected;
    }

    /**
     * Освобождает id после удаления элемента.
     *
     * @param id идентификатор, который больше не используется
     */
    public void releaseId(int id) {
        usedIds.remove(Integer.valueOf(id));
    }

    /**
     * Полностью очищает генератор.
     */
    public void clear() {
        usedIds.clear();
    }
}