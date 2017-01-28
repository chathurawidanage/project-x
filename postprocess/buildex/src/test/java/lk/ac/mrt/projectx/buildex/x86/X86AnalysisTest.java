package lk.ac.mrt.projectx.buildex.x86;

import junit.framework.TestCase;
import lk.ac.mrt.projectx.buildex.DefinesDotH;

/**
 * Created by krv on 1/28/17.
 */
public class X86AnalysisTest extends TestCase {

    public void testCheckEFlagBit() throws Exception {
        DefinesDotH.OpCodes op = DefinesDotH.OpCodes.values()[ 156 ];
        Boolean cf = X86Analysis.checkEFlagBit( X86Analysis.EflagBits.Carry_Flag, 1367867392L );
        Boolean zf = X86Analysis.checkEFlagBit( X86Analysis.EflagBits.Zero_Flag, 1367867392L );
        Boolean of = X86Analysis.checkEFlagBit( X86Analysis.EflagBits.Overflow_Flag, 1367867392L );
        Boolean af = X86Analysis.checkEFlagBit( X86Analysis.EflagBits.Auxiliary_Carry_Flag, 1367867392L );
        Boolean pf = X86Analysis.checkEFlagBit( X86Analysis.EflagBits.Parity_Flag, 1367867392L );
        Boolean sf = X86Analysis.checkEFlagBit( X86Analysis.EflagBits.Sign_Flag, 1367867392L );

        assertTrue( cf );
        assertTrue( zf );
        assertTrue( of );
        assertTrue( pf );
        assertTrue( sf );
        assertFalse( af );
    }

}