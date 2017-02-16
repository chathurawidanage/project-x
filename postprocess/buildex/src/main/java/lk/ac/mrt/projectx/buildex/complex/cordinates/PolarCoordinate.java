package lk.ac.mrt.projectx.buildex.complex.cordinates;

/**
 * @author Chathura Widanage
 */
public class PolarCoordinate {
    private double theta;
    private double r;

    public PolarCoordinate(double theta, double r) {
        this.theta = theta;
        this.r = r;
    }

    public double getTheta() {
        return theta;
    }

    public void setTheta(double theta) {
        this.theta = theta;
    }

    public double getR() {
        return r;
    }

    public void setR(double r) {
        this.r = r;
    }

    @Override
    public String toString() {
        return this.theta + "," + this.r;
    }
}
