package lk.ac.mrt.projectx.buildex.models.common;

/**
 * Created by wik2kassa on 12/3/2016.
 */
public class JumpInfo {

    /**
     * the pc of the conditional jump
     */
    public long jump_pc; //
    /**
     * eflags set by this pc
     */
    public long cond_pc; //
    /**
     * the target pc of this jump
     */
    public long target_pc; //
    /**
     * the fall-through pc
     */
    public long fall_pc;   //
    /**
     * the merge point for the taken and not taken paths
     */
    public long merge_pc;  //

    /**
     * example lines in the instruction trace for taken and notTaken jump conditionals
     */
    public long taken;
    /**
     * example lines in the instruction trace for taken and notTaken jump conditionals
     */
    public long not_taken;

    public JumpInfo clone() {
        JumpInfo inst = new JumpInfo();
        inst.jump_pc = jump_pc;
        inst.cond_pc = cond_pc;
        inst.target_pc = target_pc;
        inst.fall_pc = fall_pc;
        inst.merge_pc = merge_pc;
        inst.taken = taken;
        inst.not_taken = not_taken;
        return inst;
    }

    public long getJump_pc() {
        return jump_pc;
    }

    public void setJump_pc(long jump_pc) {
        this.jump_pc = jump_pc;
    }

    public long getCond_pc() {
        return cond_pc;
    }

    public void setCond_pc(long cond_pc) {
        this.cond_pc = cond_pc;
    }

    public long getTarget_pc() {
        return target_pc;
    }

    public void setTarget_pc(long target_pc) {
        this.target_pc = target_pc;
    }

    public long getFall_pc() {
        return fall_pc;
    }

    public void setFall_pc(long fall_pc) {
        this.fall_pc = fall_pc;
    }

    public long getMerge_pc() {
        return merge_pc;
    }

    public void setMerge_pc(long merge_pc) {
        this.merge_pc = merge_pc;
    }

    public long getTaken() {
        return taken;
    }

    public void setTaken(long taken) {
        this.taken = taken;
    }

    public long getNot_taken() {
        return not_taken;
    }

    public void setNot_taken(long not_taken) {
        this.not_taken = not_taken;
    }
}