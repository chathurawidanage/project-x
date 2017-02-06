package lk.ac.mrt.projectx.buildex.complex;

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

/**
 * @author Chathura Widanage
 */
public class ComplexSynthesizer {
    public double[] synthesize(double[] y, double x[][],boolean noIntercept) {
        OLSMultipleLinearRegression olsMultipleLinearRegression = new OLSMultipleLinearRegression();
        olsMultipleLinearRegression.setNoIntercept(noIntercept);
        olsMultipleLinearRegression.newSampleData(y, x);
        double[] doubles = olsMultipleLinearRegression.estimateRegressionParameters();
        return doubles;
    }

    public double[] synthesizeBruteForce(double[] y,double x[][]){
        return new double[]{};
    }


}
