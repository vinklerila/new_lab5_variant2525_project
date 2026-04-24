package io;

import java.io.PrintStream;

/**
 * Менеджер вывода пользовательских сообщений и сообщений об ошибках.
 */
public final class OutputManager {
    private final PrintStream out;
    private final PrintStream err;

    public OutputManager(PrintStream out, PrintStream err) {
        this.out = out;
        this.err = err;
    }

    public void println(String message) {
        out.println(message);
    }

    public void print(String message) {
        out.print(message);
    }

    public void error(String message) {
        err.println(message);
    }
}
