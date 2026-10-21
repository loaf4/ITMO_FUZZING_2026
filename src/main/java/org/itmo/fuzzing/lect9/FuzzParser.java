package org.itmo.fuzzing.lect9;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.tree.ParseTree;
import org.itmo.fuzzing.lect6.tree.DerivationTreeNode;
import org.itmo.fuzzing.lect9.parser.HTMLLexer;
import org.itmo.fuzzing.lect9.parser.HTMLParser;

public class FuzzParser {

    public static DerivationTreeNode parse(String input) {
        CharStream charStream = CharStreams.fromString(input);

        // Создание лексера и парсера
        HTMLLexer lexer = new HTMLLexer(charStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        HTMLParser parser = new HTMLParser(tokens);

        // Запуск парсинга
        ParseTree tree = parser.start(); // Начинаем с правила start
        var derivationTree = ASTToDerivationTreeConverter.convert(tree);
        return derivationTree.getRoot();
    }

}
