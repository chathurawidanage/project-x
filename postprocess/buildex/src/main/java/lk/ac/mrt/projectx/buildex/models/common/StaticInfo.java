package lk.ac.mrt.projectx.buildex.models.common;

import lk.ac.mrt.projectx.buildex.models.Pair;

import java.util.ArrayList;
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

    public StaticInfo clone() {
        StaticInfo newInstance = new StaticInfo();
        newInstance.pc = pc;
        newInstance.module_no = module_no;
        newInstance.moduleName = moduleName;
        newInstance.dissasembly = dissasembly + "";
        newInstance.instructionType = instructionType;
        newInstance.exampleLine = exampleLine;
        newInstance.conditionals = new ArrayList<>();
        if(conditionals != null) {
            for(Pair<JumpInfo, Boolean> conditional : conditionals) {
                newInstance.conditionals.add( new Pair<JumpInfo, Boolean>( conditional.first.clone(), conditional
                        .second ) );
            }
        }
        return newInstance;
    }

    public enum InstructionType {
        NONE, INPUT, INPUT_DEPENDENT_DIRECT, INPUT_DEPENDENT_INDIRECT, CONDITIONAL, LOOP, OUTPUT
    }

}
