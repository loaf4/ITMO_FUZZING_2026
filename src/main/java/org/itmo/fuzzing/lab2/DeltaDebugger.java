package org.itmo.fuzzing.lab2;

import java.util.function.Predicate;

/**
 * Дельта-дебаггинг (алгоритм ddmin) на уровне строк — реализуется студентом.
 *
 * <h2>Контракт</h2>
 * <ul>
 *     <li>На вход подаётся строка {@code input}, для которой {@code fails.test(input) == true}
 *     (иначе сокращать нечего — верните {@code input} без изменений).</li>
 *     <li>Результат {@code r} — подстрочная подпоследовательность {@code input}
 *     (символы в исходном порядке), для которой по-прежнему {@code fails.test(r) == true}.</li>
 *     <li>Результат 1-минимален: удаление любого одного символа из {@code r} даёт строку,
 *     на которой {@code fails} возвращает {@code false}.</li>
 *     <li>Алгоритм — классический ddmin: начинаем с {@code n = 2} частей, пробуем выкидывать
 *     каждую часть (проверяем дополнение); при успехе уменьшаем {@code n}, иначе удваиваем,
 *     пока {@code n} не превысит длину строки. Повторные проверки одной и той же строки
 *     имеет смысл кешировать.</li>
 * </ul>
 *
 * <h2>Где применять</h2>
 * <p>Перед добавлением сида в популяцию. Унаследуйтесь от
 * {@link org.itmo.fuzzing.lect9.GreyBoxGrammarFuzzer} и переопределите
 * {@link org.itmo.fuzzing.lect9.GreyBoxGrammarFuzzer#run run}: когда вход открыл новое покрытие,
 * сократите его вызовом {@link #reduce} с предикатом «сокращённый вход по-прежнему даёт то же
 * новое покрытие», и только затем передавайте результат в
 * {@code fragmentMutator.addToFragmentPool(...)} и {@code population.add(...)}.</p>
 *
 * <p>Описание алгоритма: Fuzzing Book, глава «Reducing Failure-Inducing Inputs»
 * (<a href="https://www.fuzzingbook.org/html/Reducer.html">fuzzingbook.org/html/Reducer.html</a>),
 * а также лекция 5, слайды «Редукция» и «Дельта-дебаггинг».</p>
 */
public final class DeltaDebugger {

    private DeltaDebugger() {
    }

    /**
     * Сокращает {@code input}, сохраняя свойство {@code fails}.
     *
     * @param input исходная строка, на которой {@code fails} истинно
     * @param fails проверяемое свойство («вход всё ещё воспроизводит то, что нас интересует»)
     * @return 1-минимальная подстрока-подпоследовательность {@code input}, на которой {@code fails} истинно
     */
    public static String reduce(String input, Predicate<String> fails) {
        // TODO: реализовать ddmin на уровне символов строки.
        throw new UnsupportedOperationException("Реализуйте дельта-дебаггинг (ddmin)");
    }
}
