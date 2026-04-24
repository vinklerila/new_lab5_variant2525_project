package io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Менеджер чтения строк из стандартного ввода и из стеков скриптов.
 */
public final class InputManager {
    private final Deque<InputSource> sources = new ArrayDeque<>();
    private final Set<Path> activeScripts = new HashSet<>();
    private final OutputManager out;

    private record InputSource(Scanner scanner, String name, Path scriptPath) {
    }

    public InputManager(InputStream inputStream, OutputManager out) {
        this.out = out;
        sources.push(new InputSource(new Scanner(inputStream, StandardCharsets.UTF_8), "stdin", null));
    }

    public boolean isScriptMode() {
        return sources.peek() != null && sources.peek().scriptPath() != null;
    }

    public String readLine() {
        while (!sources.isEmpty()) {
            InputSource source = sources.peek();
            if (source.scanner().hasNextLine()) {
                String line = source.scanner().nextLine();
                if (source.scriptPath() != null) {
                    out.println("[script " + source.name() + "] " + line);
                }
                return line;
            }

            sources.pop();
            if (source.scriptPath() != null) {
                activeScripts.remove(source.scriptPath());
                source.scanner().close();
                out.println("Скрипт завершён: " + source.name());
                continue;
            }

            return null;
        }
        return null;
    }

    public boolean pushScript(Path path) {
        try {
            Path normalized = path.toAbsolutePath().normalize();
            if (activeScripts.contains(normalized)) {
                out.error("Рекурсивное выполнение скриптов запрещено: " + normalized);
                return false;
            }
            if (!Files.exists(normalized)) {
                out.error("Файл скрипта не найден: " + normalized);
                return false;
            }
            if (!Files.isReadable(normalized)) {
                out.error("Нет прав на чтение файла скрипта: " + normalized);
                return false;
            }

            Scanner scanner = new Scanner(normalized, StandardCharsets.UTF_8);
            sources.push(new InputSource(scanner, normalized.getFileName().toString(), normalized));
            activeScripts.add(normalized);
            return true;
        } catch (IOException exception) {
            out.error("Не удалось открыть скрипт: " + exception.getMessage());
            return false;
        } catch (SecurityException exception) {
            out.error("Доступ к файлу скрипта запрещён: " + exception.getMessage());
            return false;
        }
    }
}
