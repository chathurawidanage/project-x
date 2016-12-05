package lk.ac.mrt.projectx.preprocess;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import lk.ac.mrt.projectx.buildex.ProjectXImage;

import javax.imageio.ImageIO;

/**
 * Created by Lasantha on 02-Dec-16.
 */

public class MainTest {
    private static final Logger logger = LogManager.getLogger(MainTest.class);

    public static void main(String[] args) {
        MainTest mainTest = new MainTest();
        try {
            mainTest.runAlgorithmDiffMode();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MainTest() {
        this.outputFolderPath = "E:\\FYP\\Java Ported\\Test Files\\output_files";
        this.imageFolderPath = "E:\\FYP\\Java Ported\\Test Files\\images";
        this.inImageFileName = "arith.png";
        this.outImageFileName = "aritht.png";
        this.exeFileName = "halide_threshold_test.exe";
        this.threshold = 80;
        this.filterMode = 1;
        this.bufferSize = 0;
        this.profileData = new ArrayList<byte[]>();
        this.memtraceData = new ArrayList<byte[]>();

        initialize();
    }

    public void initialize() {
        readMemtraceAndProfileFiles();
    }

    private final int DIFF_MODE = 1;
    private final int TWO_IMAGE_MODE = 2;
    private final int ONE_IMAGE_MODE = 3;

    private String outputFolderPath;
    private String imageFolderPath;
    private String inImageFileName;
    private String outImageFileName;
    private String exeFileName;
    private int filterMode;
    private int bufferSize;
    private int threshold;  // continuous chunck % of image
    private ArrayList<byte[]> profileData;
    private ArrayList<byte[]> memtraceData;


    private void readMemtraceAndProfileFiles() {

        String profileFileNameFormat = "profile_" + exeFileName + "_" + inImageFileName;
        String memtraceFileNameFormat = "memtrace_" + exeFileName + "_" + inImageFileName;

        // go through all the files in the output folder to find matches for profile and memtrace
        File folder = new File(outputFolderPath);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                String fileName = listOfFiles[i].getName();

                byte[] data;
                if (fileName.contains(profileFileNameFormat)) {
                    // read profile files
                    data = getFileContent(listOfFiles[i].getPath());
                    profileData.add(data);
                } else if (fileName.contains(memtraceFileNameFormat)) {
                    // read memtrace files
                    data = getFileContent(listOfFiles[i].getPath());
                    memtraceData.add(data);
                }
            }
        }

    }

    public void runAlgorithmDiffMode() throws IOException {

        logger.info("Filter function DIFF MODE");

        ModuleInfo module = ModuleInfo.getPopulatedModuleInfo(profileData.get(0));

        ProjectXImage inImage = new ProjectXImage(ImageIO.read(new File(imageFolderPath+"\\"+inImageFileName)));
        logger.info("Input Image Read Done! - {}",imageFolderPath+"\\"+inImageFileName);

        ProjectXImage outImage = new ProjectXImage(ImageIO.read(new File(imageFolderPath+"\\"+outImageFileName)));
        logger.info("Output Image Read Done! - {}",imageFolderPath+"\\"+outImageFileName);



    }


    private byte[] getFileContent(String filename) {
        byte[] data = null;
        logger.info("Reading file {}", filename);
        try {
            data = Files.readAllBytes(Paths.get(filename));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return data;
    }

}
