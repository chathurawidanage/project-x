package lk.ac.mrt.projectx.buildex.complex.generators;

import lk.ac.mrt.projectx.buildex.complex.cordinates.CartesianCoordinate;
import lk.ac.mrt.projectx.buildex.models.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chathura Widanage
 */
public class WaveGenerator extends Generator{
    public List<Pair<CartesianCoordinate, CartesianCoordinate>> generate(int width, int height) {
        List<Pair<CartesianCoordinate, CartesianCoordinate>> examples = new ArrayList<>();
        // wave filter
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int xx = x;
                int yy = (int) (y + 20 * Math.sin(x * 2 * Math.PI / 64));
                if (yy >= 0 && yy < height) {
                    CartesianCoordinate source = new CartesianCoordinate(x, y);
                    CartesianCoordinate dest = new CartesianCoordinate(xx, yy);
                    examples.add(new Pair<>(source, dest));
                }
            }
        }
        return examples;
    }
}
