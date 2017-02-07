package lk.ac.mrt.projectx.buildex.complex;

import lk.ac.mrt.projectx.buildex.complex.cordinates.CartesianCoordinate;
import lk.ac.mrt.projectx.buildex.complex.cordinates.PolarCoordinate;
import lk.ac.mrt.projectx.buildex.models.Pair;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Chathura Widanage
 */
public class GuessesValidationService {
    private Queue<Guesses> guesses = new LinkedList<>();
    private ExecutorService executorService = Executors.newFixedThreadPool(4);
    private List<Pair<CartesianCoordinate, CartesianCoordinate>> testCases;
    private AtomicInteger executingCounter = new AtomicInteger(0);

    private int width, height;
    private boolean isR;

    public GuessesValidationService(List<Pair<CartesianCoordinate, CartesianCoordinate>> testCases, int width, int height, boolean isR) {
        this.testCases = testCases;
        this.width = width;
        this.height = height;
        this.isR = isR;
    }

    public void submit(Guesses guess) {
        System.out.println(executingCounter);
        guesses.add(guess);
        checkAndExecute();
    }

    public void awaitTermination() throws InterruptedException {
        while (!guesses.isEmpty()) {
            Thread.sleep(10000);
        }
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.DAYS);
    }

    private void checkAndExecute() {
        if (executingCounter.intValue() < 400) {
            Guesses poll;
            synchronized (guesses) {
                poll = guesses.poll();
            }
            if (poll != null) {
                submitNewTask(poll);
            }
        }
    }

    private void submitNewTask(final Guesses guess) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                executingCounter.incrementAndGet();
                for (int p = 0; p < testCases.size(); p++) {
                    Pair<CartesianCoordinate, CartesianCoordinate> pair = testCases.get(p);
                    CartesianCoordinate cartesianCoordinate = pair.first;

                    PolarCoordinate polarCoordinate = CoordinateTransformer.cartesian2Polar(width, height, cartesianCoordinate);

                    double newValue = (polarCoordinate.getTheta() * guess.getTcof()) +
                            (polarCoordinate.getR() * guess.getRcof()) + (Math.pow(polarCoordinate.getTheta(), 2)) * guess.getT2cof()
                            + (Math.pow(polarCoordinate.getR(), 2)) * guess.getR2cof() + (polarCoordinate.getR() * polarCoordinate.getTheta() * guess.getRtcof())
                            + guess.getCcof();
                    PolarCoordinate newPola;
                    if (isR) {
                        newPola = new PolarCoordinate(polarCoordinate.getTheta(), newValue);
                    } else {
                        newValue = MathUtils.normalizeAngle(newValue, FastMath.PI);
                        newPola = new PolarCoordinate(newValue, polarCoordinate.getR());
                    }

                    PolarCoordinate realOut = CoordinateTransformer.cartesian2Polar(width, height, pair.second, true);

                    double distance = 5;
                    if (isR) {
                        distance = Math.abs(realOut.getR() - newPola.getR()) / newPola.getR();
                    } else {
                        distance = Math.abs(realOut.getTheta() - newPola.getTheta()) / newPola.getTheta();
                    }
                    if (distance <= 0.0001) {
                        guess.incrVote();
                    }
                }
                executingCounter.decrementAndGet();
                checkAndExecute();
            }
        });
    }
}
