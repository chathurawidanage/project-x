package lk.ac.mrt.projectx.buildex.complex.operations;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * @author Chathura Widanage
 */
public class Statistics {
    private DescriptiveStatistics statistics = new DescriptiveStatistics();
    private Operation operation;

    private boolean changed = true;

    private int low, heigh;

    public int getLow() {
        if (changed) {
            setChanged(false);
            low = (int) (statistics.getPercentile(25) * 1000d);//todo make 1000 a parameter
        }
        return low;
    }

    public int getHigh() {
        if (changed) {
            setChanged(false);
            heigh = (int) (statistics.getPercentile(75) * 1000d);
        }
        return heigh;
    }

    public int getTotalIterations() {
        return this.getHigh() - this.getLow();
    }

    private void setChanged(boolean changed) {
        this.changed = changed;
    }

    public Statistics(Operation operation) {
        this.operation = operation;
    }

    public Operation getOperation() {
        return operation;
    }

    public void addValue(double val) {
        setChanged(true);
        statistics.addValue(val);
    }

    @Override
    public String toString() {
        return String.format("Operation : %s [%d - %d] : %d", operation.getName(), this.getLow(), this.getHigh(), this.getTotalIterations());
    }
}
