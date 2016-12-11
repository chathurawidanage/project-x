package lk.ac.mrt.projectx.preprocess;

import java.util.ArrayList;

/**
 * Created by Lasantha on 11-Dec-16.
 */
public class MemoryInfo {
    private int type;
    private int direction;  // input , output
    /* start and end instructions */
    private long start;
    private long end;

    /* stride */
    private int probStride;
    private ArrayList<StrideFrequencyPair> strideFreqs;

    private ArrayList<MemoryInfo> memoryInfos;  // if merged this would be filled
    private boolean paddingMerge; /* heuristic merging of padded regions with non-rectangular windows */
    private int order;

    public MemoryInfo() {
        strideFreqs = new ArrayList<>();
        memoryInfos = new ArrayList<>();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public int getProbStride() {
        return probStride;
    }

    public void setProbStride(int probStride) {
        this.probStride = probStride;
    }

    public ArrayList<StrideFrequencyPair> getStrideFreqs() {
        return strideFreqs;
    }

    public void setStrideFreqs(ArrayList<StrideFrequencyPair> strideFreqs) {
        this.strideFreqs = strideFreqs;
    }

    public ArrayList<MemoryInfo> getMemoryInfos() {
        return memoryInfos;
    }

    public void setMemoryInfos(ArrayList<MemoryInfo> memoryInfos) {
        this.memoryInfos = memoryInfos;
    }

    public boolean isPaddingMerge() {
        return paddingMerge;
    }

    public void setPaddingMerge(boolean paddingMerge) {
        this.paddingMerge = paddingMerge;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
