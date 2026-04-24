package util;

/**
 * Исключение, сигнализирующее о том, что ввод был прерван.
 */
public final class InputCancelledException extends RuntimeException {
    public InputCancelledException(String message) {
        super(message);
    }
}
