package lk.ac.mrt.projectx.buildex.complex.operations;

import lk.ac.mrt.projectx.buildex.models.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chathura Widanage
 */
public class Guess {
    private List<Pair<Operation, Double>> guesses = new ArrayList<>();

    private long votes;

    private OperandDecorator guessOperator;

    public OperandDecorator getGuessOperator() {
        return guessOperator;
    }

    public void setGuessOperator(OperandDecorator guessOperator) {
        this.guessOperator = guessOperator;
    }

    public long getVotes() {
        return votes;
    }

    public void incrementVote() {
        votes++;
    }

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

    public String getGeneratedCode() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < guesses.size(); i++) {
            Pair<Operation, Double> p = guesses.get(i);
            stringBuilder.append("(")
                    .append(p.second)
                    .append("*")
                    .append(p.first.getCode())
                    .append(")");
            if (i != guesses.size() - 1) {
                stringBuilder.append("+");
            }
        }
        if (this.guessOperator != null) {
            return String.format(this.guessOperator.operation, stringBuilder.toString());
        }
        stringBuilder.append(";");

        return stringBuilder.toString();
    }

    public List<Pair<Operation, Double>> getGuesses() {
        return guesses;
    }

    @Override
    public String toString() {
        return "Guess{" +
                "guesses=" + guesses +
                ", votes=" + votes +
                '}';
    }
}
