package lk.ac.mrt.projectx.buildex.trees;

import junit.framework.TestCase;
import lk.ac.mrt.projectx.buildex.models.memoryinfo.MemoryInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by krv on 2/8/17.
 */
public class MemoryRegionUtilsTest extends TestCase {

    public void testMergeInstraceAndDumpRegions() throws Exception {
        List<MemoryInfo> test = new ArrayList<>();
        MemoryInfo mm0 = new MemoryInfo();
        MemoryInfo mm1 = new MemoryInfo();
        MemoryInfo mm2 = new MemoryInfo();
        MemoryInfo mm3 = new MemoryInfo();
        MemoryInfo mm4 = new MemoryInfo();
        MemoryInfo mm5 = new MemoryInfo();
        test.add( mm0 );
        test.add( mm1 );
        test.add( mm2 );
        test.add( mm3 );
        test.add( mm4 );
        test.add( mm5 );
        MemoryRegionUtils.mergeInstraceAndDumpRegions( null, test, null );

    }

}