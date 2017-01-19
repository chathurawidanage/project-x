package lk.ac.mrt.projectx.buildex.models.output;

import lk.ac.mrt.projectx.buildex.DefinesDotH.*;
import lk.ac.mrt.projectx.buildex.x86.X86Analysis.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chathura Widanage
 */
public class Output {
    private OpCodes opcode;
    private int numOfSources;
    private int numOfDestinations;
    private List<Operand> srcs = new ArrayList<>();
    private List<Operand> dsts = new ArrayList<>();
    private long eflags;
    private long pc;

    public OpCodes getOpcode() {
        return opcode;
    }

    public void setOpcode(OpCodes opcode) {
        this.opcode = opcode;
    }

    public void setOpcode(Integer opcode) {
        this.opcode = OpCodes.values()[ opcode ];
    }

    public void setOpcode(String opcode) {
        OpCodes op = OpCodes.values()[ Integer.parseInt( opcode ) ];
        this.opcode = op;
    }

    public int getNumOfSources() {
        return numOfSources;
    }

    public void setNumOfSources(int numOfSources) {
        this.numOfSources = numOfSources;
    }

    public int getNumOfDestinations() {
        return numOfDestinations;
    }

    public void setNumOfDestinations(int numOfDestinations) {
        this.numOfDestinations = numOfDestinations;
    }

    public List<Operand> getSrcs() {
        return srcs;
    }

    public void setSrcs(List<Operand> srcs) {
        this.srcs = srcs;
    }

    public List<Operand> getDsts() {
        return dsts;
    }

    public void setDsts(List<Operand> dsts) {
        this.dsts = dsts;
    }

    public long getEflags() {
        return eflags;
    }

    public void setEflags(long eflags) {
        this.eflags = eflags;
    }

    public long getPc() {
        return pc;
    }

    public void setPc(long pc) {
        this.pc = pc;
    }
}
