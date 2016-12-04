package lk.ac.mrt.projectx.preprocess;

import java.util.ArrayList;

/**
 * Created by Lasantha on 04-Dec-16.
 */
public class BasicBlockInfo {

    private int start_addr; // offset from the start of the module
    private int size;
    private int frequency;  // number of times it gets executed
    private boolean isRet;
    private boolean isCall;
    private boolean isCallTarget;
    private int func_addr;  // we are also keeping backward information - specially when global function information is missing

    private ArrayList<TargetInfo> fromBasicBlocks;  // bbs from which this bb was reached
    private ArrayList<TargetInfo> toBasicBlocks;    // to which basic blocks this bb connects to
    private ArrayList<TargetInfo> callees;
    private ArrayList<TargetInfo> callers;

    public BasicBlockInfo() {
        this.fromBasicBlocks = new ArrayList<>();
        this.toBasicBlocks = new ArrayList<>();
        this.callees = new ArrayList<>();
        this.callers = new ArrayList<>();
    }

    public int getStart_addr() {
        return start_addr;
    }

    public void setStart_addr(int start_addr) {
        this.start_addr = start_addr;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public boolean isRet() {
        return isRet;
    }

    public void setRet(boolean ret) {
        isRet = ret;
    }

    public boolean isCall() {
        return isCall;
    }

    public void setCall(boolean call) {
        isCall = call;
    }

    public boolean isCallTarget() {
        return isCallTarget;
    }

    public void setCallTarget(boolean callTarget) {
        isCallTarget = callTarget;
    }

    public int getFunc_addr() {
        return func_addr;
    }

    public void setFunc_addr(int func_addr) {
        this.func_addr = func_addr;
    }

    public ArrayList<TargetInfo> getFromBasicBlocks() {
        return fromBasicBlocks;
    }

    public void setFromBasicBlocks(ArrayList<TargetInfo> fromBasicBlocks) {
        this.fromBasicBlocks = fromBasicBlocks;
    }

    public ArrayList<TargetInfo> getToBasicBlocks() {
        return toBasicBlocks;
    }

    public void setToBasicBlocks(ArrayList<TargetInfo> toBasicBlocks) {
        this.toBasicBlocks = toBasicBlocks;
    }

    public ArrayList<TargetInfo> getCallees() {
        return callees;
    }

    public void setCallees(ArrayList<TargetInfo> callees) {
        this.callees = callees;
    }

    public ArrayList<TargetInfo> getCallers() {
        return callers;
    }

    public void setCallers(ArrayList<TargetInfo> callers) {
        this.callers = callers;
    }
}
