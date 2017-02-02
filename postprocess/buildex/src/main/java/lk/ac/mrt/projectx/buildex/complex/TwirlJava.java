package lk.ac.mrt.projectx.buildex.complex;

import lk.ac.mrt.projectx.buildex.complex.cordinates.CartesianCoordinate;
import lk.ac.mrt.projectx.buildex.complex.cordinates.PolarCoordinate;

import java.awt.image.BufferedImage;

/**
 * @author Chathura Widanage
 */
public class TwirlJava {
    public void filter(BufferedImage in, BufferedImage out) {
        int width=in.getWidth();
        int height=in.getHeight();
        for (int i = 0; i < in.getWidth(); i++) {
            for (int j = 0; j < in.getHeight(); j++) {
                CartesianCoordinate cartesianCoordinate = new CartesianCoordinate(i, j);
                PolarCoordinate polarCoordinate = CoordinateTransformer.cartesian2Polar(cartesianCoordinate);

                double thetaNew = polarCoordinate.getTheta() + (polarCoordinate.getR() /10);
                PolarCoordinate newPola = new PolarCoordinate(polarCoordinate.getR(), thetaNew);

                CartesianCoordinate newCartCord = CoordinateTransformer.polar2Cartesian(newPola);
                out.setRGB(i, j, clamp(in, newCartCord));
            }
        }
    }

    private int clamp(BufferedImage in, CartesianCoordinate cartesianCoordinate) {
        int x = (int) cartesianCoordinate.getX();
        x = x >= in.getWidth() ? in.getWidth() - 1 : x;
        x = x < 0 ? 0 : x;

        int y = (int) cartesianCoordinate.getY();
        y = y >= in.getHeight() ? in.getHeight() - 1 : y;
        y = y < 0 ? 0 : y;

        return in.getRGB(x, y);
    }
}
