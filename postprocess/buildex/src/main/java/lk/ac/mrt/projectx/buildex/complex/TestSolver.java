package lk.ac.mrt.projectx.buildex.complex;

import lk.ac.mrt.projectx.buildex.complex.cordinates.CartesianCoordinate;
import lk.ac.mrt.projectx.buildex.complex.cordinates.PolarCoordinate;
import lk.ac.mrt.projectx.buildex.complex.generators.TwirlGenerator;
import lk.ac.mrt.projectx.buildex.complex.generators.WaveGenerator;
import lk.ac.mrt.projectx.buildex.models.Pair;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Chathura Widanage
 */
public class TestSolver {
    private final static Logger logger = LogManager.getLogger(TestSolver.class);

    public static void main(String[] args) {
        BufferedImage inImg = new BufferedImage(128, 128, BufferedImage.TYPE_3BYTE_BGR);
        BufferedImage outImg = new BufferedImage(128, 128, BufferedImage.TYPE_3BYTE_BGR);
        InductiveSynthesizer inductiveSynthesizer = new InductiveSynthesizer();
//        inductiveSynthesizer.solve(new TwirlGenerator().generate(128, 128), inImg, outImg);
        long startT = System.currentTimeMillis();
        inductiveSynthesizer.solve(new TwirlGenerator().generate(128, 128), inImg, outImg);
        logger.debug("Synthesized in {}ms", System.currentTimeMillis() - startT);
        if (true)
            return;






        /*double matL[]={8,9,10};
        double matR[][]={{7,8,9},{9,9,8},{1,1,1}};
        RealMatrix realMatrixL= MatrixUtils.createColumnRealMatrix(matL);
        RealMatrix realMatrixR= MatrixUtils.createRealMatrix(matR);

        RealMatrix realMatrixRInv=new LUDecomposition(realMatrixR).getSolver().getInverse();
        RealMatrix multiply = realMatrixRInv.multiply(realMatrixL);
        System.out.println(multiply.toString());*/
        /*Twirl dummy*/
        int width = 128;
        int height = 128;
        double x0 = 0.5 * (width - 1);
        double y0 = 0.5 * (height - 1);

/*
        PolynomialCurveFitter polynomialCurveFitterR = PolynomialCurveFitter.create(10);
        PolynomialCurveFitter polynomialCurveFitterT = PolynomialCurveFitter.create(5);
        WeightedObservedPoints weightedObservedPointsR = new WeightedObservedPoints();
        WeightedObservedPoints weightedObservedPointsTheta = new WeightedObservedPoints();*/

        // HashMap<Integer, List<Pair<CartesianCoordinate, CartesianCoordinate>>> map = new HashMap<>();

        List<Pair<CartesianCoordinate, CartesianCoordinate>> pairs = new ArrayList<>();
        // swirl
        /*for (int sx = 0; sx < width; sx++) {
            for (int sy = 0; sy < height; sy++) {
                double dx = sx - x0;
                double dy = sy - y0;
                double r = Math.sqrt(dx * dx + dy * dy);
                double angle = Math.PI / 256 * r;
                int tx = (int) (+dx * Math.cos(angle) - dy * Math.sin(angle) + x0);
                int ty = (int) (+dx * Math.sin(angle) + dy * Math.cos(angle) + y0);

                CartesianCoordinate source = new CartesianCoordinate(sx, sy);
                CartesianCoordinate dest = new CartesianCoordinate(tx, ty);

                List<Pair<CartesianCoordinate, CartesianCoordinate>> pairs = map.get((int) r);
                if (pairs == null) {
                    pairs = new ArrayList<>();
                    map.put((int) r, pairs);
                }
                pairs.add(new Pair<>(source, dest));

                *//*PolarCoordinate sourceP = CoordinateTransformer.cartesian2Polar(source);
                PolarCoordinate destP = CoordinateTransformer.cartesian2Polar(dest);

                weightedObservedPointsR.add(sourceP.getR(), destP.getR());*//*
                //weightedObservedPointsTheta.add(source.getX(), dest.getX());

                //ins[sx][sy]=new double[]{sx,sy,1};
                //System.out.println(sx+","+sy+":"+tx+","+ty);
            }
        }*/

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                CartesianCoordinate cartesianCoordinate = new CartesianCoordinate(i, j);
                PolarCoordinate polarCoordinate = CoordinateTransformer.cartesian2Polar(width, height, cartesianCoordinate);
/* TWIRL POLAR
                double thetaNew = polarCoordinate.getTheta() + (polarCoordinate.getR()/10);
                thetaNew = MathUtils.normalizeAngle(thetaNew, FastMath.PI);


                double rNew = (polarCoordinate.getR());
                PolarCoordinate newPola = new PolarCoordinate(thetaNew, rNew);

                CartesianCoordinate newCartCord = CoordinateTransformer.polar2Cartesian(width, height, newPola);
                CartesianCoordinate clamped = clamp(width, height, newCartCord);
                if(clamped.getX()==0 || clamped.getY()==0 || clamped.getX()==width-1 || clamped.getY()==height-1){
                    System.out.println("clamped"+clamped+" , "+newCartCord);
                }

                System.out.println(thetaNew + ":" + MathUtils.normalizeAngle(CoordinateTransformer.cartesian2Polar(width, height, newCartCord).getTheta(),FastMath.PI));

                pairs.add(new Pair<>(cartesianCoordinate, clamped));*/
                CartesianCoordinate out = new CartesianCoordinate(0, 0);

                /* TWIRL CART
                double dx = i - x0;
                double dy = j - y0;
                double r = Math.sqrt(dx * dx + dy * dy);
                double angle = Math.PI / 64 * r;
                int tx = (int) (+dx * Math.cos(angle) - dy * Math.sin(angle) + x0);
                int ty = (int) (+dx * Math.sin(angle) + dy * Math.cos(angle) + y0);

                // plot pixel (sx, sy) the same color as (tx, ty) if it's in bounds
                if (tx >= 0 && tx < width && ty >= 0 && ty < height) {
                    out.setX(tx);
                    out.setY(ty);
                }
                */
                /*Wave Cartesian*/
                int xx = i;
                int yy = (int) (j + 20 * Math.sin(i * 2 * Math.PI / 64));
                if (yy >= 0 && yy < height) {
                    out.setX(xx);
                    out.setY(yy);
                }
                pairs.add(new Pair<>(cartesianCoordinate, out));
            }

        }
        System.out.println("---------");

        //System.out.println(map.toString());

 /*       double[] fitR = polynomialCurveFitterR.fit(weightedObservedPointsR.toList());
        //double[] fitT = polynomialCurveFitterT.fit(weightedObservedPointsTheta.toList());


        PolynomialFunction polynomialFunctionR = new PolynomialFunction(fitR);
        //PolynomialFunction polynomialFunctionTheta = new PolynomialFunction(fitT);

        for (int sx = 0; sx < width; sx++) {
            for (int sy = 0; sy < height; sy++) {
                double dx = sx - x0;
                double dy = sy - y0;
                double r = Math.sqrt(dx * dx + dy * dy);
                double angle = Math.PI / 256 * r;
                int tx = (int) (+dx * Math.cos(angle) - dy * Math.sin(angle) + x0);
                int ty = (int) (+dx * Math.sin(angle) + dy * Math.cos(angle) + y0);

                CartesianCoordinate source = new CartesianCoordinate(sx, sy);
                CartesianCoordinate dest = new CartesianCoordinate(tx, ty);

                PolarCoordinate sourceP = CoordinateTransformer.cartesian2Polar(source);
                PolarCoordinate destP = CoordinateTransformer.cartesian2Polar(dest);

                double calcR = polynomialFunctionR.value(sourceP.getR());
                double calcT = 0;

                System.out.println((int)destP.getR()+","+(int)calcR);


                //ins[sx][sy]=new double[]{sx,sy,1};
                //System.out.println(sx+","+sy+":"+tx+","+ty);
            }
        }*/


        //3,45:33,7
        //23,26:55,8
        //58,64:57,63

        //94,116:50,122
        //111,59:105,85
        //123,77:97,113

        /*CartesianCoordinate ci1=new CartesianCoordinate(3,45);
        CartesianCoordinate ci2=new CartesianCoordinate(23,26);
        CartesianCoordinate ci3=new CartesianCoordinate(58,64);

        CartesianCoordinate co1=new CartesianCoordinate(33,7);
        CartesianCoordinate co2=new CartesianCoordinate(55,8);
        CartesianCoordinate co3=new CartesianCoordinate(57,63);

        PolarCoordinate pi1=CoordinateTransformer.cartesian2Polar(ci1);
        PolarCoordinate pi2=CoordinateTransformer.cartesian2Polar(ci2);
        PolarCoordinate pi3=CoordinateTransformer.cartesian2Polar(ci3);

        PolarCoordinate po1=CoordinateTransformer.cartesian2Polar(co1);
        PolarCoordinate po2=CoordinateTransformer.cartesian2Polar(co2);
        PolarCoordinate po3=CoordinateTransformer.cartesian2Polar(co3);



        RealMatrix coef=new Array2DRowRealMatrix(new double[][]{{pi1.getR(),pi1.getTheta(),1},
                {pi2.getR(),pi2.getTheta(),1},
                {pi3.getR(),pi3.getTheta(),1}});
        DecompositionSolver decompositionSolver=new LUDecomposition(coef).getSolver();

        RealVector constants=new ArrayRealVector(new double[]{po1.getR(),po2.getR(),po3.getR()});
        RealVector solve = decompositionSolver.solve(constants);
        System.out.println(solve);

        coef=new Array2DRowRealMatrix(new double[][]{{127,127,1},{85,112,1},{32,51,1}});
        decompositionSolver=new LUDecomposition(coef).getSolver();

        constants=new ArrayRealVector(new double[]{35,51,39});
        solve = decompositionSolver.solve(constants);
        System.out.println(solve);*/

       /* List<Double> r1=new ArrayList<>();
        List<Double> r2=new ArrayList<>();
        List<Double> r3=new ArrayList<>();*/




        /*List<Guesses> votes = new ArrayList<>();
        for (int i = r1RcoefLow; i <= r1RcoefHigh; i++) {
            double rcof = i * 1.0 / 1000d;
            for (int j = r1TcoefLow; j <= r1TcoefHigh; j++) {
                double tcof = j * 1.0 / 1000d;
                Guesses guesses = new Guesses();
                guesses.setRcof(rcof);
                guesses.setTcof(tcof);
                votes.add(guesses);
                for (int l = 0; l < pairs.size(); l++) {
                    Pair<CartesianCoordinate, CartesianCoordinate> pair = pairs.get(l);
                    CartesianCoordinate cartesianCoordinate = pair.first;
                    PolarCoordinate polarCoordinate = CoordinateTransformer.cartesian2Polar(cartesianCoordinate);

                    double newR = (polarCoordinate.getTheta() * tcof) + (polarCoordinate.getR() * rcof);
                    PolarCoordinate newPola = new PolarCoordinate(polarCoordinate.getTheta(), newR);


                    CartesianCoordinate newCartCord = CoordinateTransformer.polar2Cartesian(newPola);
                    CartesianCoordinate assumedCoordinate = newCartCord;//clamp(width, height, newCartCord);

                    double distance = Math.sqrt(
                            Math.pow(assumedCoordinate.getX() - pair.second.getX(), 2) + Math.pow(assumedCoordinate.getY() - pair.second.getY(), 2));
                    if (distance == 0) {
                        guesses.incrVote();
                    }
                }
                completedIts++;
                //logger.debug(completedIts * 100.0d / (iterations * 1.0d));
            }
        }

        Collections.sort(votes, new Comparator<Guesses>() {
            @Override
            public int compare(Guesses o1, Guesses o2) {
                if (o1.getRcof() == 1 && o1.getTcof() == 0) {
                    System.out.println(o1.getVotes());
                }
                return (o1.getVotes() - o2.getVotes());
            }
        });

        System.out.println(votes);
        System.out.println(votes.get(votes.size() - 1));
        System.out.println(votes.get(votes.size() - 1).getVotes() + " votes");*/



     /*   DescriptiveStatistics ds=new DescriptiveStatistics();
        for(double r:r1){
            ds.addValue(r);
        }
        System.out.println("R1");
        System.out.println(ds.getMean());
        System.out.println(ds.getPercentile(50));

        ds=new DescriptiveStatistics();
        for(double r:r2){
            ds.addValue(r);
        }
        System.out.println("R2");
        System.out.println(ds.getMean());
        System.out.println(ds.getPercentile(50));

        ds=new DescriptiveStatistics();
        for(double r:r3){
            ds.addValue(r);
        }
        System.out.println("R3");
        System.out.println(ds.getMean());
        System.out.println(ds.getPercentile(50));*/


    }

    private static Random random = new Random();

    private static int getRandom(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }

    private static double round(double value) {
        return (double) Math.round(value * 1000d) / 1000d;
    }


    private static CartesianCoordinate clamp(int width, int height, CartesianCoordinate cartesianCoordinate) {
        int x = (int) Math.round(cartesianCoordinate.getX());
        x = x >= width ? width - 1 : x;
        x = x < 0 ? 0 : x;

        int y = (int) Math.round(cartesianCoordinate.getY());
        y = y >= height ? height - 1 : y;
        y = y < 0 ? 0 : y;

        return new CartesianCoordinate(x, y);
    }


    private static final BigDecimal SQRT_DIG = new BigDecimal(150);
    private static final BigDecimal SQRT_PRE = new BigDecimal(10).pow(SQRT_DIG.intValue());

    /**
     * Private utility method used to compute the square root of a BigDecimal.
     *
     * @author Luciano Culacciatti
     * @url http://www.codeproject.com/Tips/257031/Implementing-SqrtRoot-in-BigDecimal
     */
    private static BigDecimal sqrtNewtonRaphson(BigDecimal c, BigDecimal xn, BigDecimal precision) {
        BigDecimal fx = xn.pow(2).add(c.negate());
        BigDecimal fpx = xn.multiply(new BigDecimal(2));
        BigDecimal xn1 = fx.divide(fpx, 2 * SQRT_DIG.intValue(), RoundingMode.HALF_DOWN);
        xn1 = xn.add(xn1.negate());
        BigDecimal currentSquare = xn1.pow(2);
        BigDecimal currentPrecision = currentSquare.subtract(c);
        currentPrecision = currentPrecision.abs();
        if (currentPrecision.compareTo(precision) <= -1) {
            return xn1;
        }
        return sqrtNewtonRaphson(c, xn1, precision);
    }

    /**
     * Uses Newton Raphson to compute the square root of a BigDecimal.
     *
     * @author Luciano Culacciatti
     * @url http://www.codeproject.com/Tips/257031/Implementing-SqrtRoot-in-BigDecimal
     */
    public static BigDecimal bigSqrt(BigDecimal c) {
        return sqrtNewtonRaphson(c, new BigDecimal(1), new BigDecimal(1).divide(SQRT_PRE));
    }
}
