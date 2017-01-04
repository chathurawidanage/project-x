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
        File outputFolder = new File("generated_files_test\\working\\output_files");//Configurations.getOutputFolder();
        File[] files = outputFolder.listFiles();
        ProjectXImage inputImage = new ProjectXImage(ImageIO.read(new File("generated_files_test\\working\\images\\a.png")));
        ProjectXImage outputImage = new ProjectXImage(ImageIO.read(new File("generated_files_test\\working\\images\\ablur.png")));


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
        for (int i = 0; i < imageBuffer.length; i += 3) {
            int swapPosition = (3*3) - i - 3;
            System.arraycopy(imageBuffer,i,reversedImageBuffer,swapPosition,3);
        }

       /* int[] revereImageBufferCopy = Arrays.copyOf(reversedImageBuffer, reversedImageBuffer.length);
        int channelGap = 3;
        int channelLevelIndex = 0;
        for (int i = 0; i < reversedImageBuffer.length; ) {
            reversedImageBuffer[i++] = revereImageBufferCopy[channelLevelIndex];
            reversedImageBuffer[i++] = revereImageBufferCopy[channelLevelIndex + channelGap];
            reversedImageBuffer[i++] = revereImageBufferCopy[channelLevelIndex + (2 * channelGap)];
            channelLevelIndex++;
        }*/
        for (int x : reversedImageBuffer) {
            System.out.print(x + ",");
        }


    }
}
