package lk.ac.mrt.projectx.buildex.models.output;

import lk.ac.mrt.projectx.buildex.x86.X86Analysis.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chathura Widanage
 */
public class Output {
    private Operation opcode;
    private int numOfSources;
    private int numOfDestinations;
    private List<Operand> srcs=new ArrayList<>();
    private List<Operand> dsts=new ArrayList<>();
    private long eflags;
    private long pc;

    public Operation getOpcode() {
        return opcode;
    }

    public void setOpcode(Operation opcode) {
        this.opcode = opcode;
    }

    public void setOpcode(String opcode) {
        Operation op = Operation.valueOf(opcode);
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
