package lk.ac.mrt.projectx.preprocess;

import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by krv on 12/3/16.
 */
public class Diff {
    final static Options cmdLineOptions = CommandLineArgumentsOptions();
    final static Logger logger = LogManager.getLogger(Diff.class);

    public static void main(String[] args) {
        CommandLineParser parser = new DefaultParser();

        String[] test = {"-first","file_1","-second","file_2","-output","outputfile","-exec","Exec"};
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
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "codediff", cmdLineOptions,true);
        }

        DRCoveStructure dd = new DRCoveStructure();
        dd.LoadFromFile("/home/krv/Projects/FYP/project-x/preprocess/code_diff/test/drcov.halide_blur_hvscan_test.exe.02112.0000.proc.log");
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
