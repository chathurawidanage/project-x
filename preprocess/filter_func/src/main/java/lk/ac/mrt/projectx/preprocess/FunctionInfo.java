package lk.ac.mrt.projectx.preprocess;

import java.util.ArrayList;

/**
 * Created by Lasantha on 04-Dec-16.
 */
public class FunctionInfo {

    private long startAddress;
    private long endAddress;
    private int frequency;
    private String moduleName;
    private long moduleAddress;
    private ArrayList<BasicBlockInfo> basicBlocks;

    public FunctionInfo() {
        this.basicBlocks = new ArrayList<>();
    }

    public long getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(long startAddress) {
        this.startAddress = startAddress;
    }

    public long getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(long endAddress) {
        this.endAddress = endAddress;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public long getModuleAddress() {
        return moduleAddress;
    }

    public void setModuleAddress(long moduleAddress) {
        this.moduleAddress = moduleAddress;
    }

    public ArrayList<BasicBlockInfo> getBasicBlocks() {
        return basicBlocks;
    }

    public void setBasicBlocks(ArrayList<BasicBlockInfo> basicBlocks) {
        this.basicBlocks = basicBlocks;
    }
}
