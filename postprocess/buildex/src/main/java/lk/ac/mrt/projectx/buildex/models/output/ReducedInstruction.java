package lk.ac.mrt.projectx.buildex.models.output;

import lk.ac.mrt.projectx.buildex.x86.X86Analysis;
import lk.ac.mrt.projectx.buildex.x86.X86Analysis.Operation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static lk.ac.mrt.projectx.buildex.DefinesDotH.DR_REG.DR_REG_VIRTUAL_1;
import static lk.ac.mrt.projectx.buildex.DefinesDotH.OpCodes.*;
import static lk.ac.mrt.projectx.buildex.x86.X86Analysis.checkLAHFBit;

/**
 * Created by krv on 1/12/17.
 */
public class ReducedInstruction {

    final static Logger logger = LogManager.getLogger( ReducedInstruction.class );

    //region Private variables

    private Operation operation;
    private Operand dst;
    private List<Operand> srcs;
    private Boolean sign;
    private Boolean isFloating;
    private Integer numRinstr;

    //endregion Private variables

    //region Public Constructors

    public ReducedInstruction(Operation operation, Operand dst, Boolean sign) {
        this.operation = operation;
        this.dst = dst;
        this.sign = sign;
    }

    //endregion Public Constructors

    //region Public methods

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Integer operation) {
        this.operation = Operation.values()[ operation ];
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public Operand getDst() {
        return dst;
    }

    public void setDst(Operand dst) {
        this.dst = dst;
    }

    public Boolean getSign() {
        return sign;
    }

    public void setSign(Boolean sign) {
        this.sign = sign;
    }

    public Boolean isFloating() {
        return isFloating;
    }

    public Integer getNumRinstr() {
        return srcs.size();
    }

    @Deprecated
    public void setNumRinstr(Integer numRinstr) {
        // this should be the size of the srcs list size no need to explicitly mention
        // in java
        this.numRinstr = numRinstr;
    }

    /**
     * Set floating to true
     */
    public void setFloating() {
        isFloating = true;
    }

    /**
     * This is a pure function without side effects for cinstr -- Helium
     *
     * @param cinst  ::Output pointer to a pre-populated complex x86 instruction
     * @param disams ::String string of the disassembly of 'cinstr'. Can be used for debugging purposes
     * @param line   ::Integer Line in which this instruction can be found in the instruction trace (after filtering)
     * @return ::List<ReducedInstruction> Reduced instruction list (original amount pointer will be the size of the list)
     */
    private List<ReducedInstruction> cinstrToRinstr(Output cinst, String disams, Integer line) {
        logger.debug( "Enter canonicalization - app_pc" );
        Integer amount = 0; // this can be taken by the size of the return list
        List<ReducedInstruction> rInstructions = new ArrayList<>();
        Boolean unhandled = false;

        switch (cinst.getOpcode()) {
            case OP_movss:
                //todo - Kevin to test -- Helium
                if (isBounds( cinst, 1, 1 )) {
                    // move low 32 bits from src to dst. Upper bits unchanged is dst is register
                    // bits 127-32 zeroes is source is memory
                    Operand dst = cinst.getDsts().get( 0 );
                    Operand src = cinst.getSrcs().get( 0 );
                    // get low 32 bits of dst (or 4 bytes)
                    Operand dst_low = new Operand( dst.getType(), 4, ((Integer) dst.getValue())
                            + dst.getWidth() - 4 );
                    Operand src_low = new Operand( src.getType(), 4, ((Integer) src.getValue())
                            + src.getWidth() - 4 );
                    if (dst.getType() == MemoryType.MEM_STACK_TYPE || dst.getType() == MemoryType.MEM_HEAP_TYPE) {
                        ReducedInstruction inst0 = new ReducedInstruction( Operation.op_assign, dst_low, true );
                        inst0.srcs.add( src_low );
                        // get upper bits
                        Operand dst_hight = new Operand( dst.getType(), 12, ((Integer) dst.getValue())
                                + dst.getWidth() - 16 );
                        // move zeros into upper bits
                        Operand immediate = new Operand( MemoryType.IMM_INT_TYPE, 12, 0 );
                        ReducedInstruction inst1 = new ReducedInstruction( Operation.op_assign, dst_hight, true );
                        inst1.srcs.add( immediate );

                        rInstructions.add( inst0 );
                        rInstructions.add( inst1 );
                    } else {
                        ReducedInstruction inst0 = new ReducedInstruction( Operation.op_assign, dst_low, true );
                        inst0.srcs.add( src_low );
                        rInstructions.add( inst0 );
                    }

                } else {
                    unhandled = true;
                }
                break;
            case OP_pmuldq:
                // todo - Kevin to test - Helium
                if (isBounds( cinst, 2, 1 )) {
                    /** If destination larger than 128 bits, should keep upper bits unhcanged
                     * dsts[0][63:0] <- srcs[0][31:0] * srcs[1][31:0]
                     * dsts[0][127:64] <- srcs[0][95:64] * srcs[1][95:64]
                     */
//                    amount = 2;
                    // create virtual reg/mem accesssing 32 bit input ranges tmp 64 bit reg and
                    // 2 tmp 32 bit reg
                    Operand input1 = cinst.getSrcs().get( 0 );
                    Operand input2 = cinst.getSrcs().get( 1 );
                    Operand output = cinst.getDsts().get( 0 );

                    // offset starting point by correct number of BYTES (divided by 8 to change
                    // from bits to bytes
                    Operand input32_1 = new Operand( input1.getType(), 4, ((Integer) input1.getValue())
                            + input1.getWidth() - 4 );
                    Operand input96_1 = new Operand( input1.getType(), 4, ((Integer) input1.getValue())
                            + input1.getWidth() - 12 );
                    Operand input32_2 = new Operand( input2.getType(), 4, ((Integer) input2.getValue())
                            + input2.getWidth() - 4 );
                    Operand input96_2 = new Operand( input2.getType(), 4, ((Integer) input2.getValue())
                            + input2.getWidth() - 12 );

                    // create virtual reg/mem accessing 8 byte ranges
                    Operand output_low = new Operand( output.getType(), 8, ((Integer) output.getValue())
                            + output.getWidth() - 8 );
                    Operand output_high = new Operand( output.getType(), 8, ((Integer) output.getValue())
                            + output.getWidth() - 16 );

                    ReducedInstruction inst0 = new ReducedInstruction( Operation.op_mul, output_low, true );
                    inst0.getSrcs().add( input32_1 );
                    inst0.getSrcs().add( input32_2 );
                    ReducedInstruction inst1 = new ReducedInstruction( Operation.op_mul, output_high, true );
                    inst1.getSrcs().add( input96_1 );
                    inst1.getSrcs().add( input96_2 );
                    rInstructions.add( inst0 );
                    rInstructions.add( inst1 );
                } else {
                    unhandled = true;
                }
                break;

            // **************************** integer instructions
            case OP_push_imm:
            case OP_push:
                // [esp -4] [dst[1]] <- src[0]
                if (isBounds( cinst, 2, 2 )) {
//                    amount = 1;
                    ReducedInstruction inst0 = new ReducedInstruction( Operation.op_assign, cinst.getDsts().get( 1 ),
                            false );
                    inst0.getSrcs().add( cinst.getSrcs().get( 0 ) );
                    rInstructions.add( inst0 );
                } else {
                    unhandled = true;
                }
                break;
            case OP_pop:
                // dst[0] <- [esp][src[1]]
                if (isBounds( cinst, 2, 2 )) {
//                    amount = 1;
                    ReducedInstruction inst0 = new ReducedInstruction( Operation.op_assign, cinst.getDsts().get( 0 ),
                            false );
                    inst0.getSrcs().add( cinst.getSrcs().get( 1 ) );
                    rInstructions.add( inst0 );
                } else {
                    unhandled = true;
                }

                break;
            case OP_mov_st:
            case OP_mov_ld:
            case OP_mov_imm:
            case OP_movzx:
            case OP_movsx:
            case OP_movq:
            case OP_movd:
            case OP_movapd:
            case OP_movdqa:
            case OP_mov_seg:
            case OP_movaps: // todo : kevin to test -- Helium

            case OP_vmovss: // todo : check
            case OP_vmovsd: // todo : check

            case OP_vcvtsi2ss: // todo : check
            case OP_vcvtsi2sd: // todo : check
            case OP_vcvttss2si: // todo : check
            case OP_vcvttsd2si: // todo : check

            case OP_cvttsd2si: // todo : check
                // dst[0] <- src[0]
                if (isBounds( cinst, 1, 1 )) {
//                    amount = 1;
                    ReducedInstruction inst0 = new ReducedInstruction( Operation.op_assign, cinst.getDsts().get( 0 ),
                            false );
                    inst0.getSrcs().add( cinst.getSrcs().get( 0 ) );
                    rInstructions.add( inst0 );
                } else if (isBounds( cinst, 1, 2 )) {
//                    amount = 1;
                    ReducedInstruction inst0 = new ReducedInstruction( Operation.op_assign, cinst.getDsts().get( 0 ),
                            false );
                    inst0.getSrcs().add( cinst.getSrcs().get( 1 ) );
                    rInstructions.add( inst0 );
                } else {
                    unhandled = true;
                }
                break;
            case OP_vmulss:
            case OP_vmulsd:
                if (isBounds( cinst, 1, 2 )) {
                    ReducedInstruction inst0 = new ReducedInstruction( Operation.op_mul, cinst.getDsts().get( 0 ),
                            false );
                    inst0.getSrcs().add( cinst.getSrcs().get( 0 ) );
                    inst0.getSrcs().add( cinst.getSrcs().get( 1 ) );
                    rInstructions.add( inst0 );
                } else {
                    unhandled = true;
                }
                break;
            case OP_vaddsd:
                if (isBounds( cinst, 1, 2 )) {
                    ReducedInstruction inst0 = new ReducedInstruction( Operation.op_add, cinst.getDsts().get( 0 ),
                            false );
                    inst0.getSrcs().add( cinst.getSrcs().get( 0 ) );
                    inst0.getSrcs().add( cinst.getSrcs().get( 1 ) );
                    rInstructions.add( inst0 );
                } else {
                    unhandled = true;
                }
                break;
            case OP_imul:
                // 1st flavour -> 1 dst * 2 src
                // dst[0] <- src[0] * src[1]
                if (isBounds( cinst, 1, 2 )) {
                    ReducedInstruction inst0 = new ReducedInstruction( Operation.op_mul, cinst.getDsts().get( 0 ),
                            true );
                    inst0.getSrcs().add( cinst.getSrcs().get( 0 ) );
                    inst0.getSrcs().add( cinst.getSrcs().get( 1 ) );
                    rInstructions.add( inst0 );
                } else if (isBounds( cinst, 2, 2 )) {
                    // edx [dst0] : eax [dst1] <- eax [src1] * [src0]
                    // create an operand for the virtual register
                    Operand virtualReg = new Operand( MemoryType.REG_TYPE, 2 * cinst.getSrcs().get( 1 ).getWidth()
                            , DR_REG_VIRTUAL_1.ordinal() );
                    virtualReg.regToMemRange();

                    // virtual <= eax * src0
                    ReducedInstruction inst0 = new ReducedInstruction( Operation.op_mul, virtualReg, true );
                    inst0.getSrcs().add( cinst.getSrcs().get( 1 ) );
                    inst0.getSrcs().add( cinst.getSrcs().get( 0 ) );

                    // edx <= split_h(virtual)
                    ReducedInstruction inst1 = new ReducedInstruction( Operation.op_split_h, cinst.getDsts().get( 0 ), true );
                    inst1.getSrcs().add( virtualReg );

                    // eax <= split_l(virtual)
                    ReducedInstruction inst2 = new ReducedInstruction( Operation.op_split_l, cinst.getDsts().get( 1 ), true );
                    inst2.getSrcs().add( virtualReg );

                    rInstructions.add( inst0 );
                    rInstructions.add( inst1 );
                    rInstructions.add( inst2 );
                } else {
                    unhandled = true;
                }
                break;
            case OP_mul:
                if (isBounds( cinst, 2, 2 )) {
                    Operand virtualReg = new Operand( MemoryType.REG_TYPE, 2 * cinst.getSrcs().get( 1 ).getWidth()
                            , DR_REG_VIRTUAL_1.ordinal() );
                    virtualReg.regToMemRange();

                    // virtual <= eax * src0
                    ReducedInstruction inst0 = new ReducedInstruction( Operation.op_mul, virtualReg, false );
                    inst0.getSrcs().add( cinst.getSrcs().get( 1 ) );
                    inst0.getSrcs().add( cinst.getSrcs().get( 0 ) );

                    // edx <= split_h(virtual)
                    ReducedInstruction inst1 = new ReducedInstruction( Operation.op_split_h, cinst.getDsts().get( 0 ), false );
                    inst1.getSrcs().add( virtualReg );

                    // eax <= split_l(virtual)
                    ReducedInstruction inst2 = new ReducedInstruction( Operation.op_split_l, cinst.getDsts().get( 1 ), false );
                    inst2.getSrcs().add( virtualReg );

                    rInstructions.add( inst0 );
                    rInstructions.add( inst1 );
                    rInstructions.add( inst2 );
                } else {
                    unhandled = true;
                }
                break;
            case OP_idiv:
                // dst - edx / dx, eax / ax, src - src[0], edx / dx, eax / ax
                if (isBounds( cinst, 2, 3 )) {
                    // create an operand for virtual register
                    Operand virtualReg = new Operand( MemoryType.REG_TYPE, 2 * cinst.getSrcs().get( 1 ).getWidth()
                            , DR_REG_VIRTUAL_1.ordinal() );
                    virtualReg.regToMemRange();

                    // virtual <= eax * src0
                    ReducedInstruction inst0 = new ReducedInstruction( Operation.op_mul, virtualReg, false );
                    inst0.getSrcs().add( cinst.getSrcs().get( 1 ) );
                    inst0.getSrcs().add( cinst.getSrcs().get( 2 ) );

                    // edx <= split_h(virtual)
                    ReducedInstruction inst1 = new ReducedInstruction( Operation.op_split_h, cinst.getDsts().get( 0 ),
                            true );
                    inst1.getSrcs().add( virtualReg );

                    // eax <= split_l(virtual)
                    ReducedInstruction inst2 = new ReducedInstruction( Operation.op_split_l, cinst.getDsts().get( 1 ),
                            true );
                    inst2.getSrcs().add( virtualReg );

                    rInstructions.add( inst0 );
                    rInstructions.add( inst1 );
                    rInstructions.add( inst2 );
                } else {
                    unhandled = true;
                }
                break;
            case OP_cdq:
                // todo : need to change -- Helium
                // edx <- eax
                if (isBounds( cinst, 1, 1 )) {
                    ReducedInstruction inst0 = new ReducedInstruction( Operation.op_signex,
                            cinst.getDsts().get( 0 ), true );
                    inst0.getSrcs().add( cinst.getSrcs().get( 0 ) );
                    rInstructions.add( inst0 );
                } else {
                    unhandled = true;
                }
                break;
            case OP_cwde:
                // eax <- ax
                if (isBounds( cinst, 1, 1 )) {
                    ReducedInstruction inst0 = new ReducedInstruction( Operation.op_signex, cinst.getDsts().get( 0 ),
                            true );
                    inst0.getSrcs().add( cinst.getSrcs().get( 0 ) );
                    rInstructions.add( inst0 );
                } else {
                    unhandled = true;
                }
                break;
            case OP_xchg:
                // exchange the two registers
                if (isBounds( cinst, 2, 2 )) {
                    Operand virtualReg = new Operand( MemoryType.REG_TYPE, cinst.getSrcs().get( 0 ).getWidth(),
                            DR_REG_VIRTUAL_1.ordinal() );
                    virtualReg.regToMemRange();

                    // virtual <- src[0]
                    ReducedInstruction inst0 = new ReducedInstruction( Operation.op_assign, virtualReg, false );
                    inst0.getSrcs().add( cinst.getSrcs().get( 0 ) );
                    // dst[0] <- src[1]
                    ReducedInstruction inst1 = new ReducedInstruction( Operation.op_assign, cinst.getDsts().get( 0 ),
                            false );
                    inst1.getSrcs().add( cinst.getSrcs().get( 1 ) );
                    // dst[1] <- virtual
                    ReducedInstruction inst2 = new ReducedInstruction( Operation.op_assign, cinst.getDsts().get( 1 ),
                            false );
                    inst1.getSrcs().add( virtualReg );
                    rInstructions.add( inst0 );
                    rInstructions.add( inst1 );
                    rInstructions.add( inst2 );
                } else {
                    unhandled = true;
                }
                break;
            case OP_xorps: //todo - KEVIN test -- Helium
            case OP_xor:
            case OP_sub:
            case OP_pxor:
            case OP_psubd:
                if (isBounds( cinst, 1, 2 )) {
                    Operand first = cinst.getSrcs().get( 0 );
                    Operand second = cinst.getSrcs().get( 1 );
                    ReducedInstruction inst0 = null;
                    if (first.getType() == second.getType() && first.getValue() == second.getValue()) {
                        Operand zero = new Operand( MemoryType.IMM_INT_TYPE, cinst.getSrcs().get( 0 ).getWidth(), 0 );
                        inst0 = new ReducedInstruction( Operation.op_assign, cinst.getDsts().get( 0 ), false );
                        inst0.getSrcs().add( zero );
                    } else {
                        Operation op = null;
                        switch (cinst.getOpcode()) {
                            case OP_xorps:
                            case OP_xor:
                            case OP_pxor:
                                op = Operation.op_xor;
                                break;
                            case OP_sub:
                            case OP_psubd:
                                op = Operation.op_sub;
                                break;
                        }
                        inst0 = new ReducedInstruction( op, cinst.getDsts().get( 0 ), false );
                        inst0.getSrcs().add( cinst.getSrcs().get( 1 ) );
                        inst0.getSrcs().add( cinst.getSrcs().get( 0 ) );
                    }

                    rInstructions.add( inst0 );
                } else {
                    unhandled = true;
                }
                break;
            case OP_add:
            case OP_and:
            case OP_andpd:
            case OP_or:
                // dst[0] <- src[1] (op) src[0]
                if (isBounds( cinst, 1, 2 )) {
                    Operation op = null;
                    switch (cinst.getOpcode()) {
                        case OP_add:
                            op = Operation.op_add;
                            break;
                        case OP_and:
                        case OP_andpd:
                            op = Operation.op_and;
                            break;
                        case OP_or:
                            op = Operation.op_or;
                            break;
                    }
                    ReducedInstruction inst0 = new ReducedInstruction( op, cinst.getDsts().get( 0 ), false );
                    // todo : Changed for SUB (src1, src0) from the reverse : please check -- Helium
                    inst0.getSrcs().add( cinst.getSrcs().get( 1 ) );
                    inst0.getSrcs().add( cinst.getSrcs().get( 0 ) );
                    rInstructions.add( inst0 );
                } else {
                    unhandled = true;
                }
                break;
            case OP_neg:
                if (isBounds( cinst, 1, 1 )) {
                    ReducedInstruction inst0 = new ReducedInstruction( Operation.op_sub, cinst.getDsts().get( 0 ),
                            false );
                    inst0.getSrcs().add( cinst.getSrcs().get( 0 ) );
                    rInstructions.add( inst0 );
                } else {
                    unhandled = false;
                }
                break;
            case OP_dec:
                if (isBounds( cinst, 1, 1 )) {
                    Operand immediate = new Operand( MemoryType.IMM_INT_TYPE, cinst.getSrcs().get( 0 ).getWidth(),
                            1 );
                    ReducedInstruction inst0 = new ReducedInstruction( Operation.op_sub, cinst.getDsts().get( 0 ),
                            true );
                    inst0.getSrcs().add( cinst.getSrcs().get( 0 ) );
                    inst0.getSrcs().add( immediate );
                    rInstructions.add( inst0 );
                } else {
                    unhandled = true;
                }
                break;
            case OP_inc:
                if (isBounds( cinst, 1, 1 )) {
                    Operand immediate = new Operand( MemoryType.IMM_INT_TYPE, cinst.getSrcs().get( 0 ).getWidth(),
                            1 );
                    ReducedInstruction inst0 = new ReducedInstruction( Operation.op_add, cinst.getDsts().get( 0 ),
                            true );
                    inst0.getSrcs().add( cinst.getSrcs().get( 0 ) );
                    inst0.getSrcs().add( immediate );
                    rInstructions.add( inst0 );
                } else {
                    unhandled = true;
                }
                break;
            case OP_sar:
                if (isBounds( cinst, 1, 2 )) {
                    ReducedInstruction inst0 = new ReducedInstruction( Operation.op_rsh, cinst.getDsts().get( 0 ),
                            true );
                    inst0.getSrcs().add( cinst.getSrcs().get( 1 ) );
                    inst0.getSrcs().add( cinst.getSrcs().get( 0 ) );
                    rInstructions.add( inst0 );
                } else {
                    unhandled = true;
                }
                break;
            case OP_shr:
            case OP_psrlq:
                if (isBounds( cinst, 1, 2 )) {
                    ReducedInstruction inst0 = new ReducedInstruction( Operation.op_rsh, cinst.getDsts().get( 0 ),
                            false );
                    inst0.getSrcs().add( cinst.getSrcs().get( 1 ) );
                    inst0.getSrcs().add( cinst.getSrcs().get( 0 ) );
                    rInstructions.add( inst0 );
                } else {
                    unhandled = true;
                }
                break;
            case OP_shl:
            case OP_psllq:
                if (isBounds( cinst, 1, 1 )) {
                    ReducedInstruction inst0 = new ReducedInstruction( Operation.op_lsh, cinst.getDsts().get( 0 ),
                            false );
                    inst0.getSrcs().add( cinst.getSrcs().get( 1 ) );
                    inst0.getSrcs().add( cinst.getSrcs().get( 0 ) );
                    rInstructions.add( inst0 );
                } else {
                    unhandled = true;
                }
                break;
            case OP_not:
                if (isBounds( cinst, 1, 1 )) {
                    ReducedInstruction inst0 = new ReducedInstruction( Operation.op_not, cinst.getDsts().get( 0 ),
                            false );
                    inst0.getSrcs().add( cinst.getSrcs().get( 0 ) );
                    rInstructions.add( inst0 );
                } else {
                    unhandled = true;
                }
                break;
            case OP_lea:
                // [base, index, scale, disp]
                if (isBounds( cinst, 1, 4 )) {
                    if ((cinst.getSrcs().get( 0 ).getValue() == 0) &&
                            (cinst.getSrcs().get( 0 ).getType() == MemoryType.REG_TYPE)) {
                        cinst.getSrcs().get( 0 ).setType( MemoryType.IMM_INT_TYPE );
                        cinst.getSrcs().get( 0 ).setWidth( 4 );
                        cinst.getSrcs().get( 0 ).setValue( 0 );
                    }

                    if (cinst.getSrcs().get( 2 ).getValue() == 0) {
                        // dst <- base(src0) + disp(src3)
                        ReducedInstruction inst0 = new ReducedInstruction( Operation.op_add, cinst.getDsts().get( 0 ),
                                true );
                        inst0.getSrcs().add( cinst.getSrcs().get( 0 ) );
                        inst0.getSrcs().add( cinst.getSrcs().get( 3 ) );
                        rInstructions.add( inst0 );

                    } else {
                        Operand virtualReg = new Operand( MemoryType.REG_TYPE, cinst.getSrcs().get( 0 ).getWidth(),
                                DR_REG_VIRTUAL_1.ordinal() );
                        virtualReg.regToMemRange();

                        // virtual <- scale(src2) * index(src1)
                        ReducedInstruction inst0 = new ReducedInstruction( Operation.op_mul, virtualReg, true );
                        inst0.getSrcs().add( cinst.getSrcs().get( 2 ) );
                        inst0.getSrcs().add( cinst.getSrcs().get( 1 ) );

                        // virtual <- virtual + base(src0)
                        ReducedInstruction inst1 = new ReducedInstruction( Operation.op_add, virtualReg, true );
                        inst1.getSrcs().add( virtualReg );
                        inst1.getSrcs().add( cinst.getSrcs().get( 0 ) );

                        // dst <- virtual + disp
                        ReducedInstruction inst2 = new ReducedInstruction( Operation.op_add, cinst.getDsts().get( 0 ),
                                true );
                        inst2.getSrcs().add( virtualReg );
                        inst2.getSrcs().add( cinst.getSrcs().get( 3 ) );

                        rInstructions.add( inst0 );
                        rInstructions.add( inst1 );
                        rInstructions.add( inst2 );
                    }
                } else {
                    unhandled = true;
                }
                break;
            case OP_sbb:
                if (isBounds( cinst, 1, 2 )) {
                    Boolean cf = checkLAHFBit( X86Analysis.LahfBits.CARRY_LAHF, cinst.getEflags() );
                    ReducedInstruction inst0 = null;
                    // dsts[0] <- srcs[1] - srcs[0]
                    if (cinst.getSrcs().get( 0 ).getType() == cinst.getSrcs().get( 1 ).getType()
                            && cinst.getSrcs().get( 0 ).getValue() == cinst.getSrcs().get( 1 ).getValue()) {
                        Operand immediate0 = new Operand( MemoryType.IMM_INT_TYPE, cinst.getSrcs().get( 1 ).getWidth(),
                                0 );
                        inst0 = new ReducedInstruction( Operation.op_assign, cinst.getDsts().get( 0 )
                                , true );
                        inst0.getSrcs().add( immediate0 );
                    } else {
                        inst0 = new ReducedInstruction( Operation.op_sub, cinst.getDsts().get( 0 )
                                , true );
                        inst0.getSrcs().add( cinst.getSrcs().get( 1 ) );
                        inst0.getSrcs().add( cinst.getSrcs().get( 0 ) );
                    }
                    rInstructions.add( inst0 );
                    if (cf) {
                        // subtract an immediate 1
                        // dsts[0] <- dsts[0] - 1
                        Operand immediate1 = new Operand( MemoryType.IMM_INT_TYPE, cinst.getSrcs().get( 1 ).getWidth(),
                                1 );
                        ReducedInstruction inst1 = new ReducedInstruction( Operation.op_sub, cinst.getDsts().get( 0 )
                                , true );
                        inst1.getSrcs().add( cinst.getDsts().get( 0 ) );
                        inst1.getSrcs().add( immediate1 );
                        rInstructions.add( inst1 );
                    }
                } else {
                    unhandled = true;
                }
                break;
            case OP_adc:
                if (isBounds( cinst, 1, 2 )) {
                    Boolean cf = checkLAHFBit( X86Analysis.LahfBits.CARRY_LAHF, cinst.getEflags() );

                    // dsts[0] <- srcs[1] + srcs[0]
                    ReducedInstruction inst0 = new ReducedInstruction( Operation.op_add, cinst.getDsts().get( 0 ),
                            true );
                    inst0.getSrcs().add( cinst.getSrcs().get( 1 ) );
                    inst0.getSrcs().add( cinst.getSrcs().get( 0 ) );
                    rInstructions.add( inst0 );

                    // dsts[0] <- dsts[0] + 1
                    if (cf) {
                        Operand immediate1 = new Operand( MemoryType.IMM_INT_TYPE, cinst.getSrcs().get( 1 ).getWidth(),
                                1 );
                        ReducedInstruction inst1 = new ReducedInstruction( Operation.op_add, cinst.getDsts().get( 0 ),
                                true );
                        inst1.getSrcs().add( cinst.getDsts().get( 0 ) );
                        inst1.getSrcs().add( immediate1 );
                        rInstructions.add( inst1 );
                    }
                } else {
                    unhandled = true;
                }
                break;
            case OP_cmovle:
            case OP_cmovnle:
            case OP_cmovl:
            case OP_cmovnl:
            case OP_cmovz:
            case OP_cmovns:
            case OP_cmovnz:
                if (isBounds( cinst, 1, 2 )) {
                    Boolean zf = checkLAHFBit( X86Analysis.LahfBits.ZERO_LAHF, cinst.getEflags() );
                    Boolean sf = checkLAHFBit( X86Analysis.LahfBits.SIGN_LAHF, cinst.getEflags() );
                    Boolean of = checkLAHFBit( X86Analysis.LahfBits.OVERFLOW_LAHF, cinst.getEflags() );
                    boolean check = false;
                    if (cinst.getOpcode() == OP_cmovl) {
                        check = (sf != of);
                    } else if (cinst.getOpcode() == OP_cmovle) {
                        check = (zf || (sf != of));
                    } else if (cinst.getOpcode() == OP_cmovnle) {
                        check = ((!zf) || (sf == of));
                    } else if (cinst.getOpcode() == OP_cmovnl) {
                        check = (sf == of);
                    } else if (cinst.getOpcode() == OP_cmovz) {
                        check = zf;
                    } else if (cinst.getOpcode() == OP_cmovnz) {
                        check = !zf;
                    } else if (cinst.getOpcode() == OP_cmovns) {
                        check = !sf;
                    }

                    if (check) {
                        ReducedInstruction inst0 = new ReducedInstruction( Operation.op_assign, cinst.getDsts().get( 0 )
                                , true );
                        inst0.getSrcs().add( cinst.getSrcs().get( 0 ) );
                        rInstructions.add( inst0 );
                    }

                } else {
                    unhandled = true;
                }
                break;
            case OP_setz:
            case OP_sets:
            case OP_setns:
            case OP_setnz:
            case OP_setb:
                if (isBounds( cinst, 1, 0 )) {
                    boolean flag = false;
                    switch (cinst.getOpcode()) {
                        case OP_setz:
                            flag = checkLAHFBit( X86Analysis.LahfBits.ZERO_LAHF, cinst.getEflags() );
                            break;
                        case OP_setnz:
                            flag = checkLAHFBit( X86Analysis.LahfBits.ZERO_LAHF, cinst.getEflags() );
                            break;
                        case OP_sets:
                            flag = checkLAHFBit( X86Analysis.LahfBits.SIGN_LAHF, cinst.getEflags() );
                            break;
                        case OP_setns:
                            flag = checkLAHFBit( X86Analysis.LahfBits.SIGN_LAHF, cinst.getEflags() );
                            break;
                        case OP_setb:
                            flag = checkLAHFBit( X86Analysis.LahfBits.CARRY_LAHF, cinst.getEflags() );
                            break;
                    }

                    Operand immediate = null;

                    if (flag) {
                        immediate = new Operand( MemoryType.IMM_INT_TYPE, cinst.getDsts().get( 0 ).getWidth(),
                                1 );
                    } else {
                        immediate = new Operand( MemoryType.IMM_INT_TYPE, cinst.getDsts().get( 0 ).getWidth(),
                                0 );
                    }

                    ReducedInstruction inst0 = new ReducedInstruction( Operation.op_assign, cinst.getDsts().get( 0 ),
                            true );
                    inst0.getSrcs().add( immediate );
                    rInstructions.add( inst0 );

                } else {
                    unhandled = true;
                }
                break;
            case OP_xadd:
                if (isBounds( cinst, 2, 2 )) {
                    Operand virtualReg = new Operand( MemoryType.REG_TYPE, cinst.getSrcs().get( 0 ).getWidth(),
                            DR_REG_VIRTUAL_1.ordinal() );
                    virtualReg.regToMemRange();
                    ReducedInstruction inst0 = new ReducedInstruction( Operation.op_add, virtualReg, true );
                    inst0.getSrcs().add( cinst.getSrcs().get( 0 ) );
                    inst0.getSrcs().add( cinst.getSrcs().get( 1 ) );

                    ReducedInstruction inst1 = new ReducedInstruction( Operation.op_assign, cinst.getSrcs().get( 1 ),
                            true );
                    inst1.getSrcs().add( cinst.getSrcs().get( 0 ) );

                    ReducedInstruction inst2 = new ReducedInstruction( Operation.op_assign, cinst.getSrcs().get( 0 ),
                            true );
                    inst2.getSrcs().add( virtualReg );

                    rInstructions.add( inst0 );
                    rInstructions.add( inst1 );
                    rInstructions.add( inst2 );
                } else {
                    unhandled = true;
                }
                break;

            /***************************************************floating point instructions*********************************************************/

		    /* floating point instructions */
            case OP_fld: //Push m32fp onto the FPU register stack.
            case OP_fld1: //Push +1.0 onto the FPU register stack
            case OP_fild: //Push m32int onto the FPU register stack.
            case OP_fldz: //Push +0.0 onto the FPU register stack.
                // dst[0] <- src[0]
                if (isBounds( cinst, 1, 1 )) {
                    ReducedInstruction inst0 = new ReducedInstruction( Operation.op_assign, cinst.getDsts().get( 0 ),
                            false );
                    inst0.getSrcs().add( cinst.getSrcs().get( 0 ) );
                    rInstructions.add( inst0 );
                } else {
                    unhandled = true;
                }
                break;
            case OP_fst:
            case OP_fstp:  //Copy ST(0) to m32fp and pop register stack.
            case OP_fistp:  //Store ST(0) in m32int and pop register stack.
                if (isBounds( cinst, 1, 1 )) {
                    ReducedInstruction inst0 = new ReducedInstruction( Operation.op_assign, cinst.getDsts().get( 0 ),
                            false );
                    inst0.getSrcs().add( cinst.getSrcs().get( 0 ) );
                    rInstructions.add( inst0 );
                } else {
                    unhandled = true;
                }
                break;
            case OP_fmul: //Multiply ST(0) by m32fp and store result in ST(0).
            case OP_fmulp:  //Multiply ST(i) by ST(0), store result in ST(i), and pop the register stack.
                if (isBounds( cinst, 1, 2 )) {
                    ReducedInstruction inst0 = new ReducedInstruction( Operation.op_mul, cinst.getDsts().get( 0 ),
                            true );
                    inst0.getSrcs().add( cinst.getSrcs().get( 0 ) );
                    inst0.getSrcs().add( cinst.getSrcs().get( 1 ) );
                    rInstructions.add( inst0 );
                } else {
                    unhandled = true;
                }
                break;

            //Exchange the contents of ST(0) and ST(i).
            case OP_fxch:
                //exchange the two registers
                if (isBounds( cinst, 2, 2 )) {
                    Operand virtualReg = new Operand( MemoryType.REG_TYPE, cinst.getSrcs().get( 0 ).getWidth(),
                            DR_REG_VIRTUAL_1.ordinal() );
                    virtualReg.regToMemRange();

                    // virtual <- src[0]
                    ReducedInstruction inst0 = new ReducedInstruction( Operation.op_assign, virtualReg, false );
                    inst0.getSrcs().add( cinst.getSrcs().get( 0 ) );

                    // dst[0] <- src[1]
                    ReducedInstruction inst1 = new ReducedInstruction( Operation.op_assign, cinst.getDsts().get( 0 ),
                            false );
                    inst1.getSrcs().add( cinst.getSrcs().get( 1 ) );

                    // dst[1] <- virtual
                    ReducedInstruction inst2 = new ReducedInstruction( Operation.op_assign, cinst.getDsts().get( 1 ),
                            false );
                    inst1.getSrcs().add( virtualReg );

                    rInstructions.add( inst0 );
                    rInstructions.add( inst1 );
                    rInstructions.add( inst2 );
                } else {
                    unhandled = true;
                }
                break;
            case OP_faddp:  // Add ST(0) to ST(i), store result in ST(i), and pop the register stack
            case OP_fadd:   // Add m32fp to ST(0) and store result in ST(0).
            case OP_fsubp:  // Subtract ST(0) from ST(1), store result in ST(1), and pop register stack.
            case OP_fsub:   // Subtract m32fp from ST(0) and store result in ST(0).
            case OP_fdivp:  // Divide ST(1) by ST(0), store result in ST(1), and pop the register stack.
            case OP_fdiv:   // Divide ST(0) by m32fp and store result in ST(0).
                // dst[0] <- src[1] (op) src[0]
                if (isBounds( cinst, 1, 2 )) {
                    Operation op = null;
                    switch (cinst.getOpcode()) {
                        case OP_faddp:
                        case OP_fadd:
                            op = Operation.op_add;
                            break;
                        case OP_fsubp:
                        case OP_fsub:
                            op = Operation.op_sub;
                            break;
                        case OP_fdivp:
                        case OP_fdiv:
                            op = Operation.op_div;
                            break;
                    }
                    /* changed for SUB (src1, src0) from the reverse: please verify */
                    ReducedInstruction inst0 = new ReducedInstruction( op, cinst.getDsts().get( 0 ), false );
                    inst0.getSrcs().add( cinst.getSrcs().get( 1 ) );
                    inst0.getSrcs().add( cinst.getSrcs().get( 0 ) );
                    rInstructions.add( inst0 );
                } else {
                    unhandled = true;
                }
                break;
            case OP_fsubr:
                if (isBounds( cinst, 1, 2 )) {
                    ReducedInstruction inst0 = new ReducedInstruction( Operation.op_sub, cinst.getDsts().get( 0 ),
                            false );
                    inst0.getSrcs().add( cinst.getSrcs().get( 0 ) );
                    inst0.getSrcs().add( cinst.getSrcs().get( 1 ) );
                    rInstructions.add( inst0 );

                } else {
                    unhandled = true;
                }
                break;
            /******************************************************control flow instructions**************************************************************************************************/

            case OP_btr:

            case OP_cmpxchg:
            case OP_rep_stos:
            case OP_cld:
            case OP_jbe:

            case OP_fcom:
            case OP_fcomp:

		/* above change */

            case OP_jmp:
            case OP_jmp_short:
            case OP_jnl:
            case OP_jnl_short:
            case OP_jl:
            case OP_jnle:
            case OP_jnle_short:
            case OP_jnz:
            case OP_jnz_short:
            case OP_jz:
            case OP_jnb_short:
            case OP_jb_short:
            case OP_jz_short:
            case OP_jl_short:
            case OP_jns_short:
            case OP_js_short:
            case OP_jnbe_short:
            case OP_jle_short:
            case OP_jle:
            case OP_jbe_short:
            case OP_jns:
            case OP_jb:
            case OP_jnb:
            case OP_js:
            case OP_jmp_ind:
            case OP_jnbe:

            case OP_cmp:
            case OP_test:

            case OP_call:
            case OP_ret:
            case OP_call_ind:

		/* need to check these as they change esp and ebp ; for now just disregard */
            case OP_enter:
            case OP_leave:

		/* floating point control word stores and loads */
            case OP_fldcw:
            case OP_fnstcw:
            case OP_fnstsw:
            case OP_stmxcsr:
            case OP_fwait:

            case OP_nop_modrm:
            case OP_nop:
                break;

            default:
                unhandled = true;
                break;
        }

        logger.debug( "Op code skipped %d", rInstructions.size() );

        for (int i = 0 ; i < rInstructions.size() ; i++) {
            ReducedInstruction ins = rInstructions.get( i );
            ins.setFloating( cinst.getOpcode().isFloatingPointIns() );
        }
        return rInstructions;
    }

    public void setFloating(Boolean floating) {
        isFloating = floating;
    }

    public List<Operand> getSrcs() {
        return srcs;
    }

    //endregion Public methods

    public void setSrcs(List<Operand> srcs) {
        this.srcs = srcs;
    }

    private boolean isBounds(Output cinstr, int d, int s) {
        return ((cinstr.getNumOfDestinations() == d) && (cinstr.getNumOfSources() == s));
    }


    //endregion Private methods
}
