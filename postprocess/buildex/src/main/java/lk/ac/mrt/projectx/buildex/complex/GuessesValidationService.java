package lk.ac.mrt.projectx.buildex.complex;

import lk.ac.mrt.projectx.buildex.complex.cordinates.CartesianCoordinate;
import lk.ac.mrt.projectx.buildex.complex.cordinates.PolarCoordinate;
import lk.ac.mrt.projectx.buildex.models.Pair;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Chathura Widanage
 */
public class GuessesValidationService {
    private Queue<Guesses> guesses = new LinkedList<>();
    private ExecutorService executorService = Executors.newFixedThreadPool(4);
    private List<Pair<CartesianCoordinate, CartesianCoordinate>> testCases;
    private AtomicInteger executingCounter = new AtomicInteger(0);
    private AtomicLong itCount=new AtomicLong();

    Semaphore semaphore = new Semaphore(1);
    Lock lock = new ReentrantLock();
    private Integer maxVotes = 0;
    private List<Guesses> maxVoters = new ArrayList<>();

    private int width, height;
    private boolean isR;

    private Guesses rGuess;

    public GuessesValidationService(List<Pair<CartesianCoordinate,
            CartesianCoordinate>> testCases, int width,
                                    int height, boolean isR, Guesses rGuess) {
        this.testCases = testCases;
        this.width = width;
        this.height = height;
        this.isR = isR;
        this.rGuess = rGuess;
    }

    public void submit(Guesses guess) throws InterruptedException {
        lock.lock();
        guesses.add(guess);
        checkAndExecute();
    }

    public List<Guesses> awaitTermination() throws InterruptedException {
        while (!guesses.isEmpty()) {

        }
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.DAYS);
        return maxVoters;
    }

    private void checkAndExecute() {
        if (guesses.size() < 100) {
            lock.unlock();
        }
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
                System.out.print("\r"+itCount.incrementAndGet());
                for (int p = 0; p < testCases.size(); p++) {
                    Pair<CartesianCoordinate, CartesianCoordinate> pair = testCases.get(p);
                    CartesianCoordinate cartesianCoordinateFirst = pair.first;
                    CartesianCoordinate cartesianCoordinateSecond = pair.second;

                    PolarCoordinate polarCoordinateFirst = CoordinateTransformer.cartesian2Polar(cartesianCoordinateFirst);
                    PolarCoordinate polarCoordinateSecond = CoordinateTransformer.cartesian2Polar(cartesianCoordinateSecond);

                    double newValue = (polarCoordinateFirst.getTheta() * guess.getTcof()) +
                            (polarCoordinateFirst.getR() * guess.getRcof()) + (Math.pow(polarCoordinateFirst.getTheta(), 2)) * guess.getT2cof()
                            + (Math.pow(polarCoordinateFirst.getR(), 2)) * guess.getR2cof() + (polarCoordinateFirst.getR() * polarCoordinateFirst.getTheta() * guess.getRtcof())
                            + guess.getCcof();
                    PolarCoordinate newPolar;
                    if (isR) {
                        newPolar = new PolarCoordinate(polarCoordinateFirst.getTheta(), newValue);
                    } else {

                        double rVal = (polarCoordinateFirst.getTheta() * rGuess.getTcof()) +
                                (polarCoordinateFirst.getR() * rGuess.getRcof()) +
                                (Math.pow(polarCoordinateFirst.getTheta(), 2) * rGuess.getT2cof())
                                + (Math.pow(polarCoordinateFirst.getR(), 2) * rGuess.getR2cof()) +
                                (polarCoordinateFirst.getR() * polarCoordinateFirst.getTheta() * rGuess.getRtcof())
                                + rGuess.getCcof();

                        newValue = MathUtils.normalizeAngle(newValue, FastMath.PI);
                        //System.out.println(newValue);
                        //System.out.println(polarCoordinateSecond.getR()+","+rVal);
                        newPolar = new PolarCoordinate(newValue, rVal);
                    }


                    double distance = 5;
                    if (isR) {
                        distance = Math.abs(polarCoordinateSecond.getR() - newPolar.getR());
                    } else {
                        CartesianCoordinate genertaed = CoordinateTransformer.polar2Cartesian(newPolar);
                        double x = pair.second.getX();
                        double y = pair.second.getY();
                        distance = Math.sqrt(
                                Math.pow(genertaed.getX() - x, 2)
                                        + Math.pow(genertaed.getY() - y, 2)
                        );// / newPolar.getTheta();
                        //System.out.println(x+","+genertaed.getX());
                        //System.out.println(distance);
                    }
                    if (isR && distance <= Math.sqrt(2)) {
                        guess.incrVote();
                    } else if (!isR && distance <= 25) {
                        guess.incrVote();
                    }
                }
                try {
                    semaphore.acquire();
                    if (maxVotes < guess.getVotes()) {
                        maxVotes = guess.getVotes();
                        maxVoters.clear();
                        maxVoters.add(guess);
                    } else if (maxVotes == guess.getVotes()) {
                        maxVoters.add(guess);
                    }
                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                executingCounter.decrementAndGet();
                checkAndExecute();
            }
        });
    }
}
