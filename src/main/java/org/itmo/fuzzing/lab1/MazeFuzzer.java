package org.itmo.fuzzing.lab1;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.itmo.fuzzing.lect2.FunctionRunner;
import org.itmo.fuzzing.lect3.AFLFastSchedule;
import org.itmo.fuzzing.lect3.AdvancedMutationFuzzer;
import org.itmo.fuzzing.lect3.CountingGreyboxFuzzer;
import org.itmo.fuzzing.lect3.GreyBoxFuzzer;
import org.itmo.fuzzing.lect3.PowerSchedule;
import org.itmo.fuzzing.lect3.DirectedSchedule;
import org.itmo.fuzzing.lect3.Seed;

/**
 * Точка входа первой лабораторной работы: поиск входа, для которого
 * {@link MazeGenerated#maze(String)} возвращает {@code SOLVED}.
 *
 * <p>Выполняйте работу последовательно. Все варианты должны использовать одинаковые начальные
 * сиды, {@link MazeMutator мутатор} и бюджет запусков. Отличаться должен только способ использования
 * обратной связи.</p>
 *
 * <h2>Что использовать из лекции 3</h2>
 * <p>Не переписывайте общий цикл mutation-based и greybox-фаззинга. Используйте и расширяйте
 * готовые классы из пакета {@code org.itmo.fuzzing.lect3}:</p>
 * <ul>
 *     <li>{@link AdvancedMutationFuzzer} — основа dumb black-box режима: генерация кандидата из
 *     начальных сидов и мутации без использования покрытия при пополнении corpus;</li>
 *     <li>{@link GreyBoxFuzzer} вместе с {@link PowerSchedule} — основа coverage-guided режима:
 *     сохранение входов, которые открыли новое покрытие, и выбор сида из corpus;</li>
 *     <li>{@link GreyBoxFuzzer} вместе с собственной стратегией, наследующей
 *     {@link PowerSchedule}, — основа directed-режима: энергия сида должна зависеть от расстояния
 *     до целевого метода;</li>
 *     <li>{@link Seed} — готовая модель сида с данными, покрытием, расстоянием и энергией;</li>
 *     <li>{@link CountingGreyboxFuzzer} и {@link AFLFastSchedule} — примеры того, как расширять
 *     greybox-фаззер, учитывать статистику путей и переопределять расчёт энергии;</li>
 *     <li>{@link FunctionRunner} — готовый адаптер для запуска целевой функции и получения
 *     покрытия; используйте его с {@code MazeGenerated::maze}.</li>
 * </ul>
 *
 * <h2>Этап 1. Dumb black-box</h2>
 * <ol>
 *     <li>Создайте {@link AdvancedMutationFuzzer} с начальным сидом, например {@code "D"}, и
 *     экземпляром {@link MazeMutator}.</li>
 *     <li>Мутируйте вход и запускайте целевую функцию.</li>
 *     <li>Не используйте покрытие, call graph или расстояния до цели.</li>
 *     <li>Остановитесь при {@code SOLVED} либо после исчерпания бюджета.</li>
 * </ol>
 *
 * <h2>Этап 2. Coverage-guided fuzzing</h2>
 * <ol>
 *     <li>Используйте {@link GreyBoxFuzzer}, {@link PowerSchedule} и тот же {@link MazeMutator}.</li>
 *     <li>Собирайте покрытие каждого запуска через выданную instrumentation-инфраструктуру.</li>
 *     <li>Добавляйте вход в corpus, только если он открыл новое покрытие.</li>
 *     <li>Выбирайте сиды из corpus без знания положения целевой клетки.</li>
 * </ol>
 *
 * <h2>Этап 3. Directed greybox fuzzing</h2>
 * <ol>
 *     <li>Дополните ASM-инструментацию сбором рёбер {@code caller -> callee}.</li>
 *     <li>Постройте call graph методов {@code tile_*} и вычислите кратчайшие расстояния до метода,
 *     имя которого возвращает {@link MazeGenerated#targetTile()}.</li>
 *     <li>Определите расстояние {@link Seed} до цели по покрытым им методам.</li>
 *     <li>Реализуйте собственную стратегию {@link PowerSchedule}, назначающую больше энергии
 *     сидам с меньшим расстоянием до цели.</li>
 * </ol>
 *
 * <h2>Что выдано</h2>
 * <ul>
 *     <li>целевая функция {@link MazeGenerated#maze(String)} и генератор лабиринта;</li>
 *     <li>{@link FunctionRunner} и instrumentation-инфраструктура сбора покрытия;</li>
 *     <li>общий цикл фаззинга, corpus, модель сида и базовые стратегии из лекции 3;</li>
 *     <li>контракты {@link MazeMutator} и этой точки входа.</li>
 * </ul>
 *
 * <h2>Что требуется реализовать</h2>
 * <ul>
 *     <li>операции {@link MazeMutator} для алфавита {@code L/R/U/D};</li>
 *     <li>конфигурацию и запуск трёх режимов на выданном каркасе;</li>
 *     <li>сбор рёбер call graph в ASM-инструментации;</li>
 *     <li>расчёт расстояний от методов до {@link MazeGenerated#targetTile()};</li>
 *     <li>расстояние сида и directed-стратегию назначения энергии;</li>
 *     <li>сбор и сравнение результатов эксперимента.</li>
 * </ul>
 *
 * <h2>Сравнение</h2>
 * <p>Для каждого режима проведите несколько запусков и сравните долю успешных запусков,
 * медианное число выполнений цели до {@code SOLVED} и время. Не включайте заранее известный
 * маршрут в начальный corpus.</p>
 */
public final class MazeFuzzer {

    private MazeFuzzer() {
    }

    /**
     * Реализуйте здесь конфигурацию и запуск трёх режимов на каркасе из лекции 3, а также вывод
     * сопоставимых результатов эксперимента.
     *
     * @param args параметры запуска в выбранном вами формате
     */
    public static void main(String[] args) throws java.io.IOException {
        int budget = 100_000;
        List<String> corpus = List.of("D");
        MazeMutator mutator = new MazeMutator();
        FunctionRunner runner = new FunctionRunner(MazeGenerated::maze);

        AdvancedMutationFuzzer blackBoxFuzzer = new AdvancedMutationFuzzer(corpus, mutator, new PowerSchedule(), 1, 1);
        runOnce(blackBoxFuzzer, runner, budget);

        // GreyBoxFuzzer greyBoxFuzzer = new GreyBoxFuzzer(corpus, mutator, new PowerSchedule(), 1, 1);
        // runOnce(greyBoxFuzzer, runner, budget);

        // Map<String, Integer> distances = AstCallGraph.distancesTo(
        //         AstCallGraph.build(AstCallGraph.MAZE_SOURCE), MazeGenerated.targetTile());
        // GreyBoxFuzzer directedFuzzer = new GreyBoxFuzzer(corpus, mutator, new DirectedSchedule(distances), 1, 1);
        // runOnce(directedFuzzer, runner, budget);
    }

    private static int runOnce(AdvancedMutationFuzzer fuzzer, FunctionRunner runner, int budget) {
        Map<String, Integer> stats = new LinkedHashMap<>();
        stats.put("INVALID", 0);
        stats.put("VALID", 0);
        stats.put("SOLVED", 0);
        long start = System.nanoTime();

        for (int i = 1; i <= budget; ++i) {
            String input = fuzzer.fuzz();
            Object res = fuzzer.run(runner, input);
            String status = res == null ? "ERROR" : res.toString().lines().findFirst().orElse("");
            stats.merge(status, 1, Integer::sum);

            if (status.equals("SOLVED")) {
                System.out.println("SOLVED after " + i + " runs in " + elapsedMs(start) + " ms: " + input);
                printStats(stats);
                return i;
            }
        }
        System.out.println("Lost all budget in " + elapsedMs(start) + " ms");
        printStats(stats);
        return -1;
    }

    private static long elapsedMs(long startNanos) {
        return (System.nanoTime() - startNanos) / 1_000_000;
    }

    private static void printStats(Map<String, Integer> stats) {
        int total = stats.values().stream().mapToInt(Integer::intValue).sum();
        System.out.println("Stats (" + total + " runs):");
        stats.forEach((status, count) ->
                System.out.printf("  %-8s %7d  (%.2f%%)%n", status, count, 100.0 * count / total));
    }
}
