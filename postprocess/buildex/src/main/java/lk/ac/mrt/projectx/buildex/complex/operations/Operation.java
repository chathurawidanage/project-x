package lk.ac.mrt.projectx.buildex.complex.operations;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * @author Chathura Widanage
 */
public abstract class Operation {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Operation(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public abstract double operate(double r, double theta);
}
