package org.itmo.fuzzing.practice1;

import org.itmo.fuzzing.lect6.grammar.BetterGrammar;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Грамматика формата AdvancedData для практического задания по лекции 4.
 *
 * <p>Формат описан на слайдах 43–44 лекции 4. Пример документа:</p>
 * <pre>
 * # Информация о человеке
 * person {
 *   name: "John Doe"
 *   age: 30
 *   hobbies: ["reading", "gaming"]
 * }
 * </pre>
 *
 * <p>Правила формата:</p>
 * <ul>
 *     <li>каждая строка — пара {@code ключ: значение};</li>
 *     <li>ключ начинается с буквы и может содержать буквы, цифры и {@code _};</li>
 *     <li>значения — строки в кавычках, целые числа, {@code true}/{@code false} и массивы строк;</li>
 *     <li>блок — {@code ключ {}, затем вложенные элементы и закрывающая {@code }} на отдельной строке;</li>
 *     <li>массив — {@code [ ... ]}, элементы разделяются запятыми;</li>
 *     <li>комментарий начинается с {@code #}.</li>
 * </ul>
 *
 * <p>Лексика (буквы, цифры, ключи, строки, числа, boolean, текст комментария) уже описана.
 * Вам осталось дописать правила структуры документа, помеченные {@code TODO}. Учтите, что
 * {@link org.itmo.fuzzing.task1.DataParser} читает документ <b>построчно</b>: где именно стоят
 * переводы строк {@code "\n"}, для него важно. Ориентир: подавляющее большинство
 * сгенерированных документов должно давать ответ «I am okay».</p>
 *
 * <p>Формат записи — как у {@code ExprGrammar.EXPR_GRAMMAR} и {@code URLGrammar.URL_GRAMMAR}:
 * ключ — нетерминал в угловых скобках, значение — список альтернатив.</p>
 */
public final class AdvancedDataGrammar {

    public static final String START_SYMBOL = "<start>";

    // Алфавит намеренно урезан до a..h, чтобы задание проходилось за разумное время.
    private static final List<String> LETTERS = List.of("a", "b", "c", "d", "e", "f", "g", "h");

    public static final LinkedHashMap<String, List<String>> ADVANCED_DATA_GRAMMAR = new LinkedHashMap<>() {{
        put("<start>", List.of("<document>"));

        // ---------- Структура документа: допишите ----------

        // Документ — один или несколько элементов, каждый на своей строке.
        put("<document>", List.of("<element>", "<element><document>"));

        put("<element>", List.of("<key_value>", "<block>", "<comment>"));

        put("<key_value>", List.of("<key>: <value>\n"));

        put("<value>", List.of("<string>", "<number>", "<boolean>", "<array>"));

        put("<block>", List.of("<key> {\n<elements>\n}\n"));
        put("<elements>", List.of("<element>", "<element><elements>"));

        put("<array>", List.of("[<array_elements>]"));
        put("<array_elements>", List.of("<string>", "<string>, <array_elements>"));

        put("<comment>", List.of("# <text>\n"));

        // ---------- Лексика: уже готова ----------

        put("<key>", List.of("<identifier>"));
        put("<identifier>", List.of("<letter>", "<letter><identifier_part>"));
        put("<identifier_part>", List.of("<letter>", "<digit>", "_"));

        put("<string>", List.of("\"<characters>\""));
        put("<characters>", List.of("<character>", "<character><characters>"));
        // На слайде <character> не определён. Берём буквы и пробел: кавычки, ':', '{', '}', '#'
        // и запятые внутри строк сломали бы построчный разбор.
        put("<character>", List.of("<letter>", " "));

        put("<number>", List.of("<digit>", "<digit><number>"));
        put("<boolean>", List.of("true", "false"));
        put("<text>", List.of("<character>", "<character><text>"));

        put("<letter>", LETTERS);
        put("<digit>", List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"));
    }};

    private AdvancedDataGrammar() {
    }

    public static BetterGrammar getBetterGrammar() {
        return new BetterGrammar(ADVANCED_DATA_GRAMMAR);
    }
}
