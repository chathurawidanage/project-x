package lk.ac.mrt.projectx.buildex.complex;

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

/**
 * @author Chathura Widanage
 */
public class ComplexSynthesizer {
    public double[] synthesize(double[] y, double x[][], boolean noIntercept) {
        OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
        regression.setNoIntercept(noIntercept);
        regression.newSampleData(y, x);
        double[] doubles = regression.estimateRegressionParameters();
        if (!noIntercept) {//make intercept the last of the array, in default it is the first
            double intercept = doubles[doubles.length - 1];
            for (int i = 0; i < doubles.length - 1; i++) {
                doubles[i] = doubles[i + 1];
            }
            doubles[doubles.length - 1] = intercept;
        }
        return doubles;
    }

    public double[] synthesizeBruteForce(double[] y, double x[][]) {
        return new double[]{};
    }


}
