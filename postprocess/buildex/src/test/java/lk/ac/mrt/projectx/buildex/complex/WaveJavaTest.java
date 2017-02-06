package lk.ac.mrt.projectx.buildex.complex;

import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

import static org.junit.Assert.*;

/**
 * @author Chathura Widanage
 */
public class WaveJavaTest {
    @Test
    public void filter() throws Exception {
        BufferedImage read = ImageIO.read(new File("D:\\test\\in.jpg"));
        BufferedImage out=new BufferedImage(read.getWidth(),read.getHeight(),read.getType());
        WaveJava waveJava=new WaveJava();
        waveJava.filter(read,out);
        ImageIO.write(out,"JPG",new File("D:\\test\\outcart-wave-generated.jpg"));
    }

    @Test
    public void filterCartesian() throws Exception {
        BufferedImage read = ImageIO.read(new File("D:\\test\\in.jpg"));
        BufferedImage out=new BufferedImage(read.getWidth(),read.getHeight(),read.getType());
        WaveJava waveJava=new WaveJava();
        waveJava.filterCartesian(read,out);
        ImageIO.write(out,"JPG",new File("D:\\test\\outcart-wave.jpg"));
    }

}