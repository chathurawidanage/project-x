package lk.ac.mrt.projectx.preprocess;

import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by krv on 12/3/16.
 */
public class Diff {
    final static Options cmdLineOptions = CommandLineArgumentsOptions();
    final static Logger logger = LogManager.getLogger(Diff.class);

    public static void main(String[] args) {
        CommandLineParser parser = new DefaultParser();

        try {
            // parse the command line arguments
            CommandLine line = parser.parse(cmdLineOptions, args);
            logger.info("first file = {}", line.getOptionValue("first"));
            logger.info("second file = {}", line.getOptionValue("second"));
            logger.info("output file = {}", line.getOptionValue("output"));
            logger.info("exec name = {}", line.getOptionValue("exec"));

            DRCoveStructure firstStruct = new DRCoveStructure(line.getOptionValue("exec"));
            DRCoveStructure secondtStruct = new DRCoveStructure(line.getOptionValue("exec"));
            firstStruct.LoadFromFile(line.getOptionValue("first"));
            secondtStruct.LoadFromFile(line.getOptionValue("second"));
            List<Module> diffModules = firstStruct.GetDifference(secondtStruct);
            printToFile(line.getOptionValue("output"), diffModules);
        } catch (ParseException exp) {
            // oops, something went wrong
            logger.fatal(exp.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("codediff", cmdLineOptions, true);
        } catch (IOException e) {
            logger.fatal(e.getMessage());
        }
    }

    public static void printToFile(String fileName, List<Module> diffModules) {
        BufferedWriter bufferedWriter = null;
        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(fileName);
            bufferedWriter = new BufferedWriter(fileWriter);
            for (Module mod : diffModules) {
                bufferedWriter.write(mod.getId() + "\n");
                bufferedWriter.write(mod.getName().trim() + "\n");
                logger.info(mod.getName());
                List<Long> addresses = new ArrayList<>();
                addresses.addAll(mod.getAddresses());
                Collections.sort(addresses);
                for (Long address : addresses) {
                    bufferedWriter.write(address.toString() + '\n');
                    logger.debug("Address {}", address.toString());
                }

            }
            bufferedWriter.close();
            fileWriter.close();
        } catch (FileNotFoundException e) {
            logger.fatal(e.getMessage());
        } catch (IOException e) {
            logger.fatal(e.getMessage());
        }
    }

    public static Options CommandLineArgumentsOptions() {
        Option first = Option.builder("f")
                .required(true)
                .longOpt("first")
                .argName("FILE")
                .type(String.class)
                .hasArg()
                .desc("First DRCov file")
                .build();

        Option second = Option.builder("s")
                .required(true)
                .longOpt("second")
                .argName("FILE")
                .type(String.class)
                .hasArg()
                .desc("Second DRCov file")
                .build();

        Option output = Option.builder("o")
                .required(true)
                .longOpt("output")
                .argName("FILE")
                .desc("Code Diff file to be generated")
                .type(String.class)
                .hasArg()
                .build();

        Option exec = Option.builder("e")
                .required(true)
                .longOpt("exec")
                .argName("EXAMPLE.exe")
                .desc("Name of the instrumented programme")
                .type(String.class)
                .hasArg()
                .build();

        Options cmdOptions = new Options();
        cmdOptions.addOption(first);
        cmdOptions.addOption(second);
        cmdOptions.addOption(output);
        cmdOptions.addOption(exec);
        cmdOptions.addOption(Option.builder("h").longOpt("help").desc("Show this help").build());

        return cmdOptions;
    }
}
