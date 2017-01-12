package lk.ac.mrt.projectx.buildex;

import junit.framework.TestCase;
import lk.ac.mrt.projectx.buildex.files.AppPCFile;
import lk.ac.mrt.projectx.buildex.files.InstructionTraceFile;
import lk.ac.mrt.projectx.buildex.files.MemoryDumpFile;
import lk.ac.mrt.projectx.buildex.models.memoryinfo.MemoryInfo;
import lk.ac.mrt.projectx.buildex.models.memoryinfo.MemoryRegion;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Created by krv on 1/12/17.
 */
public class BuildexTest extends TestCase {

    public void testBuildex() throws Exception{

        String inImage = "a.png";
        String exec = "halide_blur_hvscan_test.exe";

        File outputFolder = new File("working\\output_files");
        List<File> outputFilesList = Arrays.asList(outputFolder.listFiles());

        File filterFolder =  new File("working\\filter_files");
        List<File> filterFileList = Arrays.asList(filterFolder.listFiles());

        ProjectXImage inputImage = new ProjectXImage(ImageIO.read(new File("working\\images\\a.png")));
        ProjectXImage outputImage = new ProjectXImage(ImageIO.read(new File("working\\images\\ablur.png")));

        InstructionTraceFile instructionTraceFile = InstructionTraceFile.filterLargestInstructionTraceFile(outputFilesList, inImage, exec, false);

        InstructionTraceFile disAsmFile = InstructionTraceFile.filterLargestInstructionTraceFile(outputFilesList, inImage, exec, true);

        List<MemoryDumpFile> memoryDumpFileList = MemoryDumpFile.filterMemoryDumpFiles(outputFilesList, exec);

        AppPCFile appPCFile = AppPCFile.filterAppPCFile(filterFileList, exec);

        MemoryAnalyser memoryAnalyser = MemoryAnalyser.getInstance();
        List<MemoryRegion> imageRegions = memoryAnalyser.getImageRegions(memoryDumpFileList, inputImage, outputImage);

        List<MemoryInfo> memoryLayout = MemoryLayoutOps.createMemoryLayout(instructionTraceFile, 1);

    }
}