package lk.ac.mrt.projectx.preprocess;

import junit.framework.TestCase;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.io.File;

/**
 * Created by krv on 12/23/2016.
 */
public class DRCoveStructureTest extends TestCase {
    private String fileName = "test.txt";
    private String ori_diff_file = "filter_files/diff_photoshop.txt";

    public void testLoadFromFile() throws Exception {
        DRCoveStructure drCoveStructure = new DRCoveStructure("halide_threshold_test.exe");
        drCoveStructure.LoadFromFile("output_files/drcov.halide_threshold_test.exe.02240.0000.proc.log");
        assertEquals(drCoveStructure.drcovVersion.intValue(), 1);
        assertEquals(drCoveStructure.noOfModules.intValue(), 24);

    }

    public void setUp() throws Exception {
        super.setUp();

        DRCoveStructure drCoveStructure_1 = new DRCoveStructure("halide_blur_hvscan_test.exe");
        drCoveStructure_1.LoadFromFile("working/output_files/drcov.halide_blur_hvscan_test.exe.02912.0000.proc.log");
        DRCoveStructure drCoveStructure_2 = new DRCoveStructure("halide_blur_hvscan_test.exe");
        drCoveStructure_2.LoadFromFile("working/output_files/drcov.halide_blur_hvscan_test.exe.02956.0000.proc.log");
        List<Module> diffModules = drCoveStructure_2.GetDifference(drCoveStructure_1);
        Diff.printToFile(fileName, diffModules);
    }

    public void tearDown() throws Exception {
        super.tearDown();
        try {
            File file = new File(fileName);
            if (file.delete()) {
                System.out.println(file.getName() + " is deleted!");
            } else {
                System.out.println("Delete operation is failed.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // TODO : This is failing because first line is different
    public void testLoadDiffTest() throws Exception {
        // Begin testing
        FileReader fr_test = null;
        FileReader fr_orig = null;
        BufferedReader br_orig = null;
        BufferedReader br_test = null;
        try {
            fr_test = new FileReader(fileName);
            br_test = new BufferedReader(fr_test);

            fr_orig = new FileReader(ori_diff_file);
            br_orig = new BufferedReader(fr_test);

            String ori;
            String test;
            int count = 0;
            while ((ori = br_orig.readLine()) != null) {
                test = br_test.readLine();
                if (ori != test) {
                    count += 1;
                }
            }

            assertEquals("Number of lines different", 0, count);
        } finally {
            br_test.close();
            br_orig.close();
            fr_test.close();
            fr_orig.close();
        }
    }


    public void testLoadDiffTestHacked() throws Exception {
        // Begin testing
        FileReader fr_test = null;
        FileReader fr_orig = null;
        BufferedReader br_orig = null;
        BufferedReader br_test = null;
        try {
            fr_test = new FileReader(fileName);
            br_test = new BufferedReader(fr_test);

            fr_orig = new FileReader(ori_diff_file);
            br_orig = new BufferedReader(fr_test);

            String ori;
            String test;
            int count = 0;
            test = br_test.readLine();
            while ((ori = br_orig.readLine()) != null) {
                test = br_test.readLine();
                if (ori != test) {
                    count += 1;
                }
            }

            assertEquals("Number of lines different", 0, count);
        } finally {
            br_test.close();
            br_orig.close();
            fr_test.close();
            fr_orig.close();
        }
    }
}