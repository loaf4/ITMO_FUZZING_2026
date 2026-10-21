package org.itmo.fuzzing.practice1;

import org.itmo.fuzzing.lect6.fuzzer.GrammarFuzzer;
import org.itmo.fuzzing.lect6.tree.DerivationTree;
import org.itmo.fuzzing.task1.DataParser;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Точка входа практического задания по лекции 4: найти вход, на котором
 * {@link DataParser} бросает {@code IllegalStateException("You have found a bug")}.
 *
 * <h2>Задание</h2>
 * <ol>
 *     <li>Допишите грамматику формата AdvancedData в {@link AdvancedDataGrammar}: лексика уже
 *     готова, осталась структура документа (блоки, массивы, комментарии).</li>
 *     <li>Реализуйте генерацию документов на деревьях вывода через {@link GrammarFuzzer} из
 *     лекции 4 ({@code initTree}, {@code expandTree}, {@code treeToString}) в
 *     {@link #generate(GrammarFuzzer)}.</li>
 *     <li>Запускайте парсер на сгенерированных документах, пока он не упадёт. Цикл, запуск парсера
 *     и подсчёт статистики уже написаны в {@link #main(String[])}.</li>
 * </ol>
 *
 * <h2>Ответы парсера</h2>
 * <ul>
 *     <li>«I am okay» — документ разобран;</li>
 *     <li>«I am failed but everything fine» — документ некорректен, парсер это обработал, это не баг;</li>
 *     <li>{@code IllegalStateException: You have found a bug} — цель работы.</li>
 * </ul>
 *
 * <p>Не меняйте {@link DataParser} и не подбирайте вход по его исходному коду: баг должен найти
 * фаззер. Если на большом числе итераций баг не находится, разберитесь, каких документов ваш
 * генератор почти не порождает, и настройте его. Подумайте про пороги фаз {@code expandTree} в
 * конструкторе {@link GrammarFuzzer} и про то, все ли правила грамматики реально используются.</p>
 *
 * <h2>Что сдать</h2>
 * <ul>
 *     <li>грамматику и генератор;</li>
 *     <li>найденный вход ({@code build/advanced-data-bug.txt});</li>
 *     <li>статистику: число итераций до бага и долю ответов «I am okay» на нескольких запусках;</li>
 *     <li>короткое объяснение, какие настройки генератора повлияли на результат и почему.</li>
 * </ul>
 *
 * <p>Аргументы: {@code [limit]} — максимум итераций, по умолчанию 1 000 000.</p>
 */
public final class AdvancedDataFuzzer {

    private AdvancedDataFuzzer() {
    }

    /**
     * Создаёт генератор документов.
     *
     * <p>TODO: при необходимости используйте конструктор
     * {@code GrammarFuzzer(grammar, startSymbol, minNonterminals, maxNonterminals, disp, log)}
     * и подберите пороги.</p>
     */
    static GrammarFuzzer createFuzzer() {
        return new GrammarFuzzer(AdvancedDataGrammar.getBetterGrammar());
    }

    /**
     * Генерирует один документ.
     *
     * <p>TODO: постройте дерево вывода от стартового символа, раскройте его и верните строку.</p>
     */
    static String generate(GrammarFuzzer fuzzer) {
        DerivationTree tree = fuzzer.initTree();
        tree = fuzzer.expandTree(tree);
        return tree.treeToString();
    }

    public static void main(String[] args) throws Exception {
        int limit = args.length > 0 ? Integer.parseInt(args[0]) : 1_000_000;
        int okay = 0;
        int failed = 0;
        int iteration = 0;
        String found = null;

        GrammarFuzzer fuzzer = createFuzzer();
        while (iteration < limit) {
            iteration++;
            String document = generate(fuzzer);
            String answer;
            try {
                answer = runParser(document);
            } catch (IllegalStateException bug) {
                found = document;
                break;
            }
            if (answer.contains("I am okay")) {
                okay++;
            } else {
                failed++;
            }
        }

        System.out.printf("iterations=%d okay=%d failed=%d okayShare=%.2f%%%n",
                iteration, okay, failed, iteration == 0 ? 0.0 : 100.0 * okay / iteration);
        if (found == null) {
            System.out.println("bug=not-found");
            return;
        }
        Path result = Path.of("build", "advanced-data-bug.txt");
        Files.createDirectories(result.getParent());
        Files.writeString(result, found, StandardCharsets.UTF_8);
        System.out.println("bug=found, saved to " + result);
        System.out.print(found);
    }

    /** Запускает парсер на документе и возвращает то, что он напечатал. */
    private static String runParser(String document) throws Exception {
        Path input = Files.createTempFile("advanced-data-", ".txt");
        PrintStream previous = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            Files.writeString(input, document, StandardCharsets.UTF_8);
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            DataParser.main(new String[]{input.toString()});
        } finally {
            System.setOut(previous);
            Files.deleteIfExists(input);
        }
        return output.toString(StandardCharsets.UTF_8);
    }
}
