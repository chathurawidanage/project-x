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
    private static String outputFolder;
    private static String filterFolder;
    {
        Map<String, String> env = System.getenv();
        outputFolder = env.get("EXALGO_OUTPUT_FOLDER");
        filterFolder = env.get("EXALGO_FILTER_FOLDER");

    }
    public static File getOutputFolder() {
        return new File(outputFolder);
    }

    public static File getFilterFolder() {
        return new File(filterFolder);
    }
}
