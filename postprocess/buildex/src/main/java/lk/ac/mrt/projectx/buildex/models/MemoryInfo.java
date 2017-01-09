package lk.ac.mrt.projectx.buildex.models;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chathura Widanage
 */
public class MemoryInfo {

    private int type;  /*mem type*/
    private int direction; /* input / output */

    /* start and end instructions */
    private long start;
    private long end;

    /* stride */
    private int probStride;
    List<Pair<Integer, Integer>> strideFrequency;

    private List<MemoryInfo> mergedMemoryInfos;// if merged this would be filled
    private boolean paddingMerge; /* heuristic merging of padded regions with non-rectangular windows */
    private int order;

    public MemoryInfo() {
        this.strideFrequency = new ArrayList<>();
        this.mergedMemoryInfos = new ArrayList<>();
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

    public List<Pair<Integer, Integer>> getStrideFrequency() {
        return strideFrequency;
    }

    public void setStrideFrequency(List<Pair<Integer, Integer>> strideFrequency) {
        this.strideFrequency = strideFrequency;
    }

    public List<MemoryInfo> getMergedMemoryInfos() {
        return mergedMemoryInfos;
    }

    public void setMergedMemoryInfos(List<MemoryInfo> mergedMemoryInfos) {
        this.mergedMemoryInfos = mergedMemoryInfos;
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
