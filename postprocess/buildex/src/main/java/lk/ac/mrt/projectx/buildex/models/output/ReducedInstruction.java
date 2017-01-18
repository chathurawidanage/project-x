package lk.ac.mrt.projectx.buildex.models.output;

import lk.ac.mrt.projectx.buildex.x86.X86Analysis.Operation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

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

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public void setOperation(Integer operation) {
        this.operation = Operation.values()[ operation ];
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

    public void setFloating(Boolean floating) {
        isFloating = floating;
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
                    Operand dst_low = new Operand( dst.getType(), 4, ((Integer) dst.getValue()) + dst.getWidth() - 4 );
                    Operand src_low = new Operand( src.getType(), 4, ((Integer) src.getValue()) + src.getWidth() - 4 );
                    if (dst.getType() == MemoryType.MEM_STACK_TYPE || dst.getType() == MemoryType.MEM_HEAP_TYPE) {
                        ReducedInstruction inst0 = new ReducedInstruction( Operation.op_assign, dst_low, true );
                        inst0.srcs.add( src_low );
                        // get upper bits
                        Operand dst_hight = new Operand( dst.getType(), 12, ((Integer) dst.getValue()) + dst.getWidth() - 16 );
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
                    amount = 2;
                    // create virtual reg/mem accesssing 32 bit input ranges tmp 64 bit reg and
                    // 2 tmp 32 bit reg
                    Operand input1 = cinst.getSrcs().get( 0 );
                    Operand input2 = cinst.getSrcs().get( 1 );
                    Operand output = cinst.getDsts().get( 0 );

                    // offset starting point by correct number of BYTES (divided by 8 to change
                    // from bits to bytes
                    Operand input32_1 = new Operand( input1.getType(), 4, ((Integer) input1.getValue()) + input1.getWidth() - 4 );
                    Operand input96_1 = new Operand( input1.getType(), 4, ((Integer) input1.getValue()) + input1.getWidth() - 12 );
                    Operand input32_2 = new Operand( input2.getType(), 4, ((Integer) input2.getValue()) + input2.getWidth() - 4 );
                    Operand input96_2 = new Operand( input2.getType(), 4, ((Integer) input2.getValue()) + input2.getWidth() - 12 );

                    // create virtual reg/mem accessing 8 byte ranges
                    Operand output_low = new Operand( output.getType(), 8, ((Integer) output.getValue()) + output.getWidth() - 8 );
                    Operand output_high = new Operand( output.getType(), 8, ((Integer) output.getValue()) + output.getWidth() - 16 );

                    ReducedInstruction inst0 = new ReducedInstruction( Operation.op_mul, output_low, true );
                    inst0.getSrcs().add( input32_1 );
                    inst0.getSrcs().add( input32_2 );
                    ReducedInstruction inst1 = new ReducedInstruction( Operation.op_mul, output_high, true );
                    inst1.getSrcs().add( input96_1 );
                    inst1.getSrcs().add( input96_2 );
                    rInstructions.add( inst0 );
                    rInstructions.add( inst1 );
                }
                break;
        }

        return rInstructions;
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
