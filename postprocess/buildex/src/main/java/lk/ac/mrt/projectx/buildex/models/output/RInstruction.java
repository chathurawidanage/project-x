package lk.ac.mrt.projectx.buildex.models.output;

import com.sun.org.apache.xpath.internal.operations.Bool;
import lk.ac.mrt.projectx.buildex.trees.*;
import lk.ac.mrt.projectx.buildex.trees.Operand;

import java.util.List;

/**
 * Created by krv on 1/12/17.
 */
public class RInstruction {

    //region Private variables

    private Integer operation;
    private Operand dst;
    private List<Operand> srcs;
    private Boolean sign;
    private Boolean isFloating;

    //endregion Private variables

    //region Public methods

    public Integer getOperation() {
        return operation;
    }

    public void setOperation(Integer operation) {
        this.operation = operation;
    }

    public Operand getDst() {
        return dst;
    }

    public void setDst(Operand dst) {
        this.dst = dst;
    }

    public List<Operand> getSrcs() {
        return srcs;
    }

    public void setSrcs(List<Operand> srcs) {
        this.srcs = srcs;
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

    /**
     * Set floating to true
     */
    public void setFloating() {
        isFloating = true;
    }

    //endregion Public methods
}
