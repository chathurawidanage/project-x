package lk.ac.mrt.projectx.buildex;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
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
        File outputFolder = Configurations.getOutputFolder();
        File[] files = outputFolder.listFiles();


        ProjectXImage inputImage = new ProjectXImage(ImageIO.read(new File("F:\\engineering\\fyp\\gens\\generated_files\\output_files\\arith.png")));
        ProjectXImage outputImage = new ProjectXImage(ImageIO.read(new File("F:\\engineering\\fyp\\gens\\generated_files\\output_files\\aritht.png")));


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
        byte[] imageBuffer = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        byte[] reversedImageBuffer = new byte[9];
        int range = imageBuffer.length / 3 - 1;
        for (int i = 0; i < imageBuffer.length / 3; i++) {//todo wrong reversion
            for (int channel = 0; channel < 3; channel++) {
                reversedImageBuffer[i + (range * channel)+channel] = imageBuffer[(range * (channel + 1)) + channel - i];
            }
        }
        for (byte x : reversedImageBuffer) {
            System.out.print(x + ",");
        }


    }
}
