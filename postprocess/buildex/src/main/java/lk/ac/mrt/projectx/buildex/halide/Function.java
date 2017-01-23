package lk.ac.mrt.projectx.buildex.halide;

import lk.ac.mrt.projectx.buildex.models.Pair;
import lk.ac.mrt.projectx.buildex.trees.AbstractTree;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chathura Widanage
 */
public class Function {
    private List<Pair<RDom, List<AbstractTree>>> reductionTrees;
    private List<AbstractTree> pureTrees;
    private Long variations;

    public Function() {
        this.reductionTrees = new ArrayList<>();
        this.pureTrees = new ArrayList<>();
    }

    public List<Pair<RDom, List<AbstractTree>>> getReductionTrees() {
        return reductionTrees;
    }

    public void setReductionTrees(List<Pair<RDom, List<AbstractTree>>> reductionTrees) {
        this.reductionTrees = reductionTrees;
    }

    public List<AbstractTree> getPureTrees() {
        return pureTrees;
    }

    public void setPureTrees(List<AbstractTree> pureTrees) {
        this.pureTrees = pureTrees;
    }

    public Long getVariations() {
        return variations;
    }

    public void setVariations(Long variations) {
        this.variations = variations;
    }
}
