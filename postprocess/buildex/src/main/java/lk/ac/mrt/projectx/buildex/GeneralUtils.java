package lk.ac.mrt.projectx.buildex;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Chathura Widanage
 */
public class GeneralUtils {
    private final static Logger logger = LogManager.getLogger(GeneralUtils.class);

    public static void assertAndFail(boolean assertOperation, String errorLog) {
        if (!assertOperation) {
            logger.fatal(errorLog);
            System.exit(0);
        }
    }
}
