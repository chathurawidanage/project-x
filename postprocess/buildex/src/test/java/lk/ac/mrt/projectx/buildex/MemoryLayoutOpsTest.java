package lk.ac.mrt.projectx.buildex;

import lk.ac.mrt.projectx.buildex.files.InstructionTraceFile;
import lk.ac.mrt.projectx.buildex.models.memoryinfo.MemoryInfo;
import lk.ac.mrt.projectx.buildex.models.memoryinfo.PCMemoryRegion;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;

/**
 * @author Chathura Widanage
 */
public class MemoryLayoutOpsTest {
    @Test
    public void testCreateMemoryLayout() throws Exception {
        blurTestCreateMemoryLayout();
    }

    @Test
    public void testCreateMemoryLayoutPCMemoryRegion() throws Exception{
        blurTestCreateMemoryLayoutPCMemoryRegion();
    }

    /**
     * This method tests createMemoryLayoutMemoryInfo method for blur filter
     *
     * @throws Exception
     */
    private void blurTestCreateMemoryLayoutPCMemoryRegion() throws Exception {
        String inImage = "a.png";
        String exec = "halide_blur_hvscan_test.exe";
        InstructionTraceFile instructionTraceFile = InstructionTraceFile.filterLargestInstructionTraceFile(
                Arrays.asList(Configurations.getOutputFolderTest("blur").listFiles()),
                inImage,
                exec,
                false
        );
        List<PCMemoryRegion> pcMemoryRegions = MemoryLayoutOps.createMemoryLayoutPCMemoryRegion(instructionTraceFile, 1);
        assertEquals(pcMemoryRegions.size(), 30);

        File memoryInfoIntStr = new File(
                Configurations.getIntermediateStructuresTest("blur"),
                "PCMemoryRegions.int"
        );
        Scanner scanner = new Scanner(memoryInfoIntStr);
        String line = scanner.nextLine();
        assertEquals(pcMemoryRegions.toString(), line.trim());
    }

    /**
     * This method tests createMemoryLayoutMemoryInfo method for blur filter
     *
     * @throws Exception
     */
    private void blurTestCreateMemoryLayout() throws Exception {
        String inImage = "a.png";
        String exec = "halide_blur_hvscan_test.exe";
        InstructionTraceFile instructionTraceFile = InstructionTraceFile.filterLargestInstructionTraceFile(
                Arrays.asList(Configurations.getOutputFolderTest("blur").listFiles()),
                inImage,
                exec,
                false
        );
        List<MemoryInfo> memoryLayout = MemoryLayoutOps.createMemoryLayoutMemoryInfo(instructionTraceFile, 1);
        assertEquals(memoryLayout.size(), 10);

        File memoryInfoIntStr = new File(
                Configurations.getIntermediateStructuresTest("blur"),
                "MemoryInfo.int"
        );
        Scanner scanner = new Scanner(memoryInfoIntStr);
        String line = scanner.nextLine();
        assertEquals(memoryLayout.toString(), line.trim());
    }


}