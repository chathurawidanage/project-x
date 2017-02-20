package lk.ac.mrt.projectx.buildex.complex.langs;

import lk.ac.mrt.projectx.buildex.complex.CoordinateTransformer;
import lk.ac.mrt.projectx.buildex.complex.cordinates.CartesianCoordinate;
import lk.ac.mrt.projectx.buildex.complex.cordinates.PolarCoordinate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class JavaGen {
    public static void main(String[] args) throws IOException {
        BufferedImage read = ImageIO.read(new File(args[0]));
        BufferedImage out = new BufferedImage(read.getWidth(), read.getHeight(), read.getType());
        ImageIO.write(out,"JPG",new File(".","out.jpg"));    }

    public static void filter(BufferedImage in, BufferedImage out) {
        int width = in.getWidth();
        int height = in.getHeight();
        for (int i = 0; i < in.getWidth(); i++) {
            for (int j = 0; j < in.getHeight(); j++) {
                int sx = i - (width / 2);
                int sy = i - (height / 2);

                double r_in = Math.hypot(sy, sx);
                double theta_in = Math.atan2(sy, sx);

                //double thetaNew = Math.sqrt(((Math.pow((height)/((Math.hypot(width/2,height/2))),2))*Math.pow(r_in,2))+((Math.pow((width)/((4)*(Math.PI)),2))*Math.pow(theta_in,2)));
                //double rNew = Math.sqrt((1.439f*Math.pow(r_in,2))+(4052.847f*Math.pow(theta_in,2)));
                double thetaNew = Math.atan(((Math.sqrt(((Math.hypot(width/2,height/2)))/(height*(Math.PI)*width)))*(r_in/theta_in)));
                double rNew = Math.atan((0.018f*(r_in/theta_in)));

                int tx = (int) (rNew * Math.cos(thetaNew));
                int ty = (int) (rNew * Math.sin(thetaNew));

                if (clampPass(width, height, tx, ty))
                    out.setRGB(i, j, in.getRGB(tx, ty));
            }
        }
    }

    private static boolean clampPass(int width, int height, int x, int y) {
        return x >= 0 && x <= width - 1 && y >= 0 && y <= height - 1;
    }
}
