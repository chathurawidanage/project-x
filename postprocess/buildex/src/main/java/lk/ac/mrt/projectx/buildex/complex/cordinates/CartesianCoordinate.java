package lk.ac.mrt.projectx.buildex.complex.cordinates;

/**
 * @author Chathura Widanage
 */
public class CartesianCoordinate {
    private double x;
    private double y;

    public CartesianCoordinate(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return x + "," + y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CartesianCoordinate)) return false;

        CartesianCoordinate that = (CartesianCoordinate) o;

        if (Double.compare(that.getX(), getX()) != 0) return false;
        return Double.compare(that.getY(), getY()) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(getX());
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getY());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
