package lk.ac.mrt.projectx.buildex;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * This class contains default configurations required for buildex
 *
 * @author Chathura Widanage
 */
public class Configurations {
    /**
     * Output files from previous stages
     *
     * @return
     */
    public static File getOutputFolder() {
        return getFolder("EXALGO_OUTPUT_FOLDER");
    }

    public static File getOutputFolderTest(String filter) {
        return new File(getTestsFolder(filter), "generated_files/output_files");
    }

    public static File getFilterFolder() {
        return getFolder("EXALGO_FILTER_FOLDER");
    }

    public static File getFilterFolderTest(String filter) {
        return new File(getTestsFolder(filter), "generated_files/filter_files");
    }

    public static File getIntermediateStructuresTest(String filter) {
        return new File(getTestsFolder(filter), "intermediate_structures");
    }

    public static File getImagesFolder() {
        return getFolder("EXALGO_FILTER_FOLDER");
    }

    public static File getImagesFolderTest() {
        return new File(getTestsFolder(""), "images");
    }

    private static File getTestsFolder(String filter) {
        try {
            File testResourcesDir = new File(Configurations.class.getClassLoader().
                    getResource("test_resources/" + filter).toURI());
            return testResourcesDir;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static File getFolder(String enVar) {
        return new File(System.getenv().get(enVar));
    }
}
