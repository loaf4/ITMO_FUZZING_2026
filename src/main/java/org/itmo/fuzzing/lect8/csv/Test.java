package org.itmo.fuzzing.lect8.csv;

import org.itmo.fuzzing.lect6.tree.DerivationTreeNode;
import org.itmo.fuzzing.lect6.tree.DerivationTreePrinter;
import org.itmo.fuzzing.lect8.GrammarCoverageFuzzer;

import java.util.ArrayList;
import java.util.Arrays;

public class Test {

    public static void main(String[] args) {
//        var myString = "1997,van,Ford,E350" + "\n" + "2000,car,Lada,2107";
        var processor = new VehicleProcessor();
//        var result = processor.processInventory(myString);
//        System.out.println(result);
        var grammar = CsvGrammar.getBetterGrammar();
//        var fuzzer = new GrammarCoverageFuzzer(grammar);
        var validInfos = new ArrayList<String>();
//        for (int i = 0; i < 100; i++) {
//            var info = fuzzer.fuzz();
//            System.out.println(info);
//            try {
//                processor.processInventory(info);
//                validInfos.add(info);
//            } catch (Throwable e) {
//                System.out.println(e.getMessage());
//            }
//        }
//        System.out.println("VALID DATA SIZE: " + validInfos.size());
//

        var pooledFuzzer = new PooledGrammarFuzzer(grammar);
        pooledFuzzer.updateCache("<item>", Arrays.asList(
                new DerivationTreeNode("<item>", Arrays.asList(new DerivationTreeNode("car", Arrays.asList()))),
                new DerivationTreeNode("<item>", Arrays.asList(new DerivationTreeNode("van", Arrays.asList())))
        ));
        for (int i = 0; i < 1000; i++) {
            var info = pooledFuzzer.fuzz();
            try {
                processor.processInventory(info);
                validInfos.add(info);
            } catch (Throwable e) {
                System.out.println(e.getMessage());
            }
        }
        System.out.println("VALID DATA SIZE: " + validInfos.size());
//
////        var tree = SimpleCsvParser.simpleParseCsv(myString);
////        DerivationTreePrinter.printTree(tree);
//
//        var newString = "1997,Ford,E350,\"ac, abs, moon\",3000.00";
//        var newTree = SimpleCsvParser.parseCsv(newString);
//        DerivationTreePrinter.printTree(newTree);
    }
}
