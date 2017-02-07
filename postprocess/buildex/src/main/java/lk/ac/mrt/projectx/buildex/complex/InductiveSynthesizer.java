package lk.ac.mrt.projectx.buildex.complex;

import lk.ac.mrt.projectx.buildex.complex.cordinates.CartesianCoordinate;
import lk.ac.mrt.projectx.buildex.complex.cordinates.PolarCoordinate;
import lk.ac.mrt.projectx.buildex.models.Pair;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.image.BufferedImage;
import java.util.*;

/**
 * @author Chathura Widanage
 */
public class InductiveSynthesizer {
    private final static Logger logger = LogManager.getLogger(InductiveSynthesizer.class);

    public void solve(List<Pair<CartesianCoordinate, CartesianCoordinate>> examples, BufferedImage imageIn, BufferedImage imageOut) {
        logger.debug("Synthesizing using {} examples", examples.size());
        int widthIn = imageIn.getWidth();
        int heightIn = imageIn.getHeight();
        int widthOut = imageOut.getWidth();
        int heightOut = imageOut.getHeight();

        /*Filtering good pairs*/
        List<Pair<CartesianCoordinate, CartesianCoordinate>> goodExamples = new ArrayList<>();
        //Map<Integer, List<Pair<CartesianCoordinate, CartesianCoordinate>>> rExamples = new HashMap<>();
        for (Pair<CartesianCoordinate, CartesianCoordinate> e : examples) {
           /* PolarCoordinate polarCoordinate = CoordinateTransformer.cartesian2Polar(e.first);
            int r = (int) Math.round(polarCoordinate.getR());
            List<Pair<CartesianCoordinate, CartesianCoordinate>> pairs = rExamples.get(r);
            if (pairs == null) {
                pairs = new ArrayList<>();
                rExamples.put(r, pairs);
            }
            pairs.add(e);*/

            if (e.second.getX() == widthOut - 1 || e.second.getX() == 0
                    || e.second.getY() == heightOut - 1 || e.second.getY() == 0) {//to get rid of errors due to clamping
                //System.out.println("drop");
            } else {

                goodExamples.add(e);
            }
        }

        //logger.debug(rExamples.keySet());

        logger.debug("Dropped {} example pairs", examples.size() - goodExamples.size());
        examples = goodExamples;

        boolean variableCombinationR[] = {true, true, false, false, true};//R,T,R2,T2,RT,C
        boolean constantR = false;

        boolean variableCombinationT[] = {true, true, false, false, true};//R,T,R2,T2,RT,C
        boolean constantT = false;

        int variablesCountR = 0;
        for (boolean x : variableCombinationR) {
            if (x) {
                variablesCountR++;
            }
        }


        int variablesCountT = 0;
        for (boolean x : variableCombinationT) {
            if (x) {
                variablesCountT++;
            }
        }


        DescriptiveStatistics rComponentRCoefficient = new DescriptiveStatistics();
        DescriptiveStatistics rComponentTCoefficient = new DescriptiveStatistics();
        DescriptiveStatistics rComponentR2Coefficient = new DescriptiveStatistics();
        DescriptiveStatistics rComponentT2Coefficient = new DescriptiveStatistics();
        DescriptiveStatistics rComponentRTCoefficient = new DescriptiveStatistics();
        DescriptiveStatistics rComponentConstCoefficient = new DescriptiveStatistics();

        DescriptiveStatistics tComponentRCoefficient = new DescriptiveStatistics();
        DescriptiveStatistics tComponentTCoefficient = new DescriptiveStatistics();
        DescriptiveStatistics tComponentR2Coefficient = new DescriptiveStatistics();
        DescriptiveStatistics tComponentT2Coefficient = new DescriptiveStatistics();
        DescriptiveStatistics tComponentRTCoefficient = new DescriptiveStatistics();
        DescriptiveStatistics tComponentConstCoefficient = new DescriptiveStatistics();

        int window = widthIn*4;//(int) Math.sqrt(width * height);
        Iterator<Pair<CartesianCoordinate, CartesianCoordinate>> rIterator = examples.iterator();
        for (int i = 0; i < examples.size() - window; i += window) {
            Pair<CartesianCoordinate, CartesianCoordinate> r = rIterator.next();
            int size = window;
            double xR[][] = new double[size][variablesCountR];
            double yR[] = new double[size];

            double xT[][] = new double[size][3];
            double yT[] = new double[size];
            for (int j = i; j < i + size; j++) {
                Pair<CartesianCoordinate, CartesianCoordinate> p = examples.get(j);

                PolarCoordinate polarCoordinateS = CoordinateTransformer.cartesian2Polar(widthIn, heightIn, p.first, false);
                PolarCoordinate polarCoordinateD = CoordinateTransformer.cartesian2Polar(widthOut, heightOut, p.second, false);

                /*Operations to R*/
                double rVarsVale[] = new double[variableCombinationR.length];
                rVarsVale[0] = polarCoordinateS.getR();
                rVarsVale[1] = MathUtils.normalizeAngle(polarCoordinateS.getTheta(), FastMath.PI);
                rVarsVale[2] = Math.pow(polarCoordinateS.getR(), 2);
                rVarsVale[3] = Math.pow(polarCoordinateS.getTheta(), 2);
                rVarsVale[4] = polarCoordinateS.getR() * MathUtils.normalizeAngle(polarCoordinateS.getTheta(), FastMath.PI);
//                xR[j - i][4] = polarCoordinateS.getR() * MathUtils.normalizeAngle(polarCoordinateS.getTheta(), FastMath.PI);
                int pivot = 0;
                for (int comb = 0; comb < variableCombinationR.length; comb++) {
                    if (variableCombinationR[comb]) {
                        xR[j - i][pivot++] = rVarsVale[comb];
                    }
                }

                yR[j - i] = polarCoordinateD.getR();


                /*Operations to T*/
                xT[j - i][0] = polarCoordinateS.getR();
                xT[j - i][1] = polarCoordinateS.getTheta();
                xT[j - i][2] = Math.PI;//to avoid the error due to normalizing angle to 360deg
                yT[j - i] = polarCoordinateD.getTheta();
                //   System.out.println(polarCoordinateD.getTheta()+":"+(polarCoordinateS.getR()+polarCoordinateS.getTheta()));
            }
            try {
                ComplexSynthesizer complexSynthesizer = new ComplexSynthesizer();
                boolean noRIntercept = !constantR;

                double[] synthesizeR = complexSynthesizer.synthesize(yR, xR, noRIntercept);
                double rVarCoefs[] = new double[variableCombinationR.length + 1];//+1 for constant
                int pivot = 0;
                for (int l = 0; l < variableCombinationR.length; l++) {
                    if (variableCombinationR[l]) {
                        rVarCoefs[l] = synthesizeR[pivot++];
                    } else {
                        rVarCoefs[l] = 0;
                    }
                }
                if (constantR) {
                    rVarCoefs[rVarCoefs.length - 1] = synthesizeR[pivot];
                }
                int rIndex = 0;
                rComponentRCoefficient.addValue(rVarCoefs[rIndex++]);
                rComponentTCoefficient.addValue(rVarCoefs[rIndex++]);
                rComponentR2Coefficient.addValue(rVarCoefs[rIndex++]);
                rComponentT2Coefficient.addValue(rVarCoefs[rIndex++]);
                rComponentRTCoefficient.addValue(rVarCoefs[rIndex++]);
                rComponentConstCoefficient.addValue(rVarCoefs[rIndex++]);
                //System.out.println(round(synthesizeR[2])+","+synthesizeR[3]);


                double[] synthesizeT = complexSynthesizer.synthesize(yT, xT, true);
                double normalizedValue = synthesizeT[2];
                for (int k = 0; k < yT.length; k++) {
                    yT[k] -= normalizedValue;
                }
                synthesizeT = complexSynthesizer.synthesize(yT, xT, true);
                tComponentRCoefficient.addValue(synthesizeT[0]);
                tComponentTCoefficient.addValue(synthesizeT[1]);
                //dsT3.addValue(round());
            } catch (Exception e) {
                logger.error(e.getMessage());//no need to print stack trace
            }
        }


        int loweP = 25;
        int higherP = 75;

        //System.out.println(rComponentRCoefficient.getValues());

        LoopBounds loopBoundsR = new LoopBounds();
        loopBoundsR.r.low = loopBound(rComponentRCoefficient.getPercentile(loweP));
        loopBoundsR.r.high = loopBound(rComponentRCoefficient.getPercentile(higherP));
        logger.debug("R->r : {},{},{}", rComponentRCoefficient.getPercentile(loweP),
                rComponentRCoefficient.getPercentile(50), rComponentRCoefficient.getPercentile(higherP));

        loopBoundsR.t.low = loopBound(rComponentTCoefficient.getPercentile(loweP));
        loopBoundsR.t.high = loopBound(rComponentTCoefficient.getPercentile(higherP));
        logger.debug("R->t : {},{},{}", rComponentTCoefficient.getPercentile(loweP),
                rComponentTCoefficient.getPercentile(50), rComponentTCoefficient.getPercentile(higherP));

        loopBoundsR.r2.low = loopBound(rComponentR2Coefficient.getPercentile(loweP));
        loopBoundsR.r2.high = loopBound(rComponentR2Coefficient.getPercentile(higherP));
        logger.debug("R->r2 : {},{},{}", rComponentR2Coefficient.getPercentile(loweP),
                rComponentR2Coefficient.getPercentile(50), rComponentR2Coefficient.getPercentile(higherP));

        loopBoundsR.rt.low = loopBound(rComponentRTCoefficient.getPercentile(loweP));
        loopBoundsR.rt.high = loopBound(rComponentRTCoefficient.getPercentile(higherP));
        logger.debug("R->rt : {},{},{}", rComponentRTCoefficient.getPercentile(loweP),
                rComponentRTCoefficient.getPercentile(50), rComponentRTCoefficient.getPercentile(higherP));

        loopBoundsR.c.low = loopBound(rComponentConstCoefficient.getPercentile(loweP));
        loopBoundsR.c.high = loopBound(rComponentConstCoefficient.getPercentile(higherP));
        logger.debug("R->const : {},{},{}", rComponentConstCoefficient.getPercentile(loweP),
                rComponentConstCoefficient.getPercentile(50), rComponentConstCoefficient.getPercentile(higherP));

        loopBoundsR.t2.low = loopBound(rComponentT2Coefficient.getPercentile(loweP));
        loopBoundsR.t2.high = loopBound(rComponentT2Coefficient.getPercentile(higherP));
        logger.debug("R->t2 : {},{},{}", rComponentT2Coefficient.getPercentile(loweP),
                rComponentT2Coefficient.getPercentile(50), rComponentT2Coefficient.getPercentile(higherP));


        logger.debug("T1 : {},{},{}", tComponentRCoefficient.getPercentile(loweP),
                tComponentRCoefficient.getPercentile(50), tComponentRCoefficient.getPercentile(higherP));

        logger.debug("T2 : {},{},{}", tComponentTCoefficient.getPercentile(loweP),
                tComponentTCoefficient.getPercentile(50), tComponentTCoefficient.getPercentile(higherP));

        int r1RcoefLow = (int) (rComponentRCoefficient.getPercentile(loweP) * 1000);
        int r1RcoefHigh = (int) (rComponentRCoefficient.getPercentile(higherP) * 1000);

        int r1TcoefLow = (int) (rComponentTCoefficient.getPercentile(loweP) * 1000);
        int r1TcoefHigh = (int) (rComponentTCoefficient.getPercentile(higherP) * 1000);


        long iterations = (r1RcoefHigh - r1RcoefLow) * (r1TcoefHigh - r1TcoefLow);
        logger.debug("iterations : {}", loopBoundsR.getIterations());

        logger.debug("Loop bounds R {}", loopBoundsR);
        Guesses approximate = Approximator.approximate(loopBoundsR, examples, true, widthIn, heightIn);
        logger.debug("R : {}", approximate);


       /* r1RcoefLow = (int) (tComponentRCoefficient.getPercentile(loweP) * 1000);
        r1RcoefHigh = (int) (tComponentRCoefficient.getPercentile(higherP) * 1000);

        r1TcoefLow = (int) (dsT2.getPercentile(loweP) * 1000);
        r1TcoefHigh = (int) (dsT2.getPercentile(higherP) * 1000);
        iterations = (r1RcoefHigh - r1RcoefLow) * (r1TcoefHigh - r1TcoefLow);
        logger.debug("iterations {}", iterations);
        approximate = Approximator.approximate(r1RcoefLow, r1RcoefHigh, r1TcoefLow, r1TcoefHigh, examples, false, widthIn, heightIn);
        logger.info("T : {}", approximate);*/

    }

    private int loopBound(double value) {
        return (int) (value * 1000);
    }

    private double round(double value) {
        return (double) Math.round(value * 1000d) / 1000d;
    }
}

