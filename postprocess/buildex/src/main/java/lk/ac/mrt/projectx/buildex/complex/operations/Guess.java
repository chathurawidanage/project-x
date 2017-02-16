package lk.ac.mrt.projectx.buildex.complex.operations;

import lk.ac.mrt.projectx.buildex.models.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chathura Widanage
 */
public class Guess {
    private List<Pair<Operation, Double>> guesses = new ArrayList<>();

    public void addGuess(Pair<Operation, Double> guess) {
        guesses.add(guess);
    }

    public double getProcessedValue(double r, double theta) {
        double sum = 0;
        for (Pair<Operation, Double> guess : guesses) {
            sum += (guess.first.operate(r, theta) * guess.second);
        }
        return sum;
    }
}
