package lk.ac.mrt.projectx.buildex.complex;

import lk.ac.mrt.projectx.buildex.complex.cordinates.CartesianCoordinate;
import lk.ac.mrt.projectx.buildex.complex.operations.Attribute;
import lk.ac.mrt.projectx.buildex.complex.operations.Guess;
import lk.ac.mrt.projectx.buildex.models.Pair;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Chathura Widanage
 */
public class TestSolver {
    private final static Logger logger = LogManager.getLogger(TestSolver.class);

    public static void main(String[] args) throws Exception {

        Options options = new Options();
        options.addOption("i", true, "input image");
        options.addOption("e", true, "examples file");
        options.addOption("a", true, "user argument");

        CommandLineParser parser = new BasicParser();
        CommandLine parse = parser.parse(options, args);

        String inputImageName = null;
        if (!parse.hasOption("i")) {
            System.out.println("Input image is required");
            return;
        } else {
            inputImageName = parse.getOptionValue("i");
            System.out.println(inputImageName);
        }

        BufferedImage in = ImageIO.read(new File(inputImageName));

        String exampleFileName = null;
        if (!parse.hasOption("e")) {
            System.out.println("Example file is required");
            return;
        } else {
            exampleFileName = parse.getOptionValue("e");
        }

        List<Attribute> attributeList = new ArrayList<>();

        if (parse.hasOption("a")) {
            String atts[] = parse.getOptionValues("a");
            for (int i = 0; i < atts.length; i++) {
                Attribute userAttr = new Attribute("attribute" + i, String.format("(%s)", atts[i]), Double.parseDouble(atts[i]), true);
                attributeList.add(userAttr);
            }
        }


        /*int x = 800;
        int y = 600;
        BufferedImage in = new BufferedImage(x, y, BufferedImage.TYPE_3BYTE_BGR);
        BufferedImage out = new BufferedImage(x, y, BufferedImage.TYPE_3BYTE_BGR);*/
        InductiveSynthesizerNew inductiveSynthesizer = new InductiveSynthesizerNew();

        ExamplesFile examplesFile = new ExamplesFile();

       /* List<Pair<CartesianCoordinate, CartesianCoordinate>>
                examples = examplesFile.read(".", "C:\\Users\\chath\\Desktop\\ExampleGenerotor\\examples\\examples_1487779456410.csv");
*/
        List<Pair<CartesianCoordinate, CartesianCoordinate>>
                examples = examplesFile.read(".", exampleFileName);


        //SourceDestinationSeeker eg = new SourceDestinationSeeker();
        //eg.generate(in,out);


        long startT = System.currentTimeMillis() / (1000 * 60);
        try {
            Pair<Guess, Guess> solve = inductiveSynthesizer.solve(examples, in, in, attributeList);

           /* logger.debug("Solve {}", solve);
            x = 600;
            y = 800;
            in = new BufferedImage(x, y, BufferedImage.TYPE_3BYTE_BGR);
            out = new BufferedImage(x, y, BufferedImage.TYPE_3BYTE_BGR);
            inductiveSynthesizer = new InductiveSynthesizerNew();
            Pair<Guess, Guess> solve2 = inductiveSynthesizer.solve(new PolarGenerator().generate(x, y)*//*eg.generate(in,out)*//*, in, out);
            logger.debug("solve 2 {}",solve2);*/

        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("Synthesized in {}minutes", System.currentTimeMillis() / (1000 * 60) - startT);
    }
}
