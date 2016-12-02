package lk.ac.mrt.projectx.buildex;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * @author Chathura Widanage
 */
public class MemoryAnalyser {
    private static final Logger logger = LogManager.getLogger(MemoryAnalyser.class);

    private static MemoryAnalyser memoryAnalyser = new MemoryAnalyser();

    public static MemoryAnalyser getInstance() {
        return memoryAnalyser;
    }

    private MemoryAnalyser() {

    }


    public void getImageRegion(List<MemoryDumpFile> memoryDumpFiles, BufferedImage inputImage, BufferedImage outputImage) throws IOException {
        /*if (this.isEqualImages(inputImage, outputImage)) {
            logger.error("Input and output images are equal");
            throw new Exception("Input and output images are equal");
        }*/

        for (MemoryDumpFile memoryDumpFile : memoryDumpFiles) {
            logger.info("Analyzing file : {}", memoryDumpFile.toString());
            forwardAnalysis(memoryDumpFile,inputImage);
        }
    }

    private void forwardAnalysis(MemoryDumpFile memoryDumpFile,BufferedImage image) throws IOException {
        BufferedReader bufferedReader=new BufferedReader(new FileReader(memoryDumpFile.getFile()));
        DataBuffer dataBuffer = image.getData().getDataBuffer();
        int read;
        while((read=bufferedReader.read())!=-1){
            if((read & 0xff)==dataBuffer.getElem(0)){
                System.out.println("found");
                break;
            }
        }
    }

    private boolean isEqualImages(BufferedImage image1, BufferedImage image2) {
        if (image1.getWidth() != image2.getWidth() || image1.getHeight() != image2.getHeight()) {
            return false;
        }
        for (int x = 1; x < image2.getWidth(); x++) {
            for (int y = 1; y < image2.getHeight(); y++) {
                if (image1.getRGB(x, y) != image2.getRGB(x, y)) {
                    return false;
                }
            }
        }
        return true;
    }
}
