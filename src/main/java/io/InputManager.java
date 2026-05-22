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

    /**
     * Создаёт менеджер ввода.
     *
     * @param inputStream поток стандартного ввода
     * @param out менеджер вывода
     */
    public InputManager(InputStream inputStream, OutputManager out) {
        this.out = out;
        sources.push(new InputSource(
                new Scanner(inputStream, StandardCharsets.UTF_8),
                "stdin",
                null
        ));
    }

    /**
     * Проверяет, выполняется ли сейчас скрипт.
     *
     * @return true, если ввод идёт из скрипта
     */
    public boolean isScriptMode() {
        return sources.peek() != null && sources.peek().scriptPath() != null;
    }

    /**
     * Считывает следующую строку из текущего источника ввода.
     *
     * @return строка ввода или null, если ввод завершён
     */
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

    /**
     * Подключает новый скрипт к стеку источников ввода.
     *
     * <p>Метод запрещает рекурсивное выполнение скриптов. Проверка выполняется
     * через {@link Path#toRealPath(java.nio.file.LinkOption...)}, поэтому
     * одинаковые файлы корректно распознаются даже при разных формах пути:
     * {@code script.txt}, {@code ./script.txt}, {@code folder/../script.txt}.</p>
     *
     * @param path путь к файлу скрипта
     * @return true, если скрипт успешно подключён
     */
    public boolean pushScript(Path path) {
        try {
            Path resolvedPath = resolveScriptPath(path);
            Path normalizedPath = resolvedPath.toAbsolutePath().normalize();

            if (!Files.exists(normalizedPath)) {
                out.error("Файл скрипта не найден: " + normalizedPath);
                return false;
            }

            if (!Files.isRegularFile(normalizedPath)) {
                out.error("Путь к скрипту не является обычным файлом: " + normalizedPath);
                return false;
            }

            if (!Files.isReadable(normalizedPath)) {
                out.error("Нет прав на чтение файла скрипта: " + normalizedPath);
                return false;
            }

            Path realPath = normalizedPath.toRealPath();

            if (activeScripts.contains(realPath)) {
                out.error("Ошибка рекурсии: скрипт уже выполняется: " + realPath);
                return false;
            }

            Scanner scanner = new Scanner(realPath, StandardCharsets.UTF_8);
            activeScripts.add(realPath);
            sources.push(new InputSource(scanner, realPath.getFileName().toString(), realPath));

            return true;
        } catch (IOException exception) {
            out.error("Не удалось открыть скрипт: " + exception.getMessage());
            return false;
        } catch (SecurityException exception) {
            out.error("Доступ к файлу скрипта запрещён: " + exception.getMessage());
            return false;
        }
    }

    private Path resolveScriptPath(Path path) {
        if (path.isAbsolute()) {
            return path;
        }

        InputSource currentSource = sources.peek();

        if (currentSource != null && currentSource.scriptPath() != null) {
            Path parent = currentSource.scriptPath().getParent();

            if (parent != null) {
                return parent.resolve(path);
            }
        }

        return path;
    }
}