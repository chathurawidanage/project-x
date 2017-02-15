package lk.ac.mrt.projectx.buildex.complex;

import lk.ac.mrt.projectx.buildex.complex.filters.FishEyeJava;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * @author Chathura Widanage
 */
public class FishEyeJavaTest {
    @Test
    public void filter() throws Exception {
        BufferedImage read = ImageIO.read(new File("D:\\test\\in.jpg"));
        BufferedImage out=new BufferedImage(read.getWidth(),read.getHeight(),read.getType());
        FishEyeJava fishEyeJava=new FishEyeJava();
        fishEyeJava.filter(read,out);
        ImageIO.write(out,"JPG",new File("D:\\test\\out-fish-gen.jpg"));
    }

    @Test
    public void filterCartesian() throws Exception {
        BufferedImage read = ImageIO.read(new File("D:\\test\\in.jpg"));
        BufferedImage out=new BufferedImage(read.getWidth(),read.getHeight(),read.getType());
        FishEyeJava fishEyeJava=new FishEyeJava();
        fishEyeJava.filterCartesian(read,out);
        ImageIO.write(out,"JPG",new File("D:\\test\\out-fish-cart.jpg"));
    }

}