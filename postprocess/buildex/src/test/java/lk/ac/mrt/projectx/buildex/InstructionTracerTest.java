package lk.ac.mrt.projectx.buildex;

import junit.framework.TestCase;
import lk.ac.mrt.projectx.buildex.files.InstructionTraceFile;

import java.util.List;

/**
 * Created by wik2kassa on 12/3/2016.
 */
public class InstructionTracerTest extends TestCase {

    public void testParseDebugDisassembly() throws Exception {
        InstructionTracer it = InstructionTracer.getInstance();
        List<StaticInfo> infoList =
                it.parseDebugDisassembly(InstructionTraceFile.getDisassemblyInstructionTrace("i_view32.exe", "arith.png", 0));

        //it.printDissassemblyInformation(infoList, infoList.get(0).getPc());
    }
}