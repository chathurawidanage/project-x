package lk.ac.mrt.projectx.buildex.models.output;

import lk.ac.mrt.projectx.buildex.DefinesDotH;
import lk.ac.mrt.projectx.buildex.InstructionTraceUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static lk.ac.mrt.projectx.buildex.DefinesDotH.DR_REG.*;
import static lk.ac.mrt.projectx.buildex.x86.X86Analysis.MAX_SIZE_OF_REG;

/**
 * @author Chathura Widanage
 * @author Rukshan Perera
 */
public class Operand implements Comparable<Operand> {

    final static Logger logger = LogManager.getLogger( Operand.class );
    private MemoryType type;
    private int width;
    private Number value;
    private List<Operand> address = new ArrayList<>();

    public Operand() {
    }

    public Operand(MemoryType operandType, Integer width, Number value) {
        this.type = operandType;
        this.value = value;
        this.width = width;
    }

    public Operand(InstructionTraceUnit.Operand operand) {
        this.type = MemoryType.values()[ ((int) operand.getType()) ];
        this.width = ((int) operand.getWidth());
        this.value = ((Number) operand.getValue());
        for (int i = 0 ; i < operand.addr.length ; i++) {
            Operand op = new Operand( operand.addr[ i ] );
            this.address.add( op );
        }
    }

    public MemoryType getType() {
        return type;
    }

    public void setType(MemoryType type) {
        this.type = type;
    }

    public void setType(int type) {
        this.type = MemoryType.values()[ type ];
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
            stringBuilder.append( ((Integer) value).toString() );
            stringBuilder.append( ":r[" );
            stringBuilder.append( this.getRegName() );
            stringBuilder.append( ":" );
            stringBuilder.append( offset.toString() );
        } else {
            if (this.type == MemoryType.IMM_FLOAT_TYPE || this.type == MemoryType.IMM_INT_TYPE) {
                stringBuilder.append( "imm[" );
            } else if (this.type == MemoryType.MEM_STACK_TYPE) {
                stringBuilder.append( "ms[" );
            } else if (this.type == MemoryType.MEM_HEAP_TYPE) {
                stringBuilder.append( "mh[" );
            }
            stringBuilder.append( this.value.toString() );
        }
        stringBuilder.append( "]" );
        stringBuilder.append( "{" );
        stringBuilder.append( this.width );
        stringBuilder.append( "}" );
        return super.toString();
    }

    /****/
    public String getRegName() {
        DefinesDotH.DR_REG reg;
        reg = memRangeToRegister();
        String name = reg.name().substring( reg.name().lastIndexOf( "_" ) + 1 );
        return name.toLowerCase();
    }

    // TODO 1        : Check why X86_analysis.cpp (mem_range_to_reg) switch case values are different from defines.h
    // TODO 1 contd. : But currently project-x gets the same value in the enum as you see
    // int mem_range_to_reg(operand_t * opnd){
    public DefinesDotH.DR_REG memRangeToRegister() {
        DefinesDotH.DR_REG ret;
        if (this.type == MemoryType.REG_TYPE) {
            int range = (Integer) this.value / MAX_SIZE_OF_REG + 1;
            if (range > 0 && range <= 56) {
                ret = DefinesDotH.DR_REG.values()[ range ];
            } else {
                ret = DR_REG_INVALID;
            }
        } else {
            ret = DR_REG_INVALID;
        }
        return ret;
    }

    @Override
    public int compareTo(Operand other) {
        //TODO : not complete but similar to Helium
        return Double.valueOf( this.value.doubleValue() ).
                compareTo( Double.valueOf( other.value.doubleValue() ) );
    }

    public boolean isFloatingPointReg() {
        DefinesDotH.DR_REG reg = memRangeToRegister();
        Boolean answer = (type == MemoryType.REG_TYPE) && (reg.ordinal() >= DR_REG_ST0.ordinal())
                && (reg.ordinal() <= DR_REG_ST7.ordinal());
        return answer;
    }

    // TODO [KRV] : Check
    @Deprecated // use the function with tos
    public void updateFloatingPointReg(String disams, int line) {
        logger.error( "please use updateFloatingPointReg method with 'tos' parameter " );
        int reg = memRangeToRegister().ordinal();
        int offset = reg - DR_REG_ST0.ordinal();
        int ret = DR_REG_ST8.ordinal() - offset;
        this.value = ret;
        regToMemRange();
    }

    /**
     * reg_to_mem_range (void reg_to_mem_range(operand_t * opnd)
     */
    public void regToMemRange() {
        if (this.type == MemoryType.REG_TYPE) {
            Integer value = (Integer) this.value;
            DefinesDotH.DR_REG reg = DefinesDotH.DR_REG.values()[ value ];
            switch (reg) {

                // ABCD registers
                // A registers
                case DR_REG_RAX:
                case DR_REG_EAX:
                case DR_REG_AX:
                case DR_REG_AH:
                case DR_REG_AL:

                    if (reg == DR_REG_AH) {
                        this.value = 1 * MAX_SIZE_OF_REG - 2;
                    } else {
                        this.value = 1 * MAX_SIZE_OF_REG - this.width;
                    }
                    break;
                // B registers
                case DR_REG_RBX:
                case DR_REG_EBX:
                case DR_REG_BX:
                case DR_REG_BH:
                case DR_REG_BL:

                    if (reg == DR_REG_BH) {
                        this.value = 2 * MAX_SIZE_OF_REG - 2;
                    } else {
                        this.value = 2 * MAX_SIZE_OF_REG - this.width;
                    }
                    break;
                // C registers
                case DR_REG_RCX:
                case DR_REG_ECX:
                case DR_REG_CX:
                case DR_REG_CH:
                case DR_REG_CL:

                    if (reg == DR_REG_CH) {
                        this.value = 3 * MAX_SIZE_OF_REG - 2;
                    } else {
                        this.value = 3 * MAX_SIZE_OF_REG - this.width;
                    }
                    break;
                // D registers
                case DR_REG_RDX:
                case DR_REG_EDX:
                case DR_REG_DX:
                case DR_REG_DH:
                case DR_REG_DL:

                    if (reg == DR_REG_DH) {
                        this.value = 4 * MAX_SIZE_OF_REG - 2;
                    } else {
                        this.value = 4 * MAX_SIZE_OF_REG - this.width;
                    }
                    break;

                // SP, BP, SI , DI registers
                // SP Registers
                case DR_REG_RSP:
                case DR_REG_ESP:
                case DR_REG_SP:
                case DR_REG_SPL:
                    this.value = 5 * MAX_SIZE_OF_REG - this.width;
                    break;
                // BP Registers
                case DR_REG_RBP:
                case DR_REG_EBP:
                case DR_REG_BP:
                case DR_REG_BPL:
                    this.value = 6 * MAX_SIZE_OF_REG - this.width;
                    break;
                // SI Registers
                case DR_REG_RSI:
                case DR_REG_ESI:
                case DR_REG_SI:
                case DR_REG_SIL:
                    this.value = 7 * MAX_SIZE_OF_REG - this.width;
                    break;
                // DI Registers
                case DR_REG_RDI:
                case DR_REG_EDI:
                case DR_REG_DI:
                case DR_REG_DIL:
                    this.value = 8 * MAX_SIZE_OF_REG - this.width;
                    break;

                // x64 regs
                // x64 register 8
                case DR_REG_R8:
                case DR_REG_R8D:
                case DR_REG_R8W:
                case DR_REG_R8L:
                    this.value = 9 * MAX_SIZE_OF_REG - this.width;
                    break;
                // x64 register 9
                case DR_REG_R9:
                case DR_REG_R9D:
                case DR_REG_R9W:
                case DR_REG_R9L:
                    this.value = 10 * MAX_SIZE_OF_REG - this.width;
                    break;
                // x64 register 10
                case DR_REG_R10:
                case DR_REG_R10D:
                case DR_REG_R10W:
                case DR_REG_R10L:
                    this.value = 11 * MAX_SIZE_OF_REG - this.width;
                    break;
                // x64 register 11
                case DR_REG_R11:
                case DR_REG_R11D:
                case DR_REG_R11W:
                case DR_REG_R11L:
                    this.value = 12 * MAX_SIZE_OF_REG - this.width;
                    break;
                // x64 register 12
                case DR_REG_R12:
                case DR_REG_R12D:
                case DR_REG_R12W:
                case DR_REG_R12L:
                    this.value = 13 * MAX_SIZE_OF_REG - this.width;
                    break;
                // x64 register 13
                case DR_REG_R13:
                case DR_REG_R13D:
                case DR_REG_R13W:
                case DR_REG_R13L:
                    this.value = 14 * MAX_SIZE_OF_REG - this.width;
                    break;
                // x64 register 14
                case DR_REG_R14:
                case DR_REG_R14D:
                case DR_REG_R14W:
                case DR_REG_R14L:
                    this.value = 15 * MAX_SIZE_OF_REG - this.width;
                    break;
                // x64 register 15
                case DR_REG_R15:
                case DR_REG_R15D:
                case DR_REG_R15W:
                case DR_REG_R15L:
                    this.value = 16 * MAX_SIZE_OF_REG - this.width;
                    break;

                // mmx registers
                // mmx 0 register
                case DR_REG_MM0:
                case DR_REG_XMM0:
                case DR_REG_YMM0:
                    this.value = 17 * MAX_SIZE_OF_REG - this.width;
                    break;
                // mmx 1 register
                case DR_REG_MM1:
                case DR_REG_XMM1:
                case DR_REG_YMM1:
                    this.value = 18 * MAX_SIZE_OF_REG - this.width;
                    break;
                // mmx 2 register
                case DR_REG_MM2:
                case DR_REG_XMM2:
                case DR_REG_YMM2:
                    this.value = 19 * MAX_SIZE_OF_REG - this.width;
                    break;
                // mmx 3 register
                case DR_REG_MM3:
                case DR_REG_XMM3:
                case DR_REG_YMM3:
                    this.value = 20 * MAX_SIZE_OF_REG - this.width;
                    break;
                // mmx 4 register
                case DR_REG_MM4:
                case DR_REG_XMM4:
                case DR_REG_YMM4:
                    this.value = 21 * MAX_SIZE_OF_REG - this.width;
                    break;
                // mmx 5 register
                case DR_REG_MM5:
                case DR_REG_XMM5:
                case DR_REG_YMM5:
                    this.value = 22 * MAX_SIZE_OF_REG - this.width;
                    break;
                // mmx 6 register
                case DR_REG_MM6:
                case DR_REG_XMM6:
                case DR_REG_YMM6:
                    this.value = 23 * MAX_SIZE_OF_REG - this.width;
                    break;
                // mmx 7 register
                case DR_REG_MM7:
                case DR_REG_XMM7:
                case DR_REG_YMM7:
                    this.value = 24 * MAX_SIZE_OF_REG - this.width;
                    break;

                // new mmx registers
                // new mmx 8 register
                case DR_REG_XMM8:
                case DR_REG_YMM8:
                    this.value = 25 * MAX_SIZE_OF_REG - this.width;
                    break;
                // new mmx 9 register
                case DR_REG_XMM9:
                case DR_REG_YMM9:
                    this.value = 26 * MAX_SIZE_OF_REG - this.width;
                    break;
                // new mmx 10 register
                case DR_REG_XMM10:
                case DR_REG_YMM10:
                    this.value = 27 * MAX_SIZE_OF_REG - this.width;
                    break;
                // new mmx 11 register
                case DR_REG_XMM11:
                case DR_REG_YMM11:
                    this.value = 28 * MAX_SIZE_OF_REG - this.width;
                    break;
                // new mmx 12 register
                case DR_REG_XMM12:
                case DR_REG_YMM12:
                    this.value = 29 * MAX_SIZE_OF_REG - this.width;
                    break;
                // new mmx 13 register
                case DR_REG_XMM13:
                case DR_REG_YMM13:
                    this.value = 30 * MAX_SIZE_OF_REG - this.width;
                    break;
                // new mmx 14 register
                case DR_REG_XMM14:
                case DR_REG_YMM14:
                    this.value = 31 * MAX_SIZE_OF_REG - this.width;
                    break;
                // new mmx 15 register
                case DR_REG_XMM15:
                case DR_REG_YMM15:
                    this.value = 32 * MAX_SIZE_OF_REG - this.width;
                    break;

                // floating point registers
                // floating point ST0 register
                case DR_REG_ST0:
                    this.value = 33 * MAX_SIZE_OF_REG - this.width;
                    break;
                // floating point ST1 register
                case DR_REG_ST1:
                    this.value = 34 * MAX_SIZE_OF_REG - this.width;
                    break;
                // floating point ST2 register
                case DR_REG_ST2:
                    this.value = 35 * MAX_SIZE_OF_REG - this.width;
                    break;
                // floating point ST2 register
                case DR_REG_ST3:
                    this.value = 36 * MAX_SIZE_OF_REG - this.width;
                    break;
                // floating point ST2 register
                case DR_REG_ST4:
                    this.value = 37 * MAX_SIZE_OF_REG - this.width;
                    break;
                // floating point ST2 register
                case DR_REG_ST5:
                    this.value = 38 * MAX_SIZE_OF_REG - this.width;
                    break;
                // floating point ST2 register
                case DR_REG_ST6:
                    this.value = 39 * MAX_SIZE_OF_REG - this.width;
                    break;
                // floating point ST2 register
                case DR_REG_ST7:
                    this.value = 40 * MAX_SIZE_OF_REG - this.width;
                    break;

                // 8 registers kept for the floating point stack extension
                case DR_REG_ST8:
                    this.value = 41 * MAX_SIZE_OF_REG - this.width;
                    break;
                case DR_REG_ST9:
                    this.value = 42 * MAX_SIZE_OF_REG - this.width;
                    break;
                case DR_REG_ST10:
                    this.value = 43 * MAX_SIZE_OF_REG - this.width;
                    break;
                case DR_REG_ST11:
                    this.value = 44 * MAX_SIZE_OF_REG - this.width;
                    break;
                case DR_REG_ST12:
                    this.value = 45 * MAX_SIZE_OF_REG - this.width;
                    break;
                case DR_REG_ST13:
                    this.value = 46 * MAX_SIZE_OF_REG - this.width;
                    break;
                case DR_REG_ST14:
                    this.value = 47 * MAX_SIZE_OF_REG - this.width;
                    break;
                case DR_REG_ST15:
                    this.value = 48 * MAX_SIZE_OF_REG - this.width;
                    break;

                // segments
                case DR_SEG_ES:
                    this.value = 49 * MAX_SIZE_OF_REG - this.width;
                    break;
                case DR_SEG_CS:
                    this.value = 50 * MAX_SIZE_OF_REG - this.width;
                    break;
                case DR_SEG_SS:
                    this.value = 51 * MAX_SIZE_OF_REG - this.width;
                    break;
                case DR_SEG_DS:
                    this.value = 52 * MAX_SIZE_OF_REG - this.width;
                    break;
                case DR_SEG_FS:
                    this.value = 53 * MAX_SIZE_OF_REG - this.width;
                    break;
                case DR_SEG_GS:
                    this.value = 54 * MAX_SIZE_OF_REG - this.width;
                    break;

                // virtual registers
                case DR_REG_VIRTUAL_1:
                    this.value = 55 * MAX_SIZE_OF_REG - this.width;
                    break;
                case DR_REG_VIRTUAL_2:
                    this.value = 56 * MAX_SIZE_OF_REG - this.width;
                    break;
                case DR_REG_NULL:
                    logger.warn( "Null register found - check if OP_lea" );
                    break;
                default:
                    logger.error( "Register %d not translated", value );
                    break;
            }
        } else if ((type == MemoryType.MEM_HEAP_TYPE || type == MemoryType.MEM_STACK_TYPE) && (width != 0)) {
            if (((Integer) value) < MAX_SIZE_OF_REG * 57) {
                logger.warn( "Memory and register space overlap" );
                logger.debug( "mem vaalue - %d, min allowed - %d", value, MAX_SIZE_OF_REG * 57 );
            }
        }
    }

    public void updateFloatingPointReg(String disams, int line, int tos) {
        int reg = memRangeToRegister().ordinal();
        int offset = reg - DR_REG_ST0.ordinal();
        int ret = tos - offset;
        this.value = ret;
        regToMemRange();
    }

    //endregion public methods

}
