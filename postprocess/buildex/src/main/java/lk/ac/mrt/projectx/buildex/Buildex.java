package lk.ac.mrt.projectx.buildex;

import lk.ac.mrt.projectx.buildex.exceptions.NoSuitableFileFoundException;
import lk.ac.mrt.projectx.buildex.files.AppPCFile;
import lk.ac.mrt.projectx.buildex.files.InstructionTraceFile;
import lk.ac.mrt.projectx.buildex.files.MemoryDumpFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


/**
 * @author Chathura Widanage
 */
public class Buildex {
    private static final Logger logger = LogManager.getLogger(Buildex.class);

    public static void main(String[] args) throws IOException, NoSuitableFileFoundException {
        String inImage = "a.png";
        String exec = "halide_blur_hvscan_test.exe";

        File outputFolder = Configurations.getOutputFolder();//new File("generated_files_test\\working\\output_files");//Configurations.getOutputFolder();
        List<File> outputFilesList = Arrays.asList(outputFolder.listFiles());

        File filterFolder = Configurations.getFilterFolder();
        List<File> filterFileList = Arrays.asList(filterFolder.listFiles());

        ProjectXImage inputImage = new ProjectXImage(ImageIO.read(new File("generated_files_test\\working\\images\\a.png")));
        ProjectXImage outputImage = new ProjectXImage(ImageIO.read(new File("generated_files_test\\working\\images\\ablur.png")));

        InstructionTraceFile instructionTraceFile = InstructionTraceFile.filterLargestInstructionTraceFile(outputFilesList, inImage, exec, false);
        logger.info("Found instrace file {}", instructionTraceFile.getName());

        InstructionTraceFile disAsmFile = InstructionTraceFile.filterLargestInstructionTraceFile(outputFilesList, inImage, exec, true);
        logger.info("Found memory dump file {}", disAsmFile.getName());

        List<MemoryDumpFile> memoryDumpFileList = MemoryDumpFile.filterMemoryDumpFiles(outputFilesList,exec);
        logger.info("Found {} memory dump files {}", memoryDumpFileList.size(), memoryDumpFileList.toString());

        AppPCFile appPCFile = AppPCFile.filterAppPCFile(filterFileList,exec);
        logger.info("Found app pc file {}", appPCFile.toString());

        MemoryAnalyser memoryAnalyser = MemoryAnalyser.getInstance();
        List<MemoryRegion> imageRegions = memoryAnalyser.getImageRegions(memoryDumpFileList, inputImage, outputImage);
        logger.info("Found {} image regions", imageRegions.size());
    }
}
