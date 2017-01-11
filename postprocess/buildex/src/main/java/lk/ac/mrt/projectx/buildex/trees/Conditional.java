package lk.ac.mrt.projectx.buildex.trees;

import lk.ac.mrt.projectx.buildex.models.common.JumpInfo;

/**
 * Created by krv on 1/11/17.
 */
public class Conditional {

    //region Private variables
    private JumpInfo jumpInfo;
    private Integer lineCond; // this is the cond_pc location
    private Integer lineJump;
    private ConcreteTree tree;
    private Boolean taken;
    //endregion Private variables

    //region Public methods

    public JumpInfo getJumpInfo() {
        return jumpInfo;
    }

    public void setJumpInfo(JumpInfo jumpInfo) {
        this.jumpInfo = jumpInfo;
    }

    public Integer getLineCond() {
        return lineCond;
    }

    public void setLineCond(Integer lineCond) {
        this.lineCond = lineCond;
    }

    public Integer getLineJump() {
        return lineJump;
    }

    public void setLineJump(Integer lineJump) {
        this.lineJump = lineJump;
    }

    public ConcreteTree getTree() {
        return tree;
    }

    public void setTree(ConcreteTree tree) {
        this.tree = tree;
    }

    public Boolean getTaken() {
        return taken;
    }

    public void setTaken(Boolean taken) {
        this.taken = taken;
    }

    //endregion Public methods
}
