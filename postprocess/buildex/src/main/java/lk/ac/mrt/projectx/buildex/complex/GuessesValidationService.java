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
import java.util.concurrent.locks.Lock;

/**
 * @author Chathura Widanage
 */
public class GuessesValidationService {
    private Queue<Guesses> guesses = new LinkedList<>();
    private ExecutorService executorService = Executors.newFixedThreadPool(4);
    private List<Pair<CartesianCoordinate, CartesianCoordinate>> testCases;
    private AtomicInteger executingCounter = new AtomicInteger(0);

    Semaphore semaphore = new Semaphore(1);
    private Integer maxVotes = 0;
    private List<Guesses> maxVoters = new ArrayList<>();

    private int width, height;
    private boolean isR;

    private Guesses rGuess;

    public GuessesValidationService(List<Pair<CartesianCoordinate,
            CartesianCoordinate>> testCases, int width,
                                    int height, boolean isR,Guesses rGuess) {
        this.testCases = testCases;
        this.width = width;
        this.height = height;
        this.isR = isR;
        this.rGuess=rGuess;
    }

    public void submit(Guesses guess) throws InterruptedException {
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

                        double rVal=(polarCoordinate.getTheta() * rGuess.getTcof()) +
                                (polarCoordinate.getR() * rGuess.getRcof()) +
                                (Math.pow(polarCoordinate.getTheta(), 2)) * rGuess.getT2cof()
                                + (Math.pow(polarCoordinate.getR(), 2)) * rGuess.getR2cof() +
                                (polarCoordinate.getR() * polarCoordinate.getTheta() * rGuess.getRtcof())
                                + rGuess.getCcof();

                        newValue = MathUtils.normalizeAngle(newValue, FastMath.PI);
                        //System.out.println(newValue);
                        newPola = new PolarCoordinate(newValue, rVal);
                    }


                    double distance = 5;
                    if (isR) {
                        double x=pair.second.getX()-width/2;
                        double y=pair.second.getY()-height/2;
                        distance = Math.abs(Math.hypot(x,y) - newPola.getR());
                    } else {
                        CartesianCoordinate genertaed=CoordinateTransformer.polar2Cartesian(width,height,newPola);
                        double x=pair.second.getX()-width/2;
                        double y=pair.second.getY()-height/2;
                        distance = Math.sqrt(
                                Math.pow(genertaed.getX()-x,2)
                                        +Math.pow(genertaed.getY()-y,2)
                        );// / newPola.getTheta();
                        //System.out.println(genertaed+","+pair.second);
                    }
                    if (isR && distance <= Math.sqrt(2)) {
                        guess.incrVote();
                    }else if(!isR && distance<=1.5){
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
