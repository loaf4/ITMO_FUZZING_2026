package org.itmo.fuzzing.lab2;

import org.itmo.fuzzing.lect2.FunctionRunner;
import org.itmo.fuzzing.lect3.PowerSchedule;
import org.itmo.fuzzing.lect9.ASTToDerivationTreeConverter;
import org.itmo.fuzzing.lect9.FragmentMutator;
import org.itmo.fuzzing.lect9.FuzzParser;
import org.itmo.fuzzing.lect9.GreyBoxGrammarFuzzer;
import org.itmo.fuzzing.lect9.SeedWithStructure;

/**
 * Точка входа второй лабораторной работы (лекция 5): grammar-based greybox фаззинг
 * JSON-парсера с редукцией сидов. Дедлайн: <b>22.10.2026</b>.
 *
 * <h2>Задание (со слайда «Домашнее задание»)</h2>
 * <ol>
 *     <li>Применить {@link GreyBoxGrammarFuzzer} к парсеру JSON ({@link JsonTarget}, библиотека Gson).</li>
 *     <li>Взять ANTLR-грамматику JSON с официального сайта
 *     (<a href="https://github.com/antlr/grammars-v4/tree/master/json">antlr/grammars-v4</a>).</li>
 *     <li>Оценить важность набора начальных сидов — привести статистические данные.</li>
 *     <li>Реализовать алгоритм дельта-дебаггинга на уровне строк ({@link DeltaDebugger}) и
 *     <b>применять его перед добавлением сида в популяцию</b>.</li>
 *     <li>Привести статистические данные.</li>
 * </ol>
 *
 * <h2>Что использовать из лекции 5</h2>
 * <p>Не переписывайте цикл greybox-фаззинга и мутатор фрагментов — используйте готовые классы
 * из {@code org.itmo.fuzzing.lect9}:</p>
 * <ul>
 *     <li>{@link GreyBoxGrammarFuzzer} — greybox-фаззер: сохраняет входы с новым покрытием,
 *     у которых удалось построить дерево разбора, и мутирует их фрагментами и посимвольно;</li>
 *     <li>{@link FragmentMutator} — пул фрагментов и мутации swap/delete. Конструктор
 *     {@code FragmentMutator(parser, tokens, parse)} принимает функцию разбора строки в дерево
 *     вывода — передайте туда свой JSON-разборщик;</li>
 *     <li>{@link SeedWithStructure} — сид вместе с деревом вывода;</li>
 *     <li>{@link ASTToDerivationTreeConverter} — перевод ANTLR {@code ParseTree} в дерево вывода;</li>
 *     <li>{@link FuzzParser} — образец разборщика для HTML: напишите его JSON-аналог;</li>
 *     <li>{@link FunctionRunner} — запуск цели с покрытием: {@code new FunctionRunner(JsonTarget::parse)};</li>
 *     <li>{@link PowerSchedule} — расписание выбора сидов (можно оставить базовое);</li>
 *     <li>Gradle-задача {@code runWithAgent} — запуск с агентом покрытия:
 *     {@code ./gradlew runWithAgent -PmainClass=org.itmo.fuzzing.lab2.JsonGrammarFuzzer}.</li>
 * </ul>
 *
 * <h2>Что выдано</h2>
 * <ul>
 *     <li>цель {@link JsonTarget#parse(String)} и инструментация пакета {@code com/google/gson};</li>
 *     <li>генерация ANTLR-парсера в сборке: положите {@code JSON.g4} в
 *     {@code src/main/antlr/org/itmo/fuzzing/lab2/parser/}, после {@code ./gradlew compileJava}
 *     появятся {@code JSONLexer}/{@code JSONParser} в пакете {@code org.itmo.fuzzing.lab2.parser}
 *     (см. README в этой папке);</li>
 *     <li>greybox-фаззер, мутатор фрагментов, модель сида и расписание из лекций 3 и 5;</li>
 *     <li>контракт {@link DeltaDebugger#reduce}.</li>
 * </ul>
 *
 * <h2>Что требуется реализовать</h2>
 * <ul>
 *     <li>JSON-разборщик строки в дерево вывода (аналог {@link FuzzParser}) на сгенерированном парсере;</li>
 *     <li>конфигурацию {@link FragmentMutator} и {@link GreyBoxGrammarFuzzer} для JSON;</li>
 *     <li>{@link DeltaDebugger#reduce} и наследника {@link GreyBoxGrammarFuzzer}, который сокращает
 *     вход с новым покрытием перед добавлением в популяцию;</li>
 *     <li>эксперимент с разными наборами начальных сидов и сбор статистики.</li>
 * </ul>
 *
 * <h2>Что сдать</h2>
 * <ul>
 *     <li>код и инструкцию запуска;</li>
 *     <li>сравнение нескольких наборов начальных сидов (например: один пустой объект, несколько
 *     простых значений, разнообразные вложенные документы) при одинаковом бюджете запусков:
 *     покрытие и его рост, размер популяции; несколько запусков на конфигурацию, медиана и разброс;</li>
 *     <li>сравнение «с редукцией сидов» и «без неё»: покрытие, средняя длина сида, скорость;</li>
 *     <li>выводы.</li>
 * </ul>
 */
public final class JsonGrammarFuzzer {

    private JsonGrammarFuzzer() {
    }

    /**
     * Реализуйте здесь настройку и запуск grammar-based greybox фаззинга JSON, эксперимент с
     * наборами сидов и с редукцией, вывод сопоставимой статистики.
     *
     * @param args параметры запуска в выбранном вами формате
     */
    public static void main(String[] args) {
        // TODO: настроить FragmentMutator(JSONParser, tokens, свой JSON-разборщик),
        //       GreyBoxGrammarFuzzer (с редукцией сидов) и провести эксперименты.
        throw new UnsupportedOperationException("Реализуйте фаззинг JSON второй лабораторной работы");
    }
}
