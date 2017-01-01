package lk.ac.mrt.projectx.buildex.trees;

import lk.ac.mrt.projectx.buildex.X86Analysis;

import static lk.ac.mrt.projectx.buildex.X86Analysis.MAX_SIZE_OF_REG;

/**
 * Created by krv on 12/4/2016.
 */
//TODO : index sort comparable (check output.h line 24-33), this may be a linked list
//TODO : DUPLICATE CLASS IN InstructionTraceUnit
public class Operand<T> implements Comparable {

    public static enum OperandType {
        REG_TYPE,
        MEM_STACK_TYPE,
        MEM_HEAP_TYPE,
        IMM_FLOAT_TYPE,
        IMM_INT_TYPE,
        DEFAULT_TYPE;
    }

    OperandType type = null;
    Long width = null;
    T value = null;
    Operand addr = null;

    @Override
    public int compareTo(Object other) {
        if (!(other instanceof Operand))
            throw new ClassCastException("A Operand object expected.");
        Float f1 = (float) this.value;
        Float f2 = (float) ((Operand) other).value;
        return Float.compare(f1, f2);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if(type == OperandType.REG_TYPE){
            int offset = (int)value - ((int)value / MAX_SIZE_OF_REG) * MAX_SIZE_OF_REG;
            stringBuilder.append(((Integer) value).toString());
            stringBuilder.append(":r[");
            stringBuilder.append(":r[");

        }
        return super.toString();
    }
}

