package lk.ac.mrt.projectx.buildex;

import java.io.File;

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
        return new File("F:\\engineering\\fyp\\gens\\generated_files\\output_files");
    }
}
