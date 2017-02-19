package lk.ac.mrt.projectx.buildex.complex;

import lk.ac.mrt.projectx.buildex.complex.cordinates.CartesianCoordinate;
import lk.ac.mrt.projectx.buildex.models.Pair;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yasiru Kassapa
 */
public class SourceDestinationSeeker {
    public List<Pair<CartesianCoordinate, CartesianCoordinate>> generate(BufferedImage sourceImg, BufferedImage filteredImage) throws Exception {
        Map<Integer, CartesianCoordinate> colorLocationDirectory = new HashMap<>();
        List<Pair<CartesianCoordinate, CartesianCoordinate>> mappings = new ArrayList<>();
        CartesianCoordinate destinationCoordinate;
        int height = sourceImg.getHeight();
        int width = sourceImg.getWidth();

        if (filteredImage.getHeight() != height || filteredImage.getWidth() != width) {
            throw new Exception("Image dimensions mismatch");
        }

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                colorLocationDirectory.put(filteredImage.getRGB(j, i), new CartesianCoordinate(j, i));
            }
        }

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                destinationCoordinate = colorLocationDirectory.get(sourceImg.getRGB(j, i));
                if (destinationCoordinate != null) {
                    mappings.add(new Pair(new CartesianCoordinate(j, i), destinationCoordinate));
                }
            }
        }

        return mappings;
    }

}
