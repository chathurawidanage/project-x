package lk.ac.mrt.projectx.buildex.trees;

import lk.ac.mrt.projectx.buildex.models.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chathura Widanage
 */
public class AbstractTreeCharacteristic {
    private AbstractTree abstractTree;
    private boolean recusrsive;
    private List<Pair<Long,Long>> extents=new ArrayList<>();
    private boolean gapsInRandom;
    private AbstractNode redNode;

    public AbstractTree getAbstractTree() {
        return abstractTree;
    }

    public void setAbstractTree(AbstractTree abstractTree) {
        this.abstractTree = abstractTree;
    }

    public boolean isRecusrsive() {
        return recusrsive;
    }

    public void setRecusrsive(boolean recusrsive) {
        this.recusrsive = recusrsive;
    }

    public List<Pair<Long, Long>> getExtents() {
        return extents;
    }

    public void setExtents(List<Pair<Long, Long>> extents) {
        this.extents = extents;
    }

    public boolean isGapsInRandom() {
        return gapsInRandom;
    }

    public void setGapsInRandom(boolean gapsInRandom) {
        this.gapsInRandom = gapsInRandom;
    }

    public AbstractNode getRedNode() {
        return redNode;
    }

    public void setRedNode(AbstractNode redNode) {
        this.redNode = redNode;
    }
}
