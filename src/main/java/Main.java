import io.InputManager;
import io.OutputManager;

/**
 * Точка входа в приложение.
 *
 * <p>Программа ожидает единственный аргумент командной строки — путь к JSON-файлу с коллекцией.
 * После проверки аргументов создаются менеджеры ввода/вывода и запускается консольная оболочка.</p>
 */
public final class Main {

    /**
     * Запрещает создание экземпляра утилитного класса.
     */
    private Main() {
    }

    /**
     * Запускает приложение.
     *
     * @param args аргументы командной строки; ожидается один аргумент — путь к JSON-файлу
     */
    public static void main(String[] args) {
        OutputManager out = new OutputManager(System.out, System.err);

        if (args == null || args.length != 1) {
            out.error("Ошибка: необходимо передать имя JSON-файла как аргумент командной строки.");
            out.error("Пример запуска: java -jar target/lab5-variant-2525-1.0.0.jar data.json");
            return;
        }

        InputManager in = new InputManager(System.in, out);
        new Console(args[0], in, out).run();
    }
}
