package lk.ac.mrt.projectx.buildex;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Chathura Widanage
 */
public class Buildex {
    private static final Logger logger = LogManager.getLogger(Buildex.class);

    public static void main(String[] args) throws IOException {
        File outputFolder = Configurations.getOutputFolder();
        File[] files = outputFolder.listFiles();


        BufferedImage inputImage = ImageIO.read(new File("F:\\engineering\\fyp\\gens\\generated_files\\output_files\\arith.png"));
        BufferedImage outputImage = ImageIO.read(new File("F:\\engineering\\fyp\\gens\\generated_files\\output_files\\aritht.png"));


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


    }
}
