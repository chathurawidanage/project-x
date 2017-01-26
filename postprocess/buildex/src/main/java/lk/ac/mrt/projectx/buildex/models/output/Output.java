package lk.ac.mrt.projectx.buildex.models.output;

import lk.ac.mrt.projectx.buildex.DefinesDotH.OpCodes;

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

    public void setOpcode(String opcode) {
        OpCodes op = OpCodes.values()[Integer.parseInt(opcode)];
        this.opcode = op;
    }

    public void setOpcode(OpCodes opcode) {
        this.opcode = opcode;
    }

    public void setOpcode(Integer opcode) {
        this.opcode = OpCodes.values()[opcode];
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

    public boolean isBounds(int d, int s) {
        return ((this.getNumOfDestinations() == d) && (this.getNumOfSources() == s));
    }

    public int getNumOfSources() {
        //return numOfSources;
        return srcs.size();
    }

    public void setNumOfSources(int numOfSources) {
        this.numOfSources = numOfSources;
    }

    public int getNumOfDestinations() {
        return dsts.size();
    }

    public void setNumOfDestinations(int numOfDestinations) {
        this.numOfDestinations = numOfDestinations;
    }

    public void updateFPReg(String disams, int line) {
        updateFPDest(disams, line);
        updateFPSrc(disams, line);
    }

    public void updateFPDest(String disams, int line) {
        for (Operand op : dsts) {
            if (op.isFloatingPointReg()) {
                op.updateFloatingPointReg(disams, line);
            }
        }
    }

    public void updateFPSrc(String disams, int line) {
        for (Operand op : srcs) {
            if (op.isFloatingPointReg()) {
                op.updateFloatingPointReg(disams, line);
            }
        }
    }
}
