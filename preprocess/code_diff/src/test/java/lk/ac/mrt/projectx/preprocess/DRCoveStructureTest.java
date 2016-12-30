package lk.ac.mrt.projectx.preprocess;

import junit.framework.TestCase;

/**
 * Created by krv on 12/23/2016.
 */
public class DRCoveStructureTest extends TestCase {
    public void testLoadFromFile() throws Exception {
        DRCoveStructure drCoveStructure = new DRCoveStructure("halide_threshold_test.exe");
        drCoveStructure.LoadFromFile("output_files/drcov.halide_threshold_test.exe.02240.0000.proc.log");
        assertEquals(drCoveStructure.drcovVersion.intValue(), 1);
        assertEquals(drCoveStructure.noOfModules.intValue(), 24);

    }

}