package lk.ac.mrt.projectx.preprocess;

import junit.framework.TestCase;

/**
 * Created by krv on 12/23/2016.
 */
public class ModuleTest extends TestCase {
    public void testGetName() throws Exception {
        Module mod = new Module();
        mod.LoadByDRCovModuleLine("11, 40960, C:\\Windowow64\\LPK.dll");
        assertEquals(mod.getName(), "C:\\Windowow64\\LPK.dll");
    }

    public void testGetAddresses() throws Exception {
        Module mod = new Module();
        mod.LoadByDRCovModuleLine("11, 40960, C:\\Windowow64\\LPK.dll");
        assertEquals(11, mod.getId().intValue());
    }

}