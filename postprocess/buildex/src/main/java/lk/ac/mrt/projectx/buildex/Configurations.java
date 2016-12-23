package lk.ac.mrt.projectx.buildex;

import java.io.File;
import java.util.Map;

/**
 * This class contains default configurations required for buildex
 *
 * @author Chathura Widanage
 */
public class Configurations {
    /**
     * Output files from previous stages
     * @return
     */
    public static File getOutputFolder() {
        return new File(System.getenv().get("EXALGO_OUTPUT_FOLDER"));
    }

    public static File getFilterFolder() {
        return new File(System.getenv().get("EXALGO_FILTER_FOLDER"));
    }
}
