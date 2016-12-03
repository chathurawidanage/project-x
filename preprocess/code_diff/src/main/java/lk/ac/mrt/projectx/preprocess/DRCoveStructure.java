
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

import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
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
    final static Options cmdLineOptions = CommandLineArgumentsOptions();

    Integer noOfBasicBlocks;
    Integer noOfModules;
    ArrayList<Module> modules;
    ArrayList<Integer> duplicateIndexes;
    ArrayList<Integer> locateOriginals;
    Integer drcovVersion;
    String drcovFlavour;

    public static void main(String[] args) {
        CommandLineParser parser = new DefaultParser();

        String[] test = {"-f","file_1","-second","file_2","-output","outputfile","-exec","Exec"};
        try {
            // parse the command line arguments
            CommandLine line = parser.parse( cmdLineOptions, test );
            System.out.println(line.getOptionValue("first"));
            System.out.println(line.getOptionValue("second"));
            System.out.println(line.getOptionValue("output"));
            System.out.println(line.getOptionValue("exec"));
        }
        catch( ParseException exp ) {
            // oops, something went wrong
            logger.error(exp.getMessage());
        }

//        DRCoveStructure dd = new DRCoveStructure();
//        dd.LoadFromFile("/home/krv/Projects/FYP/project-x/preprocess/code_diff/test/drcov.halide_blur_hvscan_test.exe.02112.0000.proc.log");
    }



    public static Options CommandLineArgumentsOptions() {
        Option first = Option.builder("f")
                .required(true)
                .longOpt("first")
                .type(String.class)
                .hasArg()
                .desc("First DRCov file")
                .build();

        Option second = Option.builder("s")
                .required(true)
                .longOpt("second")
                .type(String.class)
                .hasArg()
                .desc("Second DRCov file")
                .build();

        Option output = Option.builder("o")
                .required(true)
                .longOpt("output")
                .desc("Second DRCov file")
                .type(String.class)
                .hasArg()
                .build();

        Option exec = Option.builder("e")
                .required(true)
                .longOpt("exec")
                .desc("Name of the instrumented programme")
                .type(String.class)
                .hasArg()
                .build();

        Options cmdOptions = new Options();
        cmdOptions.addOption(first);
        cmdOptions.addOption(second);
        cmdOptions.addOption(output);
        cmdOptions.addOption(exec);

        return cmdOptions;
    }

    public void LoadFromFile(String fileName) {
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
                    startAddress = Integer.parseInt(result.group(2).trim(), 16);
                    size = 0;//Integer.parseInt(result.group(3).trim());
                    logger.debug("moduleNumber : {}, startAddress : {} size : {}", moduleNumber, startAddress, size);
                }
            }
            while ((currentLine = bufferedReader.readLine()) != null) {
//                Module module = new Module();
//                module.LoadByDRCovModuleLine(currentLine);
//                module.toString();

//                System.out.println(currentLine);
                index++;
            }


        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}

