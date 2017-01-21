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

    //endregion Private methods
}
