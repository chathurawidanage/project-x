package lk.ac.mrt.projectx.buildex.complex;

import lk.ac.mrt.projectx.buildex.complex.cordinates.CartesianCoordinate;
import lk.ac.mrt.projectx.buildex.complex.generators.Generator;
import lk.ac.mrt.projectx.buildex.models.Pair;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wik2kassa
 */
public class EluwaEngine {
    public List<Pair<CartesianCoordinate, CartesianCoordinate>> generate(BufferedImage sourceImg, BufferedImage filteredImage) throws Exception {
        Map<Integer, CartesianCoordinate> colorLocationDirectory = new HashMap<>();
        List<Pair<CartesianCoordinate, CartesianCoordinate>> mappings = new ArrayList<>();
        CartesianCoordinate sourceCodinate;
        int height = sourceImg.getHeight();
        int width = sourceImg.getWidth();

        if (filteredImage.getHeight() != height || filteredImage.getWidth() != width) {
            throw new Exception("Image dimensions mismatch");
        }

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if(colorLocationDirectory.containsKey(sourceImg.getRGB(j, i)))
                    throw new Exception("Malformed source image. Image contains non unique pixels");
                colorLocationDirectory.put(sourceImg.getRGB(j, i), new CartesianCoordinate(j, i));
            }
        }

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                sourceCodinate = colorLocationDirectory.get(filteredImage.getRGB(j, i));
                if(sourceCodinate == null)
                    throw new Exception("New RGB valued pixel detected in filtered image.");
                mappings.add(new Pair(sourceCodinate, new CartesianCoordinate(j, i)));
            }
        }

        return mappings;
    }

}
