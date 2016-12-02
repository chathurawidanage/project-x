package lk.ac.mrt.projectx.buildex;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
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


    public void getImageRegion(List<MemoryDumpFile> memoryDumpFiles, ProjectXImage inputImage, ProjectXImage outputImage) throws IOException {
        /*if (this.isEqualImages(inputImage, outputImage)) {
            logger.error("Input and output images are equal");
            throw new Exception("Input and output images are equal");
        }*/

        for (MemoryDumpFile memoryDumpFile : memoryDumpFiles) {
            logger.info("Analyzing file : {}", memoryDumpFile.toString());
            if (!memoryDumpFile.isWrite()) {
                System.out.println("Write file");
                forwardAnalysis(memoryDumpFile, inputImage);
                backwardAnalysis(memoryDumpFile, inputImage);
            } else {
                forwardAnalysis(memoryDumpFile, outputImage);
                backwardAnalysis(memoryDumpFile, outputImage);
            }
        }
    }

    private void backwardAnalysis(MemoryDumpFile memoryDumpFile, ProjectXImage image) throws IOException {
        System.out.println("Backward");
        int[] imageBuffer = image.getImageBuffer();
        int[] reversedImageBuffer = new int[imageBuffer.length];

        int range = imageBuffer.length / 3 - 1;
        for (int i = 0; i < imageBuffer.length / 3; i++) {
            for (int channel = 0; channel < 3; channel++) {
                reversedImageBuffer[i + (range * channel)+channel] = imageBuffer[(range * (channel + 1)) + channel - i];
            }
        }

        findRegions(image.getImage().getWidth(),
                image.getImage().getHeight(),
                reversedImageBuffer,
                memoryDumpFile.getBaseProgramCounter(),
                memoryDumpFile.getMemoryBuffer()
        );
        System.out.println("backward done----------------");
    }

    private void forwardAnalysis(MemoryDumpFile memoryDumpFile, ProjectXImage image) throws IOException {
        System.out.println("Forward");
        findRegions(image.getImage().getWidth(),
                image.getImage().getHeight(),
                image.getImageBuffer(),
                memoryDumpFile.getBaseProgramCounter(),
                memoryDumpFile.getMemoryBuffer()
        );
        System.out.println("Forward done----------------");
    }

    private void findRegions(int imageWidth, int imageHeight, int[] imageBuffer, long basePC, byte[] memoryBuffer) {
        for (int i = 0; i < memoryBuffer.length; i++) {
            boolean found = true;
            for (int j = 0; j < imageWidth; j++) {
                if (i + j < memoryBuffer.length && (memoryBuffer[i + j]) != imageBuffer[j]) {
                    found = false;
                    break;
                }
            }
            if (found) {
                //System.out.println("PC"+basePC);
                //System.out.println(i + " Found Forward " + (i + basePC));
                int start = 0;
                int last = 0;
            /* get the starting points of each image line */
                for (int j = i; j < memoryBuffer.length; j++) {
                    found = true;
                    for (int k = last; k < last + (imageWidth); k++) {
                        if (j + k - last < memoryBuffer.length && (memoryBuffer[j + k - last]) != imageBuffer[k]) {
                            found = false;
                            break;
                        }
                    }
                    if (found) {
                        start = j;
                        last += (imageWidth);
                        System.out.println("Found again" + (j + basePC));
                        if (last == imageWidth * imageHeight * 3) {
                            System.out.println("Found all");
                            break;
                        }
                    }
                }
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
