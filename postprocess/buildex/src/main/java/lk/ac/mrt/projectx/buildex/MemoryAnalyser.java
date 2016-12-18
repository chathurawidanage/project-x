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
 * @see https://msdn.microsoft.com/en-us/library/windows/desktop/aa473780(v=vs.85).aspx
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
            //throw new Exception("Input and output images are equal");
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
        int[] imageBuffer = image.getImageBuffer(ProjectXImage.BufferLayout.PLANAR);
        int[] reversedImageBuffer = new int[imageBuffer.length];

        //this is just for PLANAR
        for (int i = 0; i < imageBuffer.length; i += image.getImage().getWidth()) {
            int swapPosition = (image.getImage().getWidth()*image.getImage().getHeight()*3) - i - image.getImage().getWidth();
            System.arraycopy(imageBuffer,i,reversedImageBuffer,swapPosition,image.getImage().getWidth());
        }
     /*   if (true) {
            int[] revereImageBufferCopy = Arrays.copyOf(reversedImageBuffer, reversedImageBuffer.length);
            int channelGap = (image.getImage().getWidth() * image.getImage().getHeight());
            int channelLevelIndex = 0;
            for (int i = 0; i < reversedImageBuffer.length; ) {
                reversedImageBuffer[i++] = revereImageBufferCopy[channelLevelIndex];
                reversedImageBuffer[i++] = revereImageBufferCopy[channelLevelIndex + channelGap];
                reversedImageBuffer[i++] = revereImageBufferCopy[channelLevelIndex + (2 * channelGap)];
                channelLevelIndex++;
            }
        }*/

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
                image.getImageBuffer(ProjectXImage.BufferLayout.PLANAR),
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
                    logger.info("Scanned whole image {}",startPoints.size());
                    return startPoints;
                }
            } else {
                j++;
            }
        }
        System.out.println(startPoints.size());
        return null;
    }

    private boolean isEqualImages(ProjectXImage i1, ProjectXImage i2) {
        BufferedImage image1 = i1.getImage();
        BufferedImage image2 = i2.getImage();
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
