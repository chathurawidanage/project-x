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

    public static Guesses approximate(final LoopBounds loopBounds, final List<Pair<CartesianCoordinate, CartesianCoordinate>> pairs, final boolean isR, Guesses rGuess,final int width, final int height) {
        final List<Pair<CartesianCoordinate, CartesianCoordinate>> tests = getTestCases(pairs);
        GuessesValidationService gvs = new GuessesValidationService(tests, width, height, isR,rGuess);
        for (int i = loopBounds.r.low; i <= loopBounds.r.high; i++) {
            final double rcof = i * 1.0 / 1000d;
            for (int j = loopBounds.t.low; j <= loopBounds.t.high; j++) {
                final double tcof = j * 1.0 / 1000d;
                for (int k = loopBounds.r2.low; k <= loopBounds.r2.high; k++) {
                    final double r2cof = k * 1.0 / 1000d;
                    for (int l = loopBounds.t2.low; l <= loopBounds.t2.high; l++) {
                        final double t2cof = l * 1.0 / 1000d;
                        for (int m = loopBounds.rt.low; m <= loopBounds.rt.high; m++) {
                            final double rtcof = m * 1.0 / 1000d;
                            for (int n = loopBounds.c.low; n <= loopBounds.c.high; n++) {
                                final double ccof = n * 1.0 / 1000d;
                                Guesses guesses = new Guesses();
                                guesses.setRcof(rcof);
                                guesses.setTcof(tcof);
                                guesses.setT2cof(t2cof);
                                guesses.setR2cof(r2cof);
                                guesses.setRtcof(rtcof);
                                guesses.setCcof(ccof);
                                try {
                                    gvs.submit(guesses);
                                } catch (InterruptedException e) {
                                    logger.error("Error in submitting guess",e);
                                }
                            }
                        }
                    }
                }
            }
        }

        try {
            logger.debug("Waiting for test termination");
            List<Guesses> maxVoters = gvs.awaitTermination();
            logger.debug("Maximum voters ", maxVoters);
            logger.debug("Maximum votes is {} out of {}", maxVoters.get(0).getVotes(), tests.size());
            return maxVoters.get(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static List<Pair<CartesianCoordinate, CartesianCoordinate>> getTestCases(List<Pair<CartesianCoordinate, CartesianCoordinate>> pairs) {
        Set<Pair<CartesianCoordinate, CartesianCoordinate>> q1 = new HashSet<>();
        List<Pair<CartesianCoordinate, CartesianCoordinate>> q2 = new ArrayList<>();
        List<Pair<CartesianCoordinate, CartesianCoordinate>> q3 = new ArrayList<>();
        List<Pair<CartesianCoordinate, CartesianCoordinate>> q4 = new ArrayList<>();
        Set<Integer> indexes=new HashSet<>();
        Random r = new Random();
        while ( indexes.size() < Math.min(1000,Math.sqrt(pairs.size())) ) {
            indexes.add(r.nextInt(pairs.size()));
        }
        for(int index:indexes){
            q1.add(pairs.get(index));
        }
        return new ArrayList<>(q1);
    }
}
