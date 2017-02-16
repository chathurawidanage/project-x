package lk.ac.mrt.projectx.buildex.complex.filters;

import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

import static org.junit.Assert.*;

/**
 * @author Chathura Widanage
 */
public class CylinderFilterTest {
    @Test
    public void filter() throws Exception {
        BufferedImage read = ImageIO.read(new File("D:\\test\\in.jpg"));
        BufferedImage out=new BufferedImage(read.getWidth(),read.getHeight(),read.getType());
        CylinderFilter cylinderFilter=new CylinderFilter();
        cylinderFilter.filter(read,out);
        ImageIO.write(out,"JPG",new File("D:\\test\\outcart-cylinder-gen.jpg"));
    }

    @Test
    public void filterCartesian() throws Exception {
        BufferedImage read = ImageIO.read(new File("D:\\test\\in.jpg"));
        BufferedImage out=new BufferedImage(read.getWidth(),read.getHeight(),read.getType());
        CylinderFilter cylinderFilter=new CylinderFilter();
        cylinderFilter.filterCartesian(read,out);
        ImageIO.write(out,"JPG",new File("D:\\test\\outcart-cylinder.jpg"));
    }

}