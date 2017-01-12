package lk.ac.mrt.projectx.buildex;

import junit.framework.TestCase;
import lk.ac.mrt.projectx.buildex.files.MemoryDumpFile;
import lk.ac.mrt.projectx.buildex.models.memoryinfo.MemoryRegion;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * @author Chathura Widanage
 */
public class MemoryAnalyserTest extends TestCase {
    public void testGetImageRegions() throws Exception {
        blurTestGetImageRegions();
    }

    public void blurTestGetImageRegions() throws Exception {
        ProjectXImage inputImage = new ProjectXImage(ImageIO.read(new File(Configurations.getImagesFolderTest(),"a.png")));
        ProjectXImage outputImage = new ProjectXImage(ImageIO.read(new File(Configurations.getImagesFolderTest(),"ablur.png")));


        String exec = "halide_blur_hvscan_test.exe";
        File outputFolder = Configurations.getOutputFolderTest("blur");
        List<MemoryDumpFile> memoryDumpFileList = MemoryDumpFile.filterMemoryDumpFiles(Arrays.asList(outputFolder.listFiles()), exec);
        List<MemoryRegion> imageRegions = MemoryAnalyser.getInstance().getImageRegions(memoryDumpFileList, inputImage, outputImage);

        assertEquals(imageRegions.size(),4);

        File memoryInfoIntStr = new File(
                Configurations.getIntermediateStructuresTest("blur"),
                "ImageRegions.int"
        );
        Scanner scanner = new Scanner(memoryInfoIntStr);
        String line = scanner.nextLine();
        assertEquals(imageRegions.toString(), line.trim());
    }

}