package lk.ac.mrt.projectx.buildex.models.output;

import lk.ac.mrt.projectx.buildex.trees.Operand;
import lk.ac.mrt.projectx.buildex.x86.X86Analysis.Operation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Created by krv on 1/12/17.
 */
public class RInstruction {

    final static Logger logger = LogManager.getLogger(RInstruction.class);
    //region Private variables

    private Operation operation;
    private Operand dst;
    private List<Operand> srcs;
    private Boolean sign;
    private Boolean isFloating;
    private Integer numRinstr;
    //endregion Private variables

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

    //region Private methods

    /**
     *  Pure function without side effects for cinstr (output)
     *  TODO : check to make sure
     * @param cinst - pointer to a pre-populated complex x86 instruction
     * @param disams - string of diassembly of 'cinstr'. Can be used for debugging purposes
     * @param line - the line in which this instruction can be found in the instruction trace (after filtering)
     * @return amount - how many reduced set instruction were needed to canonicalize the given x86 instruction
     */
    private Integer cinstrToRinstr(Output cinst, String disams, Integer line){
        logger.debug("Enter canonicalization - app_pc");
        Integer amount = 0;
        RInstruction rInstruction = new RInstruction();

        //lets see whether this works

        return amount;
    }
    //endregion Private methods
}
