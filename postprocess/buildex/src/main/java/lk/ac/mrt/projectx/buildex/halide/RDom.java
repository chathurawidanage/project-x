package lk.ac.mrt.projectx.buildex.halide;

import lk.ac.mrt.projectx.buildex.models.Pair;
import lk.ac.mrt.projectx.buildex.trees.AbstractNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chathura Widanage
 */
public class RDom {
    private String name;
    private RDomType rDomType;
    private AbstractNode redNode;
    private List<Pair<Long, Long>> extents;
    private List<List<Long>> abstractIndexes;

    public RDom() {
        this.extents = new ArrayList<>();
        this.abstractIndexes = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RDomType getrDomType() {
        return rDomType;
    }

    public void setrDomType(RDomType rDomType) {
        this.rDomType = rDomType;
    }

    public AbstractNode getRedNode() {
        return redNode;
    }

    public void setRedNode(AbstractNode redNode) {
        this.redNode = redNode;
    }

    public List<Pair<Long, Long>> getExtents() {
        return extents;
    }

    public void setExtents(List<Pair<Long, Long>> extents) {
        this.extents = extents;
    }

    public List<List<Long>> getAbstractIndexes() {
        return abstractIndexes;
    }

    public void setAbstractIndexes(List<List<Long>> abstractIndexes) {
        this.abstractIndexes = abstractIndexes;
    }
}
