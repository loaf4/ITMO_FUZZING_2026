# ANTLR-грамматика для lab2

Положите сюда `JSON.g4` из официального репозитория грамматик ANTLR:
<https://github.com/antlr/grammars-v4/blob/master/json/JSON.g4>

После этого:

```bash
./gradlew compileJava
```

Gradle-плагин `antlr` сгенерирует `JSONLexer`, `JSONParser` (и visitor/listener) в пакете
`org.itmo.fuzzing.lab2.parser` — исходники окажутся в
`build/generated-src/antlr/main/org/itmo/fuzzing/lab2/parser/`.

Дальше напишите JSON-аналог `org.itmo.fuzzing.lect9.FuzzParser`: строка → `JSONLexer` →
`JSONParser` → `ParseTree` → `ASTToDerivationTreeConverter.convert(...)` → корень дерева вывода,
и передайте его в конструктор
`FragmentMutator(Parser parser, Set<String> tokens, Function<String, DerivationTreeNode> parse)`.

Не забудьте: стартовое правило грамматики JSON называется не `start` (посмотрите в `JSON.g4`).
