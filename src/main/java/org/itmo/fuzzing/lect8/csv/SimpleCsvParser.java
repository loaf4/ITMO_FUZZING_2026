package org.itmo.fuzzing.lect8.csv;

import org.itmo.fuzzing.lect6.tree.DerivationTree;
import org.itmo.fuzzing.lect6.tree.DerivationTreeNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.itmo.fuzzing.lect8.csv.CsvGrammar.START_SYMBOL;

public class SimpleCsvParser {

public static DerivationTree simpleParseCsv(String csv) {
    var children = new ArrayList<DerivationTreeNode>();
    var rootNode = new DerivationTreeNode(START_SYMBOL, children);
    String[] lines = csv.split("\n");
    for (int i = 0; i < lines.length; i++) {
        String line = lines[i];
        String recordName = String.format("record %d", i);
        List<DerivationTreeNode> cells = new ArrayList<>();
        String[] cellValues = line.split(",");

        for (String cell : cellValues) {
            cells.add(new DerivationTreeNode(cell.trim(), new ArrayList<>())); // Assuming DerivationTree takes a cell value and children
        }

        children.add(new DerivationTreeNode(recordName, cells)); // Adding the record to children
    }

    return new DerivationTree(rootNode);
}

    public static DerivationTree parseCsv(String myString) {
        List<DerivationTreeNode> children = new ArrayList<>();
        DerivationTreeNode rootNode = new DerivationTreeNode(START_SYMBOL, children);

        String[] lines = myString.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            List<DerivationTreeNode> cells = new ArrayList<>();
            for (String cell : commaSplit(line)) {
                cells.add(new DerivationTreeNode(cell, new ArrayList<>())); // Create a new DerivationTree for each cell
            }
            children.add(new DerivationTreeNode("record " + i, cells)); // Add record with its cells to children
        }
        return new DerivationTree(rootNode);
    }

    public static int parseQuote(String string, int i) {
        int v = string.indexOf('"', i + 1);
        return (v >= 0) ? v : -1;
    }

    public static int findComma(String string, int i) {
        int slen = string.length();
        while (i < slen) {
            if (string.charAt(i) == '"') {
                i = parseQuote(string, i);
                if (i == -1) {
                    return -1;
                }
            }
            if (string.charAt(i) == ',') {
                return i; // Return the index of the comma
            }
            i++;
        }
        return -1; // No comma found
    }

    public static Iterable<String> commaSplit(String string) {
        return new Iterable<String>() {
            @Override
            public Iterator<String> iterator() {
                return new Iterator<String>() {
                    private int i = 0; // Index in the string

                    @Override
                    public boolean hasNext() {
                        return i < string.length(); // Check if there are more elements
                    }

                    @Override
                    public String next() {
                        if (!hasNext()) {
                            throw new java.util.NoSuchElementException();
                        }
                        int c = findComma(string, i);
                        String result;

                        if (c == -1) {
                            result = string.substring(i); // Get the remainder of the string
                            i = string.length(); // Update index to end
                        } else {
                            result = string.substring(i, c); // Get the substring up to the comma
                            i = c + 1; // Update index to the next position
                        }
                        return result;
                    }
                };
            }
        };
    }
}
