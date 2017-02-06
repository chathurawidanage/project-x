package lk.ac.mrt.projectx.buildex.complex;

import lk.ac.mrt.projectx.buildex.complex.cordinates.CartesianCoordinate;
import lk.ac.mrt.projectx.buildex.complex.cordinates.PolarCoordinate;
import lk.ac.mrt.projectx.buildex.models.Pair;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Chathura Widanage
 */
public class Approximator {
    private static final Logger logger = LogManager.getLogger(Approximator.class);

    public static Guesses approximate(int rCoefLow, int rCoefHigh,
                                      int tCoefLow, int tCoeHigh, final List<Pair<CartesianCoordinate, CartesianCoordinate>> pairs, final boolean isR, final int width, final int height) {
        final List<Pair<CartesianCoordinate,CartesianCoordinate>> tests=getTestCases(pairs);
        final List<Guesses> votes = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(8);
        for (int i = rCoefLow; i <= rCoefHigh; i++) {
            final double rcof = i * 1.0 / 1000d;
            for (int j = tCoefLow; j <= tCoeHigh; j++) {
                final double tcof = j * 1.0 / 1000d;
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        Guesses guesses = new Guesses();
                        guesses.setRcof(rcof);
                        guesses.setTcof(tcof);
                        synchronized (votes) {
                            votes.add(guesses);
                        }
                        for (int l = 0; l < tests.size(); l++) {
                            Pair<CartesianCoordinate, CartesianCoordinate> pair = tests.get(l);
                            CartesianCoordinate cartesianCoordinate = pair.first;

                            PolarCoordinate polarCoordinate = CoordinateTransformer.cartesian2Polar(width, height, cartesianCoordinate);

                            double newValue = (polarCoordinate.getTheta() * tcof) + (polarCoordinate.getR() * rcof);
                            PolarCoordinate newPola;
                            if (isR) {
                                newPola = new PolarCoordinate(polarCoordinate.getTheta(), newValue);
                            } else {
                                newValue=MathUtils.normalizeAngle(newValue, FastMath.PI);
                                newPola = new PolarCoordinate(newValue, polarCoordinate.getR());
                            }

                            PolarCoordinate realOut = CoordinateTransformer.cartesian2Polar(width, height, pair.second,true);

                            /*CartesianCoordinate newCartCord = CoordinateTransformer.polar2Cartesian(newPola);
                            if(pair.second.getX()>=width || newCartCord.getX()<0 || newCartCord.getY()>=height || newCartCord.getY()<0){
                                continue;//only non clamping members get a chance to vote
                            }*/

                            /*CartesianCoordinate assumedCoordinate = newCartCord;

                            double distance = Math.sqrt(
                                    Math.pow(assumedCoordinate.getX() - pair.second.getX(), 2) + Math.pow(assumedCoordinate.getY() - pair.second.getY(), 2));
                           */
                            double distance = 5;
                            if (isR) {
                                distance = Math.abs(realOut.getR() - newPola.getR()) / newPola.getR();
                            } else {
                                distance = Math.abs(realOut.getTheta() - newPola.getTheta()) / newPola.getTheta();
                            }
                            if (distance <= 0.001) {
                                guesses.incrVote();
                            }
                        }
                    }
                });

                //logger.debug(completedIts * 100.0d / (iterations * 1.0d));
            }
        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Collections.sort(votes, new Comparator<Guesses>() {
            @Override
            public int compare(Guesses o1, Guesses o2) {
                return (o2.getVotes() - o1.getVotes());
            }
        });
/*
        System.out.println(votes);
        System.out.println(votes.get(votes.size() - 1));
        System.out.println(votes.get(votes.size() - 1).getVotes() + " votes");*/
        //logger.debug(votes);
        int maxVotes = votes.get(0).getVotes();
        int untilIndex = 0;
        for (Guesses g : votes) {
            if (g.getVotes() == maxVotes) {
                untilIndex++;
            } else {
                break;
            }
        }
        System.out.println(votes.subList(0, untilIndex));
        return votes.get(0);
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

    private static List<Pair<CartesianCoordinate, CartesianCoordinate>> getTestCases(List<Pair<CartesianCoordinate, CartesianCoordinate>> pairs) {
        Set<Pair<CartesianCoordinate, CartesianCoordinate>> q1 = new HashSet<>();
        List<Pair<CartesianCoordinate, CartesianCoordinate>> q2 = new ArrayList<>();
        List<Pair<CartesianCoordinate, CartesianCoordinate>> q3 = new ArrayList<>();
        List<Pair<CartesianCoordinate, CartesianCoordinate>> q4 = new ArrayList<>();
        Random r = new Random();
        for (int i = 0; i < pairs.size()/4; ) {
            if(q1.add(pairs.get(r.nextInt(pairs.size())))){
                i++;
            }
        }
        return new ArrayList<>(q1);
    }
}
