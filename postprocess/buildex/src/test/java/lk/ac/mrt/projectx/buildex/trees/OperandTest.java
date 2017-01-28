package lk.ac.mrt.projectx.buildex.trees;

import junit.framework.TestCase;
import lk.ac.mrt.projectx.buildex.models.output.MemoryType;
import lk.ac.mrt.projectx.buildex.models.output.Operand;

/**
 * Created by krv on 1/1/17.
 */
public class OperandTest extends TestCase {

    public void testConstructorInteger() {
        Operand operand = new Operand( MemoryType.IMM_INT_TYPE, 160, 50 );

        assertEquals( 50, operand.getValue() );
    }

    public void testConstructorFloat() {
        Operand operand = new Operand( MemoryType.IMM_FLOAT_TYPE, 160, 50.0f );

        assertEquals( 50.0f, operand.getValue() );
    }


    public void testMemRangeToRegName() {
        Operand operand = new Operand();
        operand.setType( MemoryType.REG_TYPE );
        operand.setValue( 3 * 32 );
        String string = operand.getRegName();
        assertEquals( "rbx", string );
    }

    public void testRegToMemRange() {
        Operand operand = new Operand();
        operand.setType( MemoryType.REG_TYPE );
        operand.setWidth( 4 );
        operand.setValue( 17 );
        operand.regToMemRange();
        assertEquals( 28, operand.getValue().intValue() );
    }
}