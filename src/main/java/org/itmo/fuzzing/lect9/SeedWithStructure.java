package org.itmo.fuzzing.lect9;

import org.itmo.fuzzing.lect3.Seed;
import org.itmo.fuzzing.lect6.tree.DerivationTreeNode;

import java.util.ArrayList;

public class SeedWithStructure extends Seed {
    /** Seeds augmented with structure info */

    private boolean hasStructure;
    private DerivationTreeNode structure;

    public SeedWithStructure(String data) {
        super(data);
        this.hasStructure = false;
        this.structure = new DerivationTreeNode("<empty>", new ArrayList<>()); // Assuming a constructor for DerivationTree
    }

    public boolean hasStructure() {
        return hasStructure;
    }

    public void setHasStructure(boolean hasStructure) {
        this.hasStructure = hasStructure;
    }

    public DerivationTreeNode getStructure() {
        return structure;
    }

    public void setStructure(DerivationTreeNode structure) {
        this.structure = structure;
    }

    @Override
    public String toString() {
        return getData();
    }
}