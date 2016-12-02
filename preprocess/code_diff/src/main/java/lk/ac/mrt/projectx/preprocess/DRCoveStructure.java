
package lk.ac.mrt.projectx.preprocess;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author krv
 * @version 0.9
 *
 */
public class DRCoveStructure {
    /**
     * @see
     */
    final static Logger logger = LogManager.getLogger(DRCoveStructure.class);

    Integer noOfBasicBlocks;
    Integer noOfModules;
    ArrayList<Module> modules;
    ArrayList<Integer> duplicateIndexes;
    ArrayList<Integer> locateOriginals;
    Integer drcovVersion;
    String drcovFlavour;

    public static void main(String[] args) {
        DRCoveStructure dd = new DRCoveStructure();
        dd.LoadFromFile("/home/krv/Projects/FYP/project-x/preprocess/code_diff/test/drcov.halide_blur_hvscan_test.exe.02112.0000.proc.log");
    }

    public void LoadFromFile(String fileName){
        BufferedReader bufferedReader = null;
        FileReader fileReader = null;
        String currentLine;
        Integer index = 0;
        try {
            fileReader = new FileReader(fileName);
            bufferedReader = new BufferedReader(fileReader);

            // Read the first line which indicates DRCov version
            // eg : DRCOV VERSION: 1
            currentLine = bufferedReader.readLine();
            logger.debug("First line",currentLine);
            Matcher result = Pattern.compile("\\d+").matcher(currentLine);
            if(result.find()) {
                drcovVersion = Integer.parseInt(result.group().toString());
            }
            logger.info("DRCov version ", drcovVersion);

            // Only version 2 files include flavour line
            // eg :
            if(drcovVersion == 2){
                currentLine = bufferedReader.readLine();
                result = Pattern.compile("\\d+").matcher(currentLine);
                if(result.find()) {
                    drcovFlavour = result.group().toString();
                }
                logger.info("DRCov Flavour ", currentLine);
            }

            // Get the number of modules
            // eg : Module Table: 24
            currentLine = bufferedReader.readLine();
            result = Pattern.compile("\\d+").matcher(currentLine);
            if(result.find()) {
                noOfModules = Integer.parseInt(result.group().toString());
            }
            logger.info("Number of modules ", noOfModules);

            logger.warn("Parsing line DRCov models assuming second number is decimal");
            modules = new ArrayList<>();
            duplicateIndexes = new ArrayList<>();
            // Read all the module details
            // eg :  11, 40960, C:\Windows\syswow64\LPK.dll
            for (Integer i = 0; i < noOfModules; i++){
                currentLine = bufferedReader.readLine();
                Module module = new Module();
//                System.out.println(currentLine);
                module.LoadByDRCovModuleLine(currentLine);
                Integer foundIndex = modules.indexOf(module);
                if (foundIndex != -1){
                    duplicateIndexes.add(i);
                    module.setOriginalIndex(foundIndex);
                }
                modules.add(module);
            }

            while ((currentLine = bufferedReader.readLine()) != null) {
//                Module module = new Module();
//                module.LoadByDRCovModuleLine(currentLine);
//                module.toString();

//                System.out.println(currentLine);
                index++;
            }


        }catch (FileNotFoundException e){
            logger.error(e.getMessage());
        }catch(IOException e){
            logger.error(e.getMessage());
        }
    }
}

