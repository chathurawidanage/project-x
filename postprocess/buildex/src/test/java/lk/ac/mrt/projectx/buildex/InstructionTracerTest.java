package lk.ac.mrt.projectx.buildex;

import lk.ac.mrt.projectx.buildex.files.InstructionTraceFile;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;


/**
 * Created by wik2kassa on 12/3/2016.
 */
public class InstructionTracerTest{

    @Test
    public void testParseDebugDisassembly() throws Exception {
     /*   InstructionTracer it = InstructionTracer.getInstance();
        List<StaticInfo> infoList =
                it.parseDebugDisassembly(InstructionTraceFile.getDisassemblyInstructionTrace("i_view32.exe", "arith.png", 0));
*/
        //it.printDissassemblyInformation(infoList, infoList.get(0).getPc());
    }

    @Test
    public void walkFileAndAdapter() throws Exception {
        String inImage = "a.png";
        String exec = "halide_blur_hvscan_test.exe";

        File outputFolder = Configurations.getOutputFolder();//new File("generated_files_test\\working\\output_files");//Configurations.getOutputFolder();
        List<File> outputFilesList = Arrays.asList( outputFolder.listFiles() );

        File filterFolder = Configurations.getFilterFolder();

        InstructionTraceFile instructionTraceFile = InstructionTraceFile.filterLargestInstructionTraceFile( outputFilesList, inImage, exec, false );
        InstructionTracer instructionTracer = InstructionTracer.getInstance();
//        instructionTracer.walkFileAndGetInstructions(instructionTraceFile,);
    }
}