package lk.ac.mrt.projectx.buildex.trees;

import junit.framework.TestCase;

/**
 * Created by krv on 1/1/17.
 */
public class OperandTest extends TestCase {

    public void testMemRangeToRegName() {
        Operand<Integer> operand = new Operand<>();
        operand.type = Operand.OperandType.REG_TYPE;
        operand.value = 3 * 32;
        String string = operand.getRegName();
        assertEquals("rbx", string);
    }
}