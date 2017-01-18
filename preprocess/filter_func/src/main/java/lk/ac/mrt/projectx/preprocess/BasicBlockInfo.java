package lk.ac.mrt.projectx.preprocess;

import java.util.ArrayList;

/**
 * Created by Lasantha on 04-Dec-16.
 */
public class BasicBlockInfo {

    private long startAddress; // offset from the start of the module
    private int size;
    private int frequency;  // number of times it gets executed
    private boolean isRet;
    private boolean isCall;
    private boolean isCallTarget;
    private long functionAddress;  // we are also keeping backward information - specially when global function information is missing

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

    public long getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(long startAddress) {
        this.startAddress = startAddress;
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

    public long getFunctionAddress() {
        return functionAddress;
    }

    public void setFunctionAddress(long functionAddress) {
        this.functionAddress = functionAddress;
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

    /* find the bb having the addr from a module
     * addr - any valid app_pc
     */
    public static BasicBlockInfo findBasicBlock(ModuleInfo module, long address) {

        for (int i = 0; i < module.getFunctions().size(); i++) {
            FunctionInfo func = module.getFunctions().get(i);
            for (int j = 0; j < func.getBasicBlocks().size(); j++) {
                BasicBlockInfo bb = func.getBasicBlocks().get(j);
                if ((bb.getStartAddress() <= address) && (address < (bb.getStartAddress() + bb.getSize()))) {
                    return bb;
                }

            }
        }
        return null;

    }
}
