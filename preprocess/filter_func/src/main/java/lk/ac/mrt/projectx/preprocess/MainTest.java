package lk.ac.mrt.projectx.preprocess;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

import lk.ac.mrt.projectx.buildex.ProjectXImage;

import javax.imageio.ImageIO;

/**
 * Created by Lasantha on 02-Dec-16.
 */

public class MainTest {
    private static final Logger logger = LogManager.getLogger(MainTest.class);

    public static void main(String[] args) {
        MainTest mainTest = new MainTest();
        try {
            mainTest.runAlgorithmDiffMode();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MainTest() {
        this.outputFolderPath = "E:\\FYP\\Java Ported\\Test Files\\output_files";
        this.imageFolderPath = "E:\\FYP\\Java Ported\\Test Files\\images";
        this.inImageFileName = "arith.png";
        this.outImageFileName = "aritht.png";
        this.exeFileName = "halide_threshold_test.exe";
        this.threshold = 80;
        this.filterMode = 1;
        this.bufferSize = 0;
        this.profileData = new ArrayList<byte[]>();
        this.memtraceData = new ArrayList<byte[]>();

        initialize();
    }

    public void initialize() {
        readMemtraceAndProfileFiles();
    }

    private final int DIFF_MODE = 1;
    private final int TWO_IMAGE_MODE = 2;
    private final int ONE_IMAGE_MODE = 3;

    private String outputFolderPath;
    private String imageFolderPath;
    private String inImageFileName;
    private String outImageFileName;
    private String exeFileName;
    private int filterMode;
    private int bufferSize;
    private int threshold;  // continuous chunck % of image
    private ArrayList<byte[]> profileData;
    private ArrayList<byte[]> memtraceData;


    private void readMemtraceAndProfileFiles() {

        String profileFileNameFormat = "profile_" + exeFileName + "_" + inImageFileName;
        String memtraceFileNameFormat = "memtrace_" + exeFileName + "_" + inImageFileName;

        // go through all the files in the output folder to find matches for profile and memtrace
        File folder = new File(outputFolderPath);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                String fileName = listOfFiles[i].getName();

                byte[] data;
                if (fileName.contains(profileFileNameFormat)) {
                    // read profile files
                    data = getFileContent(listOfFiles[i].getPath());
                    profileData.add(data);
                } else if (fileName.contains(memtraceFileNameFormat)) {
                    // read memtrace files
                    data = getFileContent(listOfFiles[i].getPath());
                    memtraceData.add(data);
                }
            }
        }

    }

    public void runAlgorithmDiffMode() throws IOException {

        logger.info("Filter function DIFF MODE");

        ModuleInfo module = ModuleInfo.getPopulatedModuleInfo(profileData.get(0));

        ProjectXImage inImage = new ProjectXImage(ImageIO.read(new File(imageFolderPath + "\\" + inImageFileName)));
        logger.info("Input Image Read Done! - {}", imageFolderPath + "\\" + inImageFileName);

        ProjectXImage outImage = new ProjectXImage(ImageIO.read(new File(imageFolderPath + "\\" + outImageFileName)));
        logger.info("Output Image Read Done! - {}", imageFolderPath + "\\" + outImageFileName);

        // getting the highest executed basic block
        logger.info("Finding the highest executed basic block...");

        ModuleInfo maxModule = null;
        ModuleInfo tempModule = module;
        int maxFrequency = 0;
        BasicBlockInfo maxBasicBlock = null;
        while (tempModule != null) {
            ArrayList<FunctionInfo> functions = tempModule.getFunctions();
            for (int i = 0; i < functions.size(); i++) {
                ArrayList<BasicBlockInfo> bbs = functions.get(i).getBasicBlocks();
                for (int j = 0; j < bbs.size(); j++) {
                    BasicBlockInfo bb = bbs.get(j);
                    if (bb.getFrequency() > maxFrequency) {
                        maxFrequency = bb.getFrequency();
                        maxBasicBlock = bb;
                        maxModule = tempModule;
                    }
                }
            }
            tempModule = tempModule.getNext();
        }
        logger.info("max module - {}, max start addr - {}", maxModule.getName(), maxBasicBlock.getStartAddress());

        logger.info("Finding the probable function...");

        long maxFunction = getProbableFunction(maxModule, maxBasicBlock.getStartAddress());

        logger.info("Enclosed function = {}", maxFunction);

        /* parsing memtrace files to pc_mem_regions */

        logger.info("Getting memory region information...");
        ArrayList<PcMemoryRegion> pcMems = PcMemoryRegion.getMemRegionFromMemTrace(memtraceData, module);

        logger.info("linking memory regions together...");
        PcMemoryRegion.linkMemRegions(pcMems, 1);    //TODO not tested

        logger.info("filtering out insignificant regions...");
        /* all memory related information */
        /************* Skipped because not using this ***********/

        if (bufferSize == 0) {
            PcMemoryRegion.filterMemRegions(pcMems, inImage, outImage, threshold);
        } else {
            //TODO not implemented because not using here
            //filter_mem_regions_total(pc_mems, total_size, threshold);
        }

        logger.info("Memory regions filtering - DONE!");

        ArrayList<InternalFunctionInfo> funcInfo = new ArrayList<>();

        /* get the pc_mems and there functional info as well as filter the pc_mems which are not in the func */
        for (int i = 0; i < pcMems.size(); i++) {
            logger.info("Entered finding funcs...");
            ModuleInfo md = ModuleInfo.findModuleByName(module, pcMems.get(i).getModule());
            if (md == null) {
                logger.error("ERROR: the module should be present");
            }

            BasicBlockInfo bbInfo = BasicBlockInfo.findBasicBlock(md, pcMems.get(i).getPc());
            if (bbInfo == null) {
                logger.error("ERROR: bbinfo should be present");
            }

            long funcStart = getProbableFunction(md, bbInfo.getStartAddress());

            if (funcStart == 0) {
                continue;
            }

            logger.info("module - {}, start - {} (in dec)",pcMems.get(i).getModule(),funcStart);

            boolean isThere = false;
            int index = 0;
            for (int j = 0; j < funcInfo.size(); j++){
                InternalFunctionInfo func = funcInfo.get(j);
                if (func.address == funcStart && func.name.equals(md.getName())){
                    isThere = true;
                    index = j;
                    break;
                }
            }

            if (!isThere){
                InternalFunctionInfo newFunc = new InternalFunctionInfo();
                newFunc.name = md.getName();
                newFunc.address = funcStart;
                newFunc.frequency = 1;
                newFunc.candidateInstructions.add(pcMems.get(i).getPc());
                newFunc.bbStart.add(bbInfo.getStartAddress());
                funcInfo.add(newFunc);
            }
            else{
                funcInfo.get(index).frequency++;
                boolean found = false;
                for (int j = 0; j < funcInfo.get(index).candidateInstructions.size(); j++){
                    if (pcMems.get(i).getPc() == funcInfo.get(index).candidateInstructions.get(j)){
                        found = true;
                        break;
                    }
                }
                if (!found){
                    funcInfo.get(index).candidateInstructions.add(pcMems.get(i).getPc());
                    funcInfo.get(index).bbStart.add(bbInfo.getStartAddress());
                }
            }
        }

    }


    private static final int MAX_RECURSE = 200;

    private static long getProbableFuncEntrypoint(ModuleInfo current, LinkedList<RetAddress> bbStart, ArrayList<Integer> processed, int maxRecurse) {
        if (maxRecurse > MAX_RECURSE) {
            logger.warn("WARNING: max recursion limit reached!");
            return 0;
        }

        if (bbStart.isEmpty()) {
            return 0;
        }

        RetAddress retAddress = bbStart.poll();
        processed.add(retAddress.address);

        BasicBlockInfo bbinfo = findBbExact(current, retAddress.address);
        if (bbinfo == null) {
            return getProbableFuncEntrypoint(current, bbStart, processed, maxRecurse + 1);
        }

        if (bbinfo.isCallTarget()) {
            retAddress.ret--;
        }
        if (retAddress.ret < 0) {
            return bbinfo.getStartAddress();
        }

        if (bbinfo.isRet() && maxRecurse > 0) {
            retAddress.ret++;
        }

        logger.info("Addr : {} , ret : {} , Freq : {}", retAddress.address, retAddress.ret, bbinfo.getFrequency());

        for (int i = 0; i < bbinfo.getFromBasicBlocks().size(); i++) {
            if (!processed.contains(bbinfo.getFromBasicBlocks().get(i).getTarget())) {
                bbStart.add(new RetAddress(bbinfo.getFromBasicBlocks().get(i).getTarget(), retAddress.ret));
            }
        }
        return getProbableFuncEntrypoint(current, bbStart, processed, maxRecurse + 1);
    }

    private static BasicBlockInfo findBbExact(ModuleInfo module, long addr) {
        for (int i = 0; i < module.getFunctions().size(); i++) {
            FunctionInfo func = module.getFunctions().get(i);
            for (int j = 0; j < func.getBasicBlocks().size(); j++) {
                BasicBlockInfo bb = func.getBasicBlocks().get(j);
                if (bb.getStartAddress() == addr) {
                    return bb;
                }
            }
        }
        return null;
    }

    private static class RetAddress {
        int ret;
        int address;

        public RetAddress(int address, int ret) {
            this.ret = ret;
            this.address = address;
        }
    }

    private static class InternalFunctionInfo {
        String name;
        long address;
        int frequency;
        ArrayList<Integer> candidateInstructions;
        ArrayList<Long> bbStart;

        public InternalFunctionInfo() {
            this.candidateInstructions = new ArrayList<>();
            this.bbStart = new ArrayList<>();
        }
    }

    private static long getProbableFunction(ModuleInfo current, long startAddr) {

        BasicBlockInfo bb = BasicBlockInfo.findBasicBlock(current, startAddr);
        if (bb != null) {
            RetAddress retAddress = new RetAddress((int) bb.getStartAddress(), 0);
            LinkedList<RetAddress> queue = new LinkedList<>();
            queue.add(retAddress);
            ArrayList<Integer> processed = new ArrayList<>();

            return getProbableFuncEntrypoint(current, queue, processed, 0);
        } else {
            return 0;
        }
    }

    private byte[] getFileContent(String filename) {
        byte[] data = null;
        logger.info("Reading file {}", filename);
        try {
            data = Files.readAllBytes(Paths.get(filename));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return data;
    }

}
