package lk.ac.mrt.projectx.buildex.complex.filters;

import lk.ac.mrt.projectx.buildex.complex.CoordinateTransformer;
import lk.ac.mrt.projectx.buildex.complex.cordinates.CartesianCoordinate;
import lk.ac.mrt.projectx.buildex.complex.cordinates.PolarCoordinate;

import java.awt.image.BufferedImage;

/**
 * @author Chathura Widanage
 */
public class CylinderFilter {
    public void filter(BufferedImage in, BufferedImage out) {

    }

    public void filterCartesian(BufferedImage in, BufferedImage out) {
       int width=in.getWidth();
       int height=in.getHeight();
       for(int i=0;i<width;i++){
           for(int j=0;j<height;j++){

           }
       }
    }

    private boolean clampPass(int width, int height, CartesianCoordinate cartesianCoordinate) {
        int x = (int) Math.round(cartesianCoordinate.getX());

        int y = (int) Math.round(cartesianCoordinate.getY());

        return x >= 0 && x <= width - 1 && y >= 0 && y <= height - 1;
    }
}
