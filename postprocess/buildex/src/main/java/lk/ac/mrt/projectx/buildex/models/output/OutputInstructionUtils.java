package lk.ac.mrt.projectx.buildex.models.output;

import lk.ac.mrt.projectx.buildex.DefinesDotH;
import lk.ac.mrt.projectx.buildex.models.Pair;
import lk.ac.mrt.projectx.buildex.models.common.StaticInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static lk.ac.mrt.projectx.buildex.DefinesDotH.OpCodes.*;

/**
 * Created by krv on 1/20/17.
 */
public class OutputInstructionUtils {

    final static Logger logger = LogManager.getLogger( OutputInstructionUtils.class );

    //region Public Methods

    /**
     * update_floating_point_regs x86_analysis.cpp
     *
     * @param instrs
     * @param direction   whether forward analysis = 1, or backwards analysis = 2
     * @param staticInfos
     * @param pc
     * @return ::int updated tos value, tos is not a precondition but its value is changed inside this method
     */
    public static int updateFloatingPointRegs(List<Pair<Output, StaticInfo>> instrs, Integer direction,
                                              List<StaticInfo> staticInfos, List<Integer> pc) {
        logger.debug( "updating floating point regs" );
        int tos = DefinesDotH.DR_REG.DR_REG_ST8.ordinal();

        for (int i = 0 ; i < instrs.size() ; i++) {
            Output cinstr = instrs.get( i ).first;
            boolean unhandled = false;
            String disasm = getDisasmString( staticInfos, cinstr.getPc() );
            int line = i + 1;

            // this loop has no effect since tos is already set to the value changed by the loop
//            for (int j = 0 ; j < pc.size() ; j++) {
//                if (cinstr.getPc() == pc.get( j )) {
//                    tos = DefinesDotH.DR_REG.DR_REG_ST8.ordinal();
//                    break;
//                }
//            }

            if (!cinstr.getOpcode().isFloatingPointIns()) {
                cinstr.updateFPReg( disasm, i + 1 );
            } else {
                switch (cinstr.getOpcode()) {
                    case OP_fld: //Push m32fp onto the FPU register stack.
                    case OP_fld1: //Push +1.0 onto the FPU register stack
                    case OP_fild: //Push m32int onto the FPU register stack.
                    case OP_fldz: //Push +0.0 onto the FPU register stack.
                        if (direction == 1) {// Forward
                            cinstr.updateFPSrc( disasm, line );
                            tos = updateTos( tos, true, disasm, line, direction );
                            cinstr.updateFPDest( disasm, line );
                        } else if (direction == 2) {// backwards
                            cinstr.updateFPDest( disasm, line );
                            tos = updateTos( tos, true, disasm, line, direction );
                            cinstr.updateFPSrc( disasm, line );
                        }
                        break;
                    case OP_fst:
                        cinstr.updateFPReg( disasm, line );
                        break;
                    case OP_fstp:  //Copy ST(0) to m32fp and pop register stack.
                    case OP_fistp:  //Store ST(0) in m32int and pop register stack.
                        if (direction == 1) {// forwards
                            cinstr.updateFPReg( disasm, line );
                            tos = updateTos( tos, false, disasm, line, direction );
                        } else if (direction == 2) { // backwards
                            tos = updateTos( tos, false, disasm, line, direction );
                            cinstr.updateFPReg( disasm, line );
                        }
                        break;
                    case OP_fmul: //Multiply ST(0) by m32fp and store result in ST(0).
                    case OP_fmulp:  //Multiply ST(i) by ST(0), store result in ST(i), and pop the register stack.
                        if (cinstr.getOpcode() == OP_fmulp && direction == 2) { // backwards
                            tos = updateTos( tos, false, disasm, line, direction );
                        }
                        cinstr.updateFPReg( disasm, line );
                        if (cinstr.getOpcode() == OP_fmulp && direction == 1) { // forward
                            tos = updateTos( tos, false, disasm, line, direction );
                        }
                        break;
                    case OP_fxch:
                        cinstr.updateFPReg( disasm, line );
                        break; // todo : helium does not have this break
                    case OP_faddp:  //Add ST(0) to ST(i), store result in ST(i), and pop the register stack
                    case OP_fadd:   //Add m32fp to ST(0) and store result in ST(0).
                    case OP_fsubp:  //Subtract ST(0) from ST(1), store result in ST(1), and pop register stack.
                    case OP_fsub:   //Subtract m32fp from ST(0) and store result in ST(0).
                    case OP_fdivp:  //Divide ST(1) by ST(0), store result in ST(1), and pop the register stack.
                    case OP_fdiv:   //Divide ST(0) by m32fp and store result in ST(0).
                        if (((cinstr.getOpcode() == OP_faddp) || (cinstr.getOpcode() == OP_fsubp)
                                || (cinstr.getOpcode() == OP_fdivp)) && (direction == 2)) { // backward
                            tos = updateTos( tos, false, disasm, line, direction );
                        }
                        cinstr.updateFPReg( disasm, line );
                        if (((cinstr.getOpcode() == OP_faddp) || (cinstr.getOpcode() == OP_fsubp)
                                || (cinstr.getOpcode() == OP_fdivp)) && (direction == 1)) {
                            tos = updateTos( tos, false, disasm, line, direction );
                        }
                        break;
                    case OP_fcomp:
                        tos = updateTos( tos, false, disasm, line, direction );
                        break;
                    default:
                        unhandled = true;
                        break;
                }
                assert !unhandled : "ERROR: opcode "
                        + cinstr.getOpcode().toString() + "(" + cinstr.getOpcode().ordinal() + ")" + " with " +
                        cinstr.getDsts().size() + " dests and " + cinstr.getSrcs().size() + " srcs (app_pc - " +
                        cinstr.getPc() + ") not handled in canonicalization";
            }
        }
        return tos;
    }

    /**
     * Update tos x86_analysis.cpp
     *
     * @param tos       int current tos value
     * @param push      Whether the type is a push = true, or pop = false
     * @param disasm
     * @param line
     * @param direction whether forward analysis = 1, or backwards analysis = 2
     * @return ::int updated tos value
     */
    private static int updateTos(int tos, boolean push, String disasm, int line, Integer direction) {
        if (direction == 2) { // BACKWARD_ANALYSIS
            assert tos >= DefinesDotH.DR_REG.DR_REG_ST0.ordinal() : "Floating point stack overflow";
            assert tos < DefinesDotH.DR_REG.DR_REG_ST15.ordinal() : "Floating point stack underflow";
            if (push) {
                tos--;
            } else { // pop
                tos++;
            }
        } else if (direction == 1) { // FORWARDS_ANALYSIS
            assert tos >= DefinesDotH.DR_REG.DR_REG_ST0.ordinal() : "Floating point stack overflow";
            assert tos < DefinesDotH.DR_REG.DR_REG_ST15.ordinal() : "Floating point stack underflow";
            if (push) { // push
                tos++;
            } else { // pop
                tos--;
            }
        }
        return tos;
    }

    private static String getDisasmString(List<StaticInfo> statidInfo, long pc) {
        String disasm = null;
        for (int i = 0 ; i < statidInfo.size() ; i++) {
            StaticInfo staticInfo = statidInfo.get( i );
            if (staticInfo.getPc() == pc) {
                disasm = staticInfo.getDissasembly();
            }
        }
        return disasm;
    }

    //endregion Public Methods

    //region Private Methods

    public static void updateRegsToMemRange(List<Pair<Output, StaticInfo>> instrs) {
        logger.debug( "Coverting reg to memory" );
        for (int i = 0 ; i < instrs.size() ; i++) {
            Output instr = instrs.get( i ).first;
            for (int j = 0 ; j < instr.getSrcs().size() ; j++) {
                Operand srcOp = instr.getSrcs().get( j );
                updateRegsToMemRangeHelper( srcOp, j );
            }

            for (int j = 0 ; j < instr.getSrcs().size() ; j++) {
                Operand dstOp = instr.getDsts().get( j );
                updateRegsToMemRangeHelper( dstOp, j );
            }
        }
    }

    private static void updateRegsToMemRangeHelper(Operand op, int j) {
        if ((op.getType() == MemoryType.REG_TYPE) &&
                (((Integer) op.getValue()) > DefinesDotH.DR_REG.DR_REG_ST7.ordinal())) {
            op.regToMemRange();
            if (!op.getAddress().isEmpty()) { // TODO : check the logic x86_analysis.cpp line 1181 - 1185
                for (int k = 0 ; k < 4 ; k++) {
                    if ((op.getAddress().get( k ).getType() == MemoryType.REG_TYPE)
                            && (((Integer) op.getAddress().get( j ).getValue()) == 0)) {
                        continue;
                    }
                    op.getAddress().get( j ).regToMemRange();
                }
            }
        }
    }

    //endregion Private Methods
}
