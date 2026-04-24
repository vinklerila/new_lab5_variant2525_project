package util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Минималистичный рекурсивный парсер JSON без внешних библиотек.
 */
public final class SimpleJsonParser {
    private final String source;
    private int position;

    public SimpleJsonParser(String source) {
        this.source = source == null ? "" : source;
    }

    public Object parse() {
        skipWhitespace();
        Object value = parseValue();
        skipWhitespace();
        if (!isEnd()) {
            throw error("Лишние символы после конца JSON.");
        }
        return value;
    }

    private Object parseValue() {
        skipWhitespace();
        if (isEnd()) {
            throw error("Неожиданный конец JSON.");
        }

        char current = currentChar();
        return switch (current) {
            case '{' -> parseObject();
            case '[' -> parseArray();
            case '"' -> parseString();
            case 't' -> parseLiteral("true", Boolean.TRUE);
            case 'f' -> parseLiteral("false", Boolean.FALSE);
            case 'n' -> parseLiteral("null", null);
            default -> {
                if (current == '-' || Character.isDigit(current)) {
                    yield parseNumber();
                }
                throw error("Некорректное значение JSON.");
            }
        };
    }

    private Map<String, Object> parseObject() {
        expect('{');
        skipWhitespace();

        Map<String, Object> result = new LinkedHashMap<>();
        if (tryConsume('}')) {
            return result;
        }

        while (true) {
            skipWhitespace();
            if (currentChar() != '"') {
                throw error("Ключ объекта должен быть строкой.");
            }

            String key = parseString();
            skipWhitespace();
            expect(':');
            skipWhitespace();

            Object value = parseValue();
            result.put(key, value);

            skipWhitespace();
            if (tryConsume('}')) {
                return result;
            }
            expect(',');
        }
    }

    private List<Object> parseArray() {
        expect('[');
        skipWhitespace();

        List<Object> result = new ArrayList<>();
        if (tryConsume(']')) {
            return result;
        }

        while (true) {
            result.add(parseValue());
            skipWhitespace();
            if (tryConsume(']')) {
                return result;
            }
            expect(',');
            skipWhitespace();
        }
    }

    private String parseString() {
        expect('"');
        StringBuilder builder = new StringBuilder();

        while (!isEnd()) {
            char current = currentChar();
            position++;

            if (current == '"') {
                return builder.toString();
            }

            if (current == '\\') {
                if (isEnd()) {
                    throw error("Некорректная escape-последовательность.");
                }
                char escaped = currentChar();
                position++;
                switch (escaped) {
                    case '"', '\\', '/' -> builder.append(escaped);
                    case 'b' -> builder.append('\b');
                    case 'f' -> builder.append('\f');
                    case 'n' -> builder.append('\n');
                    case 'r' -> builder.append('\r');
                    case 't' -> builder.append('\t');
                    case 'u' -> builder.append(parseUnicodeEscape());
                    default -> throw error("Неизвестная escape-последовательность.");
                }
                continue;
            }

            builder.append(current);
        }

        throw error("Строка не закрыта.");
    }

    private char parseUnicodeEscape() {
        if (position + 4 > source.length()) {
            throw error("Неполная unicode-последовательность.");
        }
        String hex = source.substring(position, position + 4);
        position += 4;
        try {
            return (char) Integer.parseInt(hex, 16);
        } catch (NumberFormatException exception) {
            throw error("Некорректная unicode-последовательность.");
        }
    }

    private Object parseLiteral(String expected, Object value) {
        if (source.startsWith(expected, position)) {
            position += expected.length();
            return value;
        }
        throw error("Ожидалось значение '" + expected + "'.");
    }

    private Number parseNumber() {
        int start = position;
        if (currentChar() == '-') {
            position++;
        }
        readDigits();

        boolean fractional = false;
        if (!isEnd() && currentChar() == '.') {
            fractional = true;
            position++;
            readDigits();
        }

        if (!isEnd() && (currentChar() == 'e' || currentChar() == 'E')) {
            fractional = true;
            position++;
            if (!isEnd() && (currentChar() == '+' || currentChar() == '-')) {
                position++;
            }
            readDigits();
        }

        String rawNumber = source.substring(start, position);
        try {
            return fractional ? Double.parseDouble(rawNumber) : Long.parseLong(rawNumber);
        } catch (NumberFormatException exception) {
            throw error("Некорректное число: " + rawNumber);
        }
    }

    private void readDigits() {
        if (isEnd() || !Character.isDigit(currentChar())) {
            throw error("Ожидалась цифра.");
        }
        while (!isEnd() && Character.isDigit(currentChar())) {
            position++;
        }
    }

    private void expect(char expected) {
        skipWhitespace();
        if (isEnd() || currentChar() != expected) {
            throw error("Ожидался символ '" + expected + "'.");
        }
        position++;
    }

    private boolean tryConsume(char expected) {
        skipWhitespace();
        if (!isEnd() && currentChar() == expected) {
            position++;
            return true;
        }
        return false;
    }

    private void skipWhitespace() {
        while (!isEnd() && Character.isWhitespace(currentChar())) {
            position++;
        }
    }

    private boolean isEnd() {
        return position >= source.length();
    }

    private char currentChar() {
        return source.charAt(position);
    }

    private JsonParseException error(String message) {
        return new JsonParseException(message + " Позиция: " + position);
    }
}
