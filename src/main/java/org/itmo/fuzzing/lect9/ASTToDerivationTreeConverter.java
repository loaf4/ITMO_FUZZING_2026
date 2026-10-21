package org.itmo.fuzzing.lect9;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.itmo.fuzzing.lect6.tree.DerivationTree;
import org.itmo.fuzzing.lect6.tree.DerivationTreeNode;

import java.util.ArrayList;
import java.util.List;

public class ASTToDerivationTreeConverter {

    public static DerivationTree convert(ParseTree astRoot) {
        // Преобразуем корень AST в узел DerivationTreeNode
        DerivationTreeNode root = convertNode(astRoot);
        return new DerivationTree(root);
    }

    private static DerivationTreeNode convertNode(ParseTree node) {

        String value = "";
        if (node instanceof TerminalNode) {
            value = node.getText();
        } else {
            value = "<" + node.getClass().getSimpleName().replace("Context", "").toLowerCase() + ">";
        }
        // Получаем дочерние узлы
        List<DerivationTreeNode> children = new ArrayList<>();
        for (int i = 0; i < node.getChildCount(); i++) {
            ParseTree child = node.getChild(i);
            DerivationTreeNode childNode = convertNode(child);
            children.add(childNode);
        }

        return new DerivationTreeNode(value, children);
    }
}
