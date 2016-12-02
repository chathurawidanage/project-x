package lk.ac.mrt.projectx.buildex;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.util.Hashtable;

/**
 * @author Chathura Widanage
 */
public class ProjectXImage {
    private BufferedImage bufferedImage;
    private int[] imageBuffer;

    public ProjectXImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

    public BufferedImage getImage() {
        return bufferedImage;
    }

    public int[] getImageBuffer() {
        if (this.imageBuffer != null) {
            return imageBuffer;
        }
        int x = this.bufferedImage.getColorModel().getPixelSize();
        System.out.println("Pixel size " + x);
        imageBuffer = new int[this.bufferedImage.getWidth() * this.bufferedImage.getHeight() * 3];
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        //storing channels separately
        for (int i = 0; i < this.bufferedImage.getWidth(); i++) {
            for (int j = 0; j < this.bufferedImage.getHeight(); j++) {
                Color c = new Color(this.bufferedImage.getRGB(i, j));
                imageBuffer[(0 * height + j) * width + i] = c.getRed();
                imageBuffer[(1 * height + j) * width + i] = c.getGreen();
                imageBuffer[(2 * height + j) * width + i] = c.getBlue();
            }
        }
        return imageBuffer;
    }
}
