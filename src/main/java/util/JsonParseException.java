package util;

/**
 * Исключение ошибки разбора JSON.
 */
public final class JsonParseException extends RuntimeException {
    public JsonParseException(String message) {
        super(message);
    }
}
