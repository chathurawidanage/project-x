package lk.ac.mrt.projectx.buildex;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
                List<Integer> startPoints = forwardAnalysis(memoryDumpFile, inputImage);
                System.out.println(startPoints);
                if (startPoints == null)
                    backwardAnalysis(memoryDumpFile, inputImage, false);
            } else {
                List<Integer> startPoints = forwardAnalysis(memoryDumpFile, outputImage);
                System.out.println(startPoints);
                if (startPoints == null)
                    backwardAnalysis(memoryDumpFile, outputImage, true);
            }
        }
    }

    //todo invalid implementation
    private List<Integer> backwardAnalysis(MemoryDumpFile memoryDumpFile, ProjectXImage image, boolean write) throws IOException {
        logger.info("Backward analyzing {}", memoryDumpFile.getFile().getName());
        int[] imageBuffer = image.getImageBuffer();
        int[] reversedImageBuffer = new int[imageBuffer.length];

        int range = imageBuffer.length / 3 - 1;
        for (int i = 0; i < imageBuffer.length / 3; i++) {
            for (int channel = 0; channel < 3; channel++) {
                reversedImageBuffer[i + (range * channel) + channel] = imageBuffer[(range * (channel + 1)) + channel - i];
            }
        }

        if (true) {
            int[] revereImageBufferCopy = Arrays.copyOf(reversedImageBuffer, reversedImageBuffer.length);
            int channelGap = (image.getImage().getWidth() * image.getImage().getHeight());
            int channelLevelIndex = 0;
            for (int i = 0; i < reversedImageBuffer.length; ) {
                reversedImageBuffer[i++] = revereImageBufferCopy[channelLevelIndex];
                reversedImageBuffer[i++] = revereImageBufferCopy[channelLevelIndex + channelGap];
                reversedImageBuffer[i++] = revereImageBufferCopy[channelLevelIndex + (2 * channelGap)];
                channelLevelIndex++;
            }
        }

        return findRegions(image.getImage().getWidth(),
                image.getImage().getHeight(),
                reversedImageBuffer,
                memoryDumpFile.getBaseProgramCounter(),
                memoryDumpFile.getMemoryBuffer()
        );
    }

    private List<Integer> forwardAnalysis(MemoryDumpFile memoryDumpFile, ProjectXImage image) throws IOException {
        logger.info("Forward analyzing {}", memoryDumpFile.getFile().getName());
        return findRegions(image.getImage().getWidth(),
                image.getImage().getHeight(),
                image.getImageBuffer(),
                memoryDumpFile.getBaseProgramCounter(),
                memoryDumpFile.getMemoryBuffer()
        );
    }

    private List<Integer> findRegions(int imageWidth, int imageHeight, int[] imageBuffer, long basePC, byte[] memoryBuffer) {
        ArrayList<Integer> startPoints = new ArrayList<>();
        int last = 0;
        for (int j = 0; j < memoryBuffer.length; ) {
            boolean found = true;
            for (int k = last; k < last + (imageWidth); k++) {
                if (j + k - last < memoryBuffer.length && (memoryBuffer[j + k - last] & 0xff) != imageBuffer[k]) {
                    found = false;
                    break;
                }
            }
            if (found) {
                last += (imageWidth);
                startPoints.add(j);
                j += (imageWidth - 1);
                if (last == imageWidth * imageHeight * 3) {//todo multiply by 3??
                    logger.info("Scanned whole image");
                    return startPoints;
                }
            } else {
                j++;
            }
        }
        return null;
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
