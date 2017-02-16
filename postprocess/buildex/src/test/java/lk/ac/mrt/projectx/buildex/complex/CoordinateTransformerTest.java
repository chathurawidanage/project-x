package lk.ac.mrt.projectx.buildex.complex;

import lk.ac.mrt.projectx.buildex.complex.cordinates.CartesianCoordinate;
import lk.ac.mrt.projectx.buildex.complex.cordinates.PolarCoordinate;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Chathura Widanage
 */
public class CoordinateTransformerTest {
    @Test
    public void atant2() throws Exception {
        for(int x=-10;x<10;x++){
            for(int y=-10;y<10;y++){
                double theta = CoordinateTransformer.atan2(y,x);
                System.out.println(Math.toDegrees(theta));
                double r=Math.hypot(x,y);
                double genX=r*Math.cos(theta);
                double genY=r*Math.sin(theta);
                assertEquals(x,genX,0.001);
                assertEquals(y,genY,0.001);
            }
        }
    }

    @Test
    public void cartesian2Polar() throws Exception {
        for (int i = 0; i < 128; i++) {
            for (int j = 0; j < 128; j++) {
                CartesianCoordinate c1 = new CartesianCoordinate(i, j);
                PolarCoordinate polarCoordinate = CoordinateTransformer.cartesian2Polar(128, 128, c1);
                CartesianCoordinate c2 = CoordinateTransformer.polar2Cartesian(128, 128, polarCoordinate);
                assertEquals(Math.round(c1.getX()), Math.round(c2.getX()));
                assertEquals(Math.round(c1.getY()), Math.round(c2.getY()));
            }
        }
    }

    @Test
    public void polar2Cartesian() throws Exception {
        for (int r = 0; r < 64; r++) {
            for (int theta = (int) -Math.PI * 1000; theta < (int) Math.PI * 1000; theta++) {
                PolarCoordinate p1 = new PolarCoordinate(theta / 1000, r);
                CartesianCoordinate cartesianCoordinate = CoordinateTransformer.polar2Cartesian(p1);
                PolarCoordinate p2 = CoordinateTransformer.cartesian2Polar(cartesianCoordinate);
                System.out.println(p1 + "->" + cartesianCoordinate + "->" + p2);
                assertEquals(Math.round(p1.getR()), Math.round(p2.getR()));
                assertEquals(Math.round(p1.getTheta()), Math.round(p2.getTheta()));
            }
        }
    }

}