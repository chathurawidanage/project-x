package lk.ac.mrt.projectx.preprocess;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author krv
 * @version 0.9
 */
public class DRCoveStructure implements Cloneable {
    /**
     * @see
     */
    final static Logger logger = LogManager.getLogger(DRCoveStructure.class);


    Integer noOfBasicBlocks;
    Integer noOfModules;
    Integer drcovVersion;
    String drcovFlavour;
    String executable;
    ArrayList<Module> modules;
    ArrayList<Integer> duplicateIndexes;

    public DRCoveStructure(String executable) {
        this.executable = executable;
        modules = new ArrayList<>();
        duplicateIndexes = new ArrayList<>();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        DRCoveStructure clone = (DRCoveStructure) super.clone();
        clone.duplicateIndexes = (ArrayList) duplicateIndexes.clone();
        clone.modules = (ArrayList) modules.clone();
        return clone;
    }


    /**
     * This method will return all the unique modules and unique address of the
     * Basic Blocks
     *
     * @param other Second DRCovStructure extracted from the drcov file
     * @return List of Modules
     */
    public List GetDifference(DRCoveStructure other) {
        ArrayList<Module> diff = new ArrayList<>();

        // TODO : Check whether the first and second modules can be interchanged
        for (Module modT : modules) {   // Go  through modules of first structure
            if (modT.getOriginalIndex() != -1) {
                continue;
            }
            if (modT.getName().contains(executable)) { // Check wheher module name or the program monitored
                Integer index = other.modules.indexOf(modT); // find a matching module name from second structure
                if (index != -1) {  // if found a match
                    Module mod = other.modules.get(index); // get its index
                    Set<Long> set1 = mod.getAddresses();
                    Set<Long> set2 = modT.getAddresses();
                    ///TODO: Why not add new basic block on second run ?
//                    modT.getAddresses().addAll(set1);// merges the addresses of both modules
//                    set2.removeAll(set1);   // Overlap of addresses of both modules
                    modT.getAddresses().removeAll(set1);// merges the addresses of both modules
                }

                diff.add(modT); // finally add the module to the diff module list
                logger.info("Added to diff {}", modT);
            }
        }
        return diff;
    }

    /**
     * Print the sum of all the basic block addresses of each module, since using a hash set automatically
     * removes duplicates. Thus this total won't be same with total basic block count
     * Also Print the modules along with the basic block addresses belong to them
     */
    private void CheckStructureIntegrity() {
        int size = 0;
        for (Module mod : modules) {
            size += mod.getAddresses().size();
            logger.info("BBS  : {} Module {}", mod.getAddresses().size(), mod.getName());
        }
        logger.info("Total BBS : {}", size);
    }

    /**
     * Parse a drcov DR client output file and fill the DRCovStructure (this) class
     *
     * @param fileName Path of the drcov DR client output file
     */
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

            // Read all the module details
            // eg :  11, 40960, C:\Windows\syswow64\LPK.dll
            for (int i = 0; i < noOfModules; i++) {
                currentLine = bufferedReader.readLine();
                Module module = new Module();
                module.LoadByDRCovModuleLine(currentLine);
                Integer foundIndex = modules.indexOf(module);
                if (foundIndex != -1) { // Duplicate module name
                    duplicateIndexes.add(i);
                    module.setOriginalIndex(foundIndex);
                    logger.debug("Module {} is a duplicate of {}", i, foundIndex);
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
                result = Pattern.compile("\\[(.*)\\]:.*0x(.*),(.*)").matcher(currentLine);
                logger.debug("Group Count : {}", result.groupCount());
                Integer moduleNumber,size;
                Long startAddress;
                if (result.find()) {
                    moduleNumber = Integer.parseInt(result.group(1).trim());
                    startAddress = Long.parseLong(result.group(2).trim(), 16); //substring coz "0x"
                    size = Integer.parseInt(result.group(3).trim());
                    logger.debug("moduleNumber {}, startAddress {}, size {}", moduleNumber, startAddress, size);
                    if (moduleNumber >= noOfModules) {
                        invalidModules++;
                    } else {
                        // Updating starting addresses
                        if (duplicateIndexes.contains(moduleNumber)) {
                            int parentIndex = modules.get(moduleNumber).getOriginalIndex();
                            modules.get(parentIndex).getAddresses().add(startAddress);
                        } else {
                            //TODO : One Liner
                            Module temp = modules.get(moduleNumber);
                            temp.getAddresses().add(startAddress);
                        }
                    }
                    logger.debug("Original : {} moduleNumber : {}, startAddress : {} size : {}", currentLine, moduleNumber, startAddress, size);
                }
            }

            logger.info("Invalid BBS {}", invalidModules);
            logger.info("Valid BBS {}", noOfBasicBlocks - invalidModules);
            bufferedReader.close();
            fileReader.close();
        } catch (FileNotFoundException e) {
            logger.fatal(e.getMessage());
        } catch (IOException e) {
            logger.fatal(e.getMessage());
        }

        CheckStructureIntegrity();
    }
}

