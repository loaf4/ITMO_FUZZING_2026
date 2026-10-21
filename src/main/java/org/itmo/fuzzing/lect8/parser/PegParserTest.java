package org.itmo.fuzzing.lect8.parser;

import org.itmo.fuzzing.lect6.ExprGrammar;
import org.itmo.fuzzing.lect6.tree.DerivationTree;
import org.itmo.fuzzing.lect6.tree.DerivationTreePrinter;

public class PegParserTest {

    public static void main(String[] args) throws Exception {
        var myString = "12";
        var grammar = ExprGrammar.getBetterGrammar();
        var parser = new PEGParser(grammar);
        var r = parser.unifyRule(grammar.rules.get("<integer>").stream().map(expansion -> expansion.toString()).toList(), myString, 0);
        var r1 = r.first;
        var r2 = r.second;
        System.out.println(r1);
        System.out.println(r2);
//        var tree = parser.parse(myString);
//        for (var treeNode : tree) {
//            var tt = new DerivationTree(treeNode);
//            DerivationTreePrinter.printTree(tt);
//        }
    }
}
