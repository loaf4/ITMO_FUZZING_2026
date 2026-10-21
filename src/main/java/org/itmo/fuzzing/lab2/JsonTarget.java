package org.itmo.fuzzing.lab2;

import com.google.gson.JsonParser;

/**
 * Цель фаззинга второй лабораторной работы — JSON-парсер библиотеки Gson.
 *
 * <p>Класс выдан готовым. Используйте его через {@link org.itmo.fuzzing.lect2.FunctionRunner}:</p>
 * <pre>{@code
 * var runner = new FunctionRunner(JsonTarget::parse);
 * }</pre>
 *
 * <p>Покрытие собирается агентом из модуля {@code instrumentation}: пакет {@code com/google/gson}
 * входит в список инструментируемых по умолчанию. Запускайте фаззер с агентом:</p>
 * <pre>{@code
 * ./gradlew runWithAgent -PmainClass=org.itmo.fuzzing.lab2.JsonGrammarFuzzer
 * }</pre>
 * <p>Без агента покрытие пустое, ни один сид не попадёт в популяцию и
 * {@link org.itmo.fuzzing.lect9.GreyBoxGrammarFuzzer} остановится с понятной ошибкой.</p>
 */
public final class JsonTarget {

    private JsonTarget() {
    }

    /**
     * Разбирает строку как JSON. Некорректный JSON приводит к исключению
     * ({@code JsonSyntaxException} и др.) — {@code FunctionRunner} считает такой запуск {@code FAIL}.
     *
     * @param s входная строка
     * @return дерево {@code JsonElement}
     */
    public static Object parse(String s) {
        return JsonParser.parseString(s);
    }
}
