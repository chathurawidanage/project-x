package lk.ac.mrt.projectx.buildex.models.output;

import lk.ac.mrt.projectx.buildex.DefinesDotH;

import java.util.ArrayList;
import java.util.List;

import static lk.ac.mrt.projectx.buildex.DefinesDotH.Registers.DR_REG_INVALID;
import static lk.ac.mrt.projectx.buildex.x86.X86Analysis.MAX_SIZE_OF_REG;

/**
 * @author Chathura Widanage
 * @author Rukshan Perera
 */
public class Operand implements Comparable<Operand> {

    private MemoryType type;
    private int width;
    private Number value;
    private List<Operand> address = new ArrayList<>();

    public Operand() {
    }

    public Operand(MemoryType operandType, Number value, Integer width) {
        this.type = operandType;
        this.value = value;
        this.width = width;
    }

    public MemoryType getType() {
        return type;
    }

    public void setType(int type) {
        this.type = MemoryType.values()[type];
    }

    public void setType(MemoryType type) {
        this.type = type;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public Number getValue() {
        return value;
    }

    public void setValue(Number value) {
        this.value = value;
    }

    public List<Operand> getAddress() {
        return address;
    }

    public void setAddress(List<Operand> address) {
        this.address = address;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (type == MemoryType.REG_TYPE) {
            Integer offset = (Integer) value - ((Integer) value / MAX_SIZE_OF_REG) * MAX_SIZE_OF_REG;
            stringBuilder.append(((Integer) value).toString());
            stringBuilder.append(":r[");
            stringBuilder.append(this.getRegName());
            stringBuilder.append(":");
            stringBuilder.append(offset.toString());
        } else {
            if (this.type == MemoryType.IMM_FLOAT_TYPE || this.type == MemoryType.IMM_INT_TYPE) {
                stringBuilder.append("imm[");
            } else if (this.type == MemoryType.MEM_STACK_TYPE) {
                stringBuilder.append("ms[");
            } else if (this.type == MemoryType.MEM_HEAP_TYPE) {
                stringBuilder.append("mh[");
            }
            stringBuilder.append(this.value.toString());
        }
        stringBuilder.append("]");
        stringBuilder.append("{");
        stringBuilder.append(this.width);
        stringBuilder.append("}");
        return super.toString();
    }

    /****/
    public String getRegName() {
        DefinesDotH.Registers reg;
        reg = memRangeToRegister();
        String name = reg.name().substring(reg.name().lastIndexOf("_") + 1);
        return name.toLowerCase();
    }

    // TODO 1        : Check why X86_analysis.cpp (mem_range_to_reg) switch case values are different from defines.h
    // TODO 1 contd. : But currently project-x gets the same value in the enum as you see
    private DefinesDotH.Registers memRangeToRegister() {
        DefinesDotH.Registers ret;
        if (this.type == MemoryType.REG_TYPE) {
            int range = (Integer) this.value / MAX_SIZE_OF_REG + 1;
            if (range > 0 && range <= 56) {
                ret = DefinesDotH.Registers.values()[range];
            } else {
                ret = DR_REG_INVALID;
            }
        } else {
            ret = DR_REG_INVALID;
        }
        return ret;
    }

    //endregion public methods

    //region private methods

    @Override
    public int compareTo(Operand other) {
        //TODO : not complete but similar to Helium
        return Double.valueOf(this.value.doubleValue()).
                compareTo(Double.valueOf(other.value.doubleValue()));
    }
}
