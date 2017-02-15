package lk.ac.mrt.projectx.buildex.complex.filters;

import lk.ac.mrt.projectx.buildex.complex.CoordinateTransformer;
import lk.ac.mrt.projectx.buildex.complex.cordinates.CartesianCoordinate;
import lk.ac.mrt.projectx.buildex.complex.cordinates.PolarCoordinate;

import java.awt.image.BufferedImage;

/**
 * @author Chathura Widanage
 */
public class WaveJava {
    public void filter(BufferedImage in, BufferedImage out) {
        int width = in.getWidth();
        int height = in.getHeight();
        for (int i = 0; i < in.getWidth(); i++) {
            for (int j = 0; j < in.getHeight(); j++) {
                CartesianCoordinate cartesianCoordinate = new CartesianCoordinate(i, j);
                PolarCoordinate polarCoordinate = CoordinateTransformer.cartesian2Polar(width, height, cartesianCoordinate);

                double thetaNew =
                        polarCoordinate.getTheta() * 1.018 + (polarCoordinate.getR() * 0.011)
                        -0.024*Math.pow(polarCoordinate.getTheta(),2)                        ;
                double rNew = 0.225* polarCoordinate.getTheta() + (polarCoordinate.getR() * 0.989)-0.007*(polarCoordinate.getR()*polarCoordinate.getTheta());
                //thetaNew= MathUtils.normalizeAngle(thetaNew, FastMath.PI);
                PolarCoordinate newPola = new PolarCoordinate(thetaNew, rNew);

                CartesianCoordinate newCartCord = CoordinateTransformer.polar2Cartesian(width, height, newPola);
                if (clampPass(width, height, newCartCord))
                    out.setRGB(i, j, in.getRGB((int)newCartCord.getX(), (int)newCartCord.getY()));
            }
        }
    }

    public void filterCartesian(BufferedImage in, BufferedImage out) {
        int width  = in.getWidth();
        int height = out.getHeight();


        // wave filter
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int xx = x;
                int yy = (int) (y + 20 * Math.sin(x * 2 * Math.PI / 64));
                if (yy >= 0 && yy < height) {
                    out.setRGB(x, y, in.getRGB(xx, yy));
                }
            }
        }
    }

    private boolean clampPass(int width, int height, CartesianCoordinate cartesianCoordinate) {
        int x = (int) Math.round(cartesianCoordinate.getX());

        int y = (int) Math.round(cartesianCoordinate.getY());

        return x >= 0 && x <= width - 1 && y >= 0 && y <= height - 1;
    }
}
