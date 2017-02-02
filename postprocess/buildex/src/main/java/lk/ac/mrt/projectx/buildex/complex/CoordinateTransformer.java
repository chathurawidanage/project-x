package lk.ac.mrt.projectx.buildex.complex;

import lk.ac.mrt.projectx.buildex.complex.cordinates.PolarCoordinate;
import lk.ac.mrt.projectx.buildex.complex.cordinates.RectangularCoordinate;

/**
 * @author Chathura Widanage
 */
public class CoordinateTransformer {
    public static PolarCoordinate rectangular2Polar(RectangularCoordinate rectangularCoordinate) {
        double r = Math.sqrt(Math.pow(rectangularCoordinate.getX(), 2)
                + Math.pow(rectangularCoordinate.getY(), 2));
        double theta = Math.acos(rectangularCoordinate.getX() / r);
        return new PolarCoordinate(theta, r);
    }

    public static RectangularCoordinate polar2Rectangular(PolarCoordinate polarCoordinate) {
        double x = polarCoordinate.getR() * Math.cos(polarCoordinate.getTheta());
        double y = polarCoordinate.getR() * Math.sin(polarCoordinate.getTheta());
        return new RectangularCoordinate(x, y);
    }

}
