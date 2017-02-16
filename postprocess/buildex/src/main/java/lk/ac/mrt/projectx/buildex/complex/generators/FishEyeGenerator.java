package lk.ac.mrt.projectx.buildex.complex.generators;

import lk.ac.mrt.projectx.buildex.complex.cordinates.CartesianCoordinate;
import lk.ac.mrt.projectx.buildex.models.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chathura Widanage
 */
public class FishEyeGenerator extends Generator {
    public List<Pair<CartesianCoordinate, CartesianCoordinate>> generate(int width, int height) {
        List<Pair<CartesianCoordinate, CartesianCoordinate>> examples = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double nx = ((2.0f * x) / (width)) - 1;
                double ny = ((2.0f * y) / (height)) - 1;
                double r = Math.hypot(nx, ny);
                double nr = (r + (1 - (Math.sqrt(1 - r * r)))) / (2.0f);
                double theta = Math.atan2(ny, nx);
                double nxn = nr * Math.cos(theta);
                double nyn = nr * Math.sin(theta);
                CartesianCoordinate outCart = new CartesianCoordinate(
                        ((nxn + 1) * width) / 2,
                        ((nyn + 1) * height) / 2
                );
                if (clampPass(width, height, outCart)) {
                    CartesianCoordinate in = new CartesianCoordinate(x, y);
                    examples.add(new Pair<>(
                            in,
                            outCart
                    ));
                }
            }
        }
        return examples;
    }
}
