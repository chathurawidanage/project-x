package lk.ac.mrt.projectx.preprocess;

/**
 * Created by Lasantha on 11-Dec-16.
 */
public class StrideFrequencyPair {
    private int stride;
    private int frequency;

    public StrideFrequencyPair(int stride, int frequency) {
        this.stride = stride;
        this.frequency = frequency;
    }

    public int getStride() {
        return stride;
    }

    public void setStride(int stride) {
        this.stride = stride;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
}
