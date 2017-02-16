package lk.ac.mrt.projectx.buildex.models.memoryinfo;

import lk.ac.mrt.projectx.buildex.models.Pair;
import lk.ac.mrt.projectx.buildex.models.output.MemoryType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chathura Widanage
 */
public class MemoryInfo {

    List<Pair<Integer, Integer>> strideFrequency;
    private MemoryType type;  /*mem type*/
    private int direction; /* input / output */
    /* start and end instructions */
    private long start;
    private long end;
    /* stride */
    private long probStride;
    private List<MemoryInfo> mergedMemoryInfos;// if merged this would be filled
    private boolean paddingMerge; /* heuristic merging of padded regions with non-rectangular windows */
    private int order;

    public MemoryInfo() {
        this.strideFrequency = new ArrayList<>();
        this.mergedMemoryInfos = new ArrayList<>();
    }

    @Override
    public int hashCode() {
        int result = getType() != null ? getType().hashCode() : 0;
        result = 31 * result + getDirection();
        result = 31 * result + (int) (getStart() ^ (getStart() >>> 32));
        result = 31 * result + (int) (getEnd() ^ (getEnd() >>> 32));
        result = 31 * result + (int) (getProbStride() ^ (getProbStride() >>> 32));
        result = 31 * result + (getStrideFrequency() != null ? getStrideFrequency().hashCode() : 0);
        result = 31 * result + (getMergedMemoryInfos() != null ? getMergedMemoryInfos().hashCode() : 0);
        result = 31 * result + (isPaddingMerge() ? 1 : 0);
        result = 31 * result + getOrder();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MemoryInfo)) return false;

        MemoryInfo that = (MemoryInfo) o;

        if (getDirection() != that.getDirection()) return false;
        if (getStart() != that.getStart()) return false;
        if (getEnd() != that.getEnd()) return false;
        if (getProbStride() != that.getProbStride()) return false;
        if (isPaddingMerge() != that.isPaddingMerge()) return false;
        if (getOrder() != that.getOrder()) return false;
        if (getType() != that.getType()) return false;
        if (getStrideFrequency() != null ? !getStrideFrequency().equals( that.getStrideFrequency() ) : that.getStrideFrequency() != null)
            return false;
        return getMergedMemoryInfos() != null ? getMergedMemoryInfos().equals( that.getMergedMemoryInfos() ) : that.getMergedMemoryInfos() == null;
    }

    public MemoryType getType() {
        return type;
    }

    public void setType(MemoryType type) {
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

    public long getProbStride() {
        return probStride;
    }

    public void setProbStride(long probStride) {
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

    @Override
    public String toString() {
        return "MemoryInfo{" +
                "type=" + type +
                ", direction=" + direction +
                ", start=" + start +
                ", end=" + end +
                ", probStride=" + probStride +
                ", strideFrequency=" + strideFrequency +
                ", mergedMemoryInfos=" + mergedMemoryInfos +
                ", paddingMerge=" + paddingMerge +
                ", order=" + order +
                '}';
    }

    public long getNumberDimensions() {
        Long dim = 1L;
        MemoryInfo local_mem = this;
        while (local_mem.getMergedMemoryInfos().size() > 0) {
            dim++;
            local_mem = local_mem.getMergedMemoryInfos().get( 0 );
        }
        return dim;
    }
}
