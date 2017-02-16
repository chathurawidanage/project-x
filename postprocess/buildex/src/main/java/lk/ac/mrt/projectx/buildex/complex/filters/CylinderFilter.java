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
        int width = in.getWidth();
        int height = in.getHeight();
        for (int i = 0; i < in.getWidth(); i++) {
            for (int j = 0; j < in.getHeight(); j++) {
                CartesianCoordinate cartesianCoordinate = new CartesianCoordinate(i, j);
                PolarCoordinate polarCoordinate = CoordinateTransformer.cartesian2Polar(width, height, cartesianCoordinate);

                double thetaNew =0 * polarCoordinate.getTheta() +
                        (polarCoordinate.getR() * 0) + 0.003 * (polarCoordinate.getR() * polarCoordinate.getTheta()) +
                        0 * Math.pow(polarCoordinate.getR(), 2) + 0 * Math.pow(polarCoordinate.getTheta(), 2);
                double rNew = (0 * polarCoordinate.getTheta()) +
                        (polarCoordinate.getR() * 0.481) + 0.216 * (polarCoordinate.getR() * polarCoordinate.getTheta()) +
                        (0* Math.pow(polarCoordinate.getR(), 2)) +(0* Math.pow(polarCoordinate.getTheta(), 2))+0;
                //thetaNew= MathUtils.normalizeAngle(thetaNew, FastMath.PI);
                PolarCoordinate newPola = new PolarCoordinate(thetaNew, rNew);

                CartesianCoordinate newCartCord = CoordinateTransformer.polar2Cartesian(width, height, newPola);
                if (clampPass(width, height, newCartCord))
                    out.setRGB(i, j, in.getRGB((int) newCartCord.getX(), (int) newCartCord.getY()));
            }
        }
    }

    public void filterCartesian(BufferedImage in, BufferedImage out) {
       int width=in.getWidth();
       int height=in.getHeight();
       for(int x=0;x<width;x++){
           for(int y=0;y<height;y++){
               double centerX = width / 2;
               double centerY = height / 2;
               double R = Math.sqrt(Math.pow(centerX, 2) + Math.pow(centerY, 2));

               double dx = x - centerX;
               double dy = y - centerY;

               double distance = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
               double xout = Math.atan2(dx, dy)* centerX / Math.PI;
               double yout = distance * height/ R;

               int tx =(int)(xout + centerX);
               int ty = (int)(yout);

               if (tx >= 0 && tx < width && ty >= 0 && ty < height)
                   out.setRGB(x, y, in.getRGB(tx, ty));

           }
       }
    }

    private boolean clampPass(int width, int height, CartesianCoordinate cartesianCoordinate) {
        int x = (int) Math.round(cartesianCoordinate.getX());

        int y = (int) Math.round(cartesianCoordinate.getY());

        return x >= 0 && x <= width - 1 && y >= 0 && y <= height - 1;
    }
}
