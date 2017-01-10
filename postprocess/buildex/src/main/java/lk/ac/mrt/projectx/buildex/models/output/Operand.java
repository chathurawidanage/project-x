package lk.ac.mrt.projectx.buildex.models.output;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chathura Widanage
 */
public class Operand {
    public static final int REG_TYPE = 0;
    public static final int MEM_STACK_TYPE = 1;
    public static final int MEM_HEAP_TYPE = 2;
    public static final int IMM_FLOAT_TYPE = 3;
    public static final int IMM_INT_TYPE = 4;
    public static final int DEFAULT_TYPE = 5;

    private int type;
    private int width;
    private double value;
    private List<Operand> address=new ArrayList<>();

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public List<Operand> getAddress() {
        return address;
    }

    public void setAddress(List<Operand> address) {
        this.address = address;
    }
}
