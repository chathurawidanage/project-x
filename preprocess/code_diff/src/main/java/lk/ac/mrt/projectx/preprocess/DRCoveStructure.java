
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

import org.apache.commons.cli.Options;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author krv
 * @version 0.9
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

    public DRCoveStructure() {
    }

    public DRCoveStructure(String fileName) {
        LoadFromFile(fileName);
    }

    public void LoadFromFile(String fileName) {
        BufferedReader bufferedReader = null;
        FileReader fileReader = null;
        String currentLine;
        Integer index = 0;
        Integer invalidModules = 0;


        try {
            fileReader = new FileReader(fileName);
            bufferedReader = new BufferedReader(fileReader);

            // Read the first line which indicates DRCov version
            // eg : DRCOV VERSION: 1
            currentLine = bufferedReader.readLine();
            logger.debug("First line : {} ", currentLine);
            Matcher result = Pattern.compile("\\d+").matcher(currentLine);
            if (result.find()) {
                drcovVersion = Integer.parseInt(result.group().toString());
            }
            logger.info("DRCov version : {} ", drcovVersion);

            // Only version 2 files include flavour line
            // eg :
            if (drcovVersion == 2) {
                currentLine = bufferedReader.readLine();
                result = Pattern.compile("\\d+").matcher(currentLine);
                if (result.find()) {
                    drcovFlavour = result.group().toString();
                }
                logger.info("DRCov Flavour {}", currentLine);
            }

            // Get the number of modules
            // eg : Module Table: 24
            currentLine = bufferedReader.readLine();
            result = Pattern.compile("\\d+").matcher(currentLine);
            if (result.find()) {
                noOfModules = Integer.parseInt(result.group().toString());
            }
            logger.info("Number of modules : {}", noOfModules);

            logger.warn("Parsing line DRCov models assuming second number is decimal");
            modules = new ArrayList<>();
            duplicateIndexes = new ArrayList<>();
            // Read all the module details
            // eg :  11, 40960, C:\Windows\syswow64\LPK.dll
            for (int i = 0; i < noOfModules; i++) {
                currentLine = bufferedReader.readLine();
                Module module = new Module();
//                System.out.println(currentLine);
                module.LoadByDRCovModuleLine(currentLine);
                Integer foundIndex = modules.indexOf(module);
                if (foundIndex != -1) {
                    duplicateIndexes.add(i);
                    module.setOriginalIndex(foundIndex);
                }
                modules.add(module);
            }
            logger.info("Number of Duplicate Modules {}", duplicateIndexes.size());

            currentLine = bufferedReader.readLine();

            // Get the number of basic blocks
            // eg : BB Table: 21058 bbs
            result = Pattern.compile("(\\d+)").matcher(currentLine);
            if (result.find()) {
                noOfBasicBlocks = Integer.parseInt(result.group(0).toString());
            }
            logger.info("Number of Basic Blocks {}", noOfBasicBlocks);


            // Parsing the basic block lines
            // eg : module[ 21]: 0x000101c4,  13
            // Module number start_address size
            for (int i = 0; i < noOfBasicBlocks; i++) {
                currentLine = bufferedReader.readLine();
                result = Pattern.compile("\\[ (\\d+)\\]: (0x[abcdef]*\\d*[abdcdef]*\\d*),.+(\\d+)").matcher(currentLine);
                logger.debug("Group Count : {}", result.groupCount());
                Integer moduleNumber, startAddress, size;
                if (result.find()) {
                    moduleNumber = Integer.parseInt(result.group(1).trim());
                    startAddress = Integer.parseInt(result.group(2).substring(2).trim(), 16); //substring coz "0x"
                    size = Integer.parseInt(result.group(3).trim());
                    if(moduleNumber >= noOfModules){
                        invalidModules ++;
                    }else {
                        // Updating starting addresses
                        if(duplicateIndexes.contains(moduleNumber)){
                            int parentIndex = modules.get(moduleNumber).getOriginalIndex();
                            modules.get(parentIndex).getAddresses().add(startAddress);
                        }else{
                            modules.get(moduleNumber).getAddresses().add(startAddress);
                        }

                    }
                    logger.debug("Original : {} moduleNumber : {}, startAddress : {} size : {}", currentLine, moduleNumber, startAddress, size);
                }
            }

            logger.info("Invalid BBS {}", invalidModules);
            logger.info("Valid BBS {}", noOfBasicBlocks-invalidModules);

        } catch (FileNotFoundException e) {
            logger.fatal(e.getMessage());
        } catch (IOException e) {
            logger.fatal(e.getMessage());
        }
    }
}

