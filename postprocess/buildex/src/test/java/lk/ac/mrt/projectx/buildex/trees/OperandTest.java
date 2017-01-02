package lk.ac.mrt.projectx.buildex.trees;

import junit.framework.TestCase;

/**
 * Created by krv on 1/1/17.
 */
public class OperandTest extends TestCase {

    public void testConstructorInteger() {
        Operand<Integer> operand = new Operand<>(Operand.OperandType.IMM_INT_TYPE, 50, 160);

        assertEquals((Integer)50, operand.value);
    }

    public void testConstructorFloat() {
        Operand<Float> operand = new Operand<>(Operand.OperandType.IMM_FLOAT_TYPE, 50.0f, 160);

        assertEquals(50.0f, operand.value);
    }


    public void testMemRangeToRegName() {
        Operand<Integer> operand = new Operand<>();
        operand.type = Operand.OperandType.REG_TYPE;
        operand.value = 3 * 32;
        String string = operand.getRegName();
        assertEquals("rbx", string);
    }
}