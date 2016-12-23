package lk.ac.mrt.projectx.buildex;

import javafx.util.Pair;

import java.util.List;

/**
 * Created by wik2kassa on 12/3/2016.
 */
public class StaticInfo {
    /**
     * // module for this instruction (we encode as integers)
     */
    private long module_no;
    /**
     * module name
     */
    private String moduleName;
    /**
     * the program counter value for this instruction - for a jump instruction this is the jump pc
     */
    private long pc;
    /**
     * disassembly string
     */
    private String dissasembly;
    /**
     * type of the instruction
     */
    private InstructionType instructionType;
    /**
     * are there any input dependent conditionals?
     */
    private List<Pair<JumpInfo, Boolean>> conditionals;
    /**
     * example line for this instruction in the instrace
     */
    private int exampleLine;

    public StaticInfo getStaticInfo(List<StaticInfo> instruction, long programCounter) {
        for (StaticInfo staticInfo :
                instruction) {
            if (staticInfo.pc == programCounter)
                return  staticInfo;
        }
        return null;
    }

    public StaticInfo getStaticInfo(List<StaticInfo> instruction, JumpInfo jumpInfo) {
        for (StaticInfo staticInfo :
                instruction) {
            if (staticInfo.pc == jumpInfo.jump_pc)
                return  staticInfo;
        }
        return null;
    }

    public long getModule_no() {
        return module_no;
    }

    public void setModule_no(long module_no) {
        this.module_no = module_no;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public long getPc() {
        return pc;
    }

    public void setPc(long pc) {
        this.pc = pc;
    }

    public String getDissasembly() {
        return dissasembly;
    }

    public void setDissasembly(String dissasembly) {
        this.dissasembly = dissasembly;
    }

    public InstructionType getInstructionType() {
        return instructionType;
    }

    public void setInstructionType(InstructionType instructionType) {
        this.instructionType = instructionType;
    }

    public List<Pair<JumpInfo, Boolean>> getConditionals() {
        return conditionals;
    }

    public void setConditionals(List<Pair<JumpInfo, Boolean>> conditionals) {
        this.conditionals = conditionals;
    }

    public int getExampleLine() {
        return exampleLine;
    }

    public void setExampleLine(int exampleLine) {
        this.exampleLine = exampleLine;
    }

    public enum InstructionType {
        NONE, INPUT, INPUT_DEPENDENT_DIRECT, INPUT_DEPENDENT_INDIRECT, CONDITIONAL, LOOP, OUTPUT
    }

    public class JumpInfo {
        /**
         * the pc of the conditional jump
         */
        public long jump_pc; //
        /**
         *  eflags set by this pc
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
    }
}
