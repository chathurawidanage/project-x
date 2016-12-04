package lk.ac.mrt.projectx.buildex;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author Chathura Widanage
 */
public class Buildex {
    private static final Logger logger = LogManager.getLogger(Buildex.class);

    public static void main(String[] args) throws IOException {
        File outputFolder = new File("generated_files_test\\output_files");//Configurations.getOutputFolder();
        File[] files = outputFolder.listFiles();


        ProjectXImage inputImage = new ProjectXImage(ImageIO.read(new File("generated_files_test\\output_files\\arith.png")));
        ProjectXImage outputImage = new ProjectXImage(ImageIO.read(new File("generated_files_test\\output_files\\aritht.png")));


        List<MemoryDumpFile> memoryDumpFileList = new ArrayList<>();
        for (File f : files) {
            try {
                MemoryDumpFile memoryDumpFile = new MemoryDumpFile(f);
                logger.info("Found memory dump file : {}", memoryDumpFile.getFile().getAbsoluteFile());
                memoryDumpFileList.add(memoryDumpFile);
            } catch (Exception e) {

            }
        }


        MemoryAnalyser memoryAnalyser = MemoryAnalyser.getInstance();
        memoryAnalyser.getImageRegion(memoryDumpFileList, inputImage, outputImage);


        System.out.println("-------");
        int[] imageBuffer = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[] reversedImageBuffer = new int[9];
        int range = imageBuffer.length / 3 - 1;
        for (int i = 0; i < imageBuffer.length / 3; i++) {
            for (int channel = 0; channel < 3; channel++) {
                reversedImageBuffer[i + (range * channel) + channel] = imageBuffer[(range * (channel + 1)) + channel - i];
            }
        }

        int[] revereImageBufferCopy = Arrays.copyOf(reversedImageBuffer, reversedImageBuffer.length);
        int channelGap = 3;
        int channelLevelIndex = 0;
        for (int i = 0; i < reversedImageBuffer.length; ) {
            reversedImageBuffer[i++] = revereImageBufferCopy[channelLevelIndex];
            reversedImageBuffer[i++] = revereImageBufferCopy[channelLevelIndex + channelGap];
            reversedImageBuffer[i++] = revereImageBufferCopy[channelLevelIndex + (2 * channelGap)];
            channelLevelIndex++;
        }
        for (int x : reversedImageBuffer) {
            System.out.print(x + ",");
        }


    }
}
