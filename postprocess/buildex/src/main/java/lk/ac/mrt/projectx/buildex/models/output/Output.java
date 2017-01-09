package lk.ac.mrt.projectx.buildex.models.output;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chathura Widanage
 */
public class Output {
    private int opcode;
    private int numSrcs;
    private int num_Dsts;
    private List<Operand> srcs=new ArrayList<>();
    private List<Operand> dsts=new ArrayList<>();
    private int eflags;
    private int pc;

    public int getOpcode() {
        return opcode;
    }

    public void setOpcode(int opcode) {
        this.opcode = opcode;
    }

    public int getNumSrcs() {
        return numSrcs;
    }

    public void setNumSrcs(int numSrcs) {
        this.numSrcs = numSrcs;
    }

    public int getNum_Dsts() {
        return num_Dsts;
    }

    public void setNum_Dsts(int num_Dsts) {
        this.num_Dsts = num_Dsts;
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

    public int getEflags() {
        return eflags;
    }

    public void setEflags(int eflags) {
        this.eflags = eflags;
    }

    public int getPc() {
        return pc;
    }

    public void setPc(int pc) {
        this.pc = pc;
    }
}
