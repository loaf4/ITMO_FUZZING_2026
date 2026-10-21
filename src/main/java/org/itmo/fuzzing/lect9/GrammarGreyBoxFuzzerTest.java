package org.itmo.fuzzing.lect9;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.itmo.fuzzing.lect2.FunctionRunner;
import org.itmo.fuzzing.lect3.GreyBoxFuzzer;
import org.itmo.fuzzing.lect3.PowerSchedule;
import org.itmo.fuzzing.lect6.tree.DerivationTree;
import org.itmo.fuzzing.lect6.tree.DerivationTreePrinter;
import org.itmo.fuzzing.lect9.parser.HTMLLexer;
import org.itmo.fuzzing.lect9.parser.HTMLParser;
import org.jsoup.Jsoup;

import java.util.Arrays;
import java.util.Set;

public class GrammarGreyBoxFuzzerTest {

    void main(String[] args) {
//        var jsoupRunner = new FunctionRunner(s -> {
//            Jsoup.parse(s);
//            return null;
//        });
//        int N = 5_000;
//
//        var fuzzer = new GreyBoxFuzzer(
//                Arrays.asList(" "),
//                new FuzzMutator(),
//                new PowerSchedule(),
//                1,
//                5
//        );
//
//        var startTime = System.currentTimeMillis();
//        fuzzer.fuzz(jsoupRunner, N);
//        var endTime = System.currentTimeMillis();
//        var coverage = fuzzer.coveragesSeen;
//        System.out.println("Time taken: " + (endTime - startTime));
//        System.out.println("COVERED = " + coverage.size());
//        System.exit(0);
//
//
//        var fuzzer = new GreyBoxFuzzer(
//                Arrays.asList(" "),
//                new DictMutator(Arrays.asList("<a>", "</a>", "<a/>", "='a'")),
//                new PowerSchedule(),
//                1,
//                5
//        );
//        var startTime = System.currentTimeMillis();
//        fuzzer.fuzz(jsoupRunner, N);
//        var endTime = System.currentTimeMillis();
//        var coverage = fuzzer.coveragesSeen;
//        System.out.println("Time taken: " + (endTime - startTime));
//        System.out.println("COVERED = " + coverage.size());
//        System.exit(0);


        var text = "<html>TEXT</html>";
        CharStream charStream = CharStreams.fromString(text);
//
//         Создание лексера и парсера
        HTMLLexer lexer = new HTMLLexer(charStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        HTMLParser parser = new HTMLParser(tokens);

        // Запуск парсинга
//        ParseTree tree = parser.start(); // Начинаем с правила start
//        var derivationTree = ASTToDerivationTreeConverter.convert(tree);
//        DerivationTreePrinter.printTree(derivationTree);
//        System.exit(0);
        var validSeed = new SeedWithStructure(
                "<html><head><title>Hello</title></head><body>World<br/></body></html>");
        var tree = FuzzParser.parse("<html><head><title>Hello</title></head><body>World<br/></body></html>");
        var mutator = new FragmentMutator(parser, Set.of("<id>", "<text>"));
        mutator.addToFragmentPool(validSeed);
//        for (var key : mutator.fragments.entrySet()) { // Assuming a getter for fragments
//            System.out.println(key.getKey());
//            for (var f : key.getValue()) { // Assuming a getter for the list of fragments
//                System.out.print("|-" ); // Assuming treeToString is a defined method
//                System.out.println(f.allTerminals());
//            }
//        }
//        System.out.println("----------");
//        System.out.println(validSeed);
//        var mutatedSeed = mutator.mutate(validSeed);
//        System.out.println("&&&&&&&&&&&&&&&&&");
//        System.out.println(mutatedSeed);
//        System.exit(0);

//
        var jsoupRunner = new FunctionRunner(s -> {
            Jsoup.parse(s);
            return null;
        });
        int N = 30_000;

        var fuzzer = new GreyBoxGrammarFuzzer(
                Arrays.asList(validSeed.getData()),
                mutator,
                new FuzzMutator(),
                new PowerSchedule(),
                1,
                5
        );
        var startTime = System.currentTimeMillis();
        fuzzer.fuzz(jsoupRunner, N);
        var endTime = System.currentTimeMillis();
        var coverage = fuzzer.coveragesSeen;
        System.out.println("Time taken: " + (endTime - startTime));
        System.out.println("COVERED = " + coverage.size());
        System.exit(0);
    }
}
