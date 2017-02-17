package lk.ac.mrt.projectx.buildex.complex;

import lk.ac.mrt.projectx.buildex.complex.cordinates.CartesianCoordinate;
import lk.ac.mrt.projectx.buildex.complex.operations.Guess;
import lk.ac.mrt.projectx.buildex.complex.operations.GuessesGenerator;
import lk.ac.mrt.projectx.buildex.models.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * @author Chathura Widanage
 */
public class ApproximatorNew {
    private static final Logger logger = LogManager.getLogger(ApproximatorNew.class);

    public static Guess approximate(final GuessesGenerator guessesGenerator, final List<Pair<CartesianCoordinate, CartesianCoordinate>> pairs, final boolean isR, Guess rGuess, final int width, final int height) {
        final List<Pair<CartesianCoordinate, CartesianCoordinate>> tests = getTestCases(pairs);
        GuessesValidationServiceNew gvs = new GuessesValidationServiceNew(tests, width, height, isR, rGuess);
        while (guessesGenerator.hasNext()) {
            Guess guess = guessesGenerator.next();
            try {
                gvs.submit(guess);
            } catch (InterruptedException e) {
                logger.error("Interrupted while submitting a guess", e);
            }
        }

        try {
            logger.debug("Waiting for test termination");
            List<Guess> maxVoters = gvs.awaitTermination();
            //logger.debug("Maximum voters {}", maxVoters);
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
        Set<Integer> indexes = new HashSet<>();
        Random r = new Random();
        while (indexes.size() < Math.min(1000, Math.sqrt(pairs.size()))) {
            indexes.add(r.nextInt(pairs.size()));
        }
        for (int index : indexes) {
            q1.add(pairs.get(index));
        }
        return new ArrayList<>(q1);
    }
}
