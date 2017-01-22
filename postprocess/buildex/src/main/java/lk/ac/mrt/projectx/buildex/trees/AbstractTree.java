package lk.ac.mrt.projectx.buildex.trees;

import lk.ac.mrt.projectx.buildex.models.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chathura Widanage
 * @author Rukshan Perera
 */
public class AbstractTree extends Tree {
    private List<Pair<AbstractTree,Boolean>> conditionalTrees=new ArrayList<>();

    public List<Pair<AbstractTree, Boolean>> getConditionalTrees() {
        return conditionalTrees;
    }

    public void setConditionalTrees(List<Pair<AbstractTree, Boolean>> conditionalTrees) {
        this.conditionalTrees = conditionalTrees;
    }

    @Override
    public void simplifyTree() {

    }
}
