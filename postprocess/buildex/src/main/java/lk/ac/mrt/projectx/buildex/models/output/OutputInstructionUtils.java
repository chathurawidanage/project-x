package lk.ac.mrt.projectx.buildex.models.output;

import lk.ac.mrt.projectx.buildex.DefinesDotH;
import lk.ac.mrt.projectx.buildex.models.Pair;
import lk.ac.mrt.projectx.buildex.models.common.StaticInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Created by krv on 1/20/17.
 */
public class OutputInstructionUtils {
    final static Logger logger = LogManager.getLogger( OutputInstructionUtils.class );

    public static void updateRegsToMemRange(List<Pair<Output, StaticInfo>> instrs){
        logger.debug( "Coverting reg to memory" );
        for (int i = 0 ; i < instrs.size() ; i++) {
            Output instr = instrs.get( i ).first;
            for (int j = 0 ; j < instr.getSrcs().size() ; j++) {
                Operand srcOp = instr.getSrcs().get( j );
                if ((srcOp.getType() == MemoryType.REG_TYPE) &&
                        (((Integer) srcOp.getValue()) > DefinesDotH.DR_REG.DR_REG_ST7.ordinal())) {
                    srcOp.regToMemRange();

                }
            }
        }
    }

}
