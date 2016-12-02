package lk.ac.mrt.projectx.buildex;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by wik2kassa on 12/2/2016.
 */
public class InstructionTracer {
    private static final Logger logger = LogManager.getLogger(InstructionTracer.class);
    private static InstructionTracer instance = new InstructionTracer();

    public  static InstructionTracer getInstance() {
        return instance;
    }

    private InstructionTracer() {

    }
}
