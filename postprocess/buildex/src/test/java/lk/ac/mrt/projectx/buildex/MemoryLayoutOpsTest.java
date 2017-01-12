package lk.ac.mrt.projectx.buildex;

import junit.framework.TestCase;
import lk.ac.mrt.projectx.buildex.files.InstructionTraceFile;
import lk.ac.mrt.projectx.buildex.models.memoryinfo.MemoryInfo;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * @author Chathura Widanage
 */
public class MemoryLayoutOpsTest extends TestCase {
    public void testCreateMemoryLayout() throws Exception {
        blurTestCreateMemoryLayou();
    }

    /**
     * This method tests createMemoryLayout method for blur filter
     *
     * @throws Exception
     */
    private void blurTestCreateMemoryLayou() throws Exception {
        String inImage = "a.png";
        String exec = "halide_blur_hvscan_test.exe";
        InstructionTraceFile instructionTraceFile = InstructionTraceFile.filterLargestInstructionTraceFile(
                Arrays.asList(Configurations.getOutputFolderTest("blur").listFiles()),
                inImage,
                exec,
                false
        );
        List<MemoryInfo> memoryLayout = MemoryLayoutOps.createMemoryLayout(instructionTraceFile, 1);
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