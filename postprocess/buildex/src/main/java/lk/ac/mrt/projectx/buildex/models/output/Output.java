package lk.ac.mrt.projectx.buildex.models.output;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chathura Widanage
 */
public class Output {
    private int opcode;
    private int numOfSources;
    private int numOfDestinations;
    private List<Operand> srcs=new ArrayList<>();
    private List<Operand> dsts=new ArrayList<>();
    private long eflags;
    private long pc;

    public int getOpcode() {
        return opcode;
    }

    public void setOpcode(int opcode) {
        this.opcode = opcode;
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
