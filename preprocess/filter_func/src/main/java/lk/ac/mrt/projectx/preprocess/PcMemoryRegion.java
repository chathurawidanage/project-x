package lk.ac.mrt.projectx.preprocess;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Lasantha on 11-Dec-16.
 */
public class PcMemoryRegion {
    private static final Logger logger = LogManager.getLogger(PcMemoryRegion.class);

    private static final int MEM_HEAP_TYPE = 2;
    //directions
    private static final int MEM_INPUT = 1;
    private static final int MEM_OUTPUT = 2;


    /* at least pc should be populated */
    private String module;
    private int pc;

    private ArrayList<PcMemoryRegion> fromRegions;  /* these are memory depedancies */
    private ArrayList<MemoryInfo> regions;
    private ArrayList<PcMemoryRegion> toRegions;   /* these are memory dependencies */


    public PcMemoryRegion() {
        fromRegions = new ArrayList<>();
        regions = new ArrayList<>();
        toRegions = new ArrayList<>();
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public int getPc() {
        return pc;
    }

    public void setPc(int pc) {
        this.pc = pc;
    }

    public ArrayList<PcMemoryRegion> getFromRegions() {
        return fromRegions;
    }

    public void setFromRegions(ArrayList<PcMemoryRegion> fromRegions) {
        this.fromRegions = fromRegions;
    }

    public ArrayList<MemoryInfo> getRegions() {
        return regions;
    }

    public void setRegions(ArrayList<MemoryInfo> regions) {
        this.regions = regions;
    }

    public ArrayList<PcMemoryRegion> getToRegions() {
        return toRegions;
    }

    public void setToRegions(ArrayList<PcMemoryRegion> toRegions) {
        this.toRegions = toRegions;
    }


    public static ArrayList<PcMemoryRegion> getMemRegionFromMemTrace(ArrayList<byte[]> memtraceData, ModuleInfo head) {

        ArrayList<PcMemoryRegion> pcMems = new ArrayList<>();

        for (int i = 0; i < memtraceData.size(); i++) {
            Scanner data = new Scanner(new String(memtraceData.get(i)));

            while (data.hasNextLine()) {
                String[] tokens = data.nextLine().split(",");

                MemoryInput input = new MemoryInput();
                ModuleInfo module = ModuleInfo.findModule(head, Long.parseLong(tokens[0], 16));

                if (module != null) {
                    input.setModule(module.getName());
                    input.setPc(Integer.parseInt(tokens[1], 16));
                    input.setWrite((Integer.parseInt(tokens[2])) == 1);
                    input.setStride(Integer.parseInt(tokens[3]));
                    input.setType(MEM_HEAP_TYPE);
                    input.setMemAddress(Long.decode(tokens[4]));
                    updateMemRegionsMain(pcMems,input);
                } else {
                    logger.warn("WARNING: cannot find a module for {} address", Long.parseLong(tokens[0], 16));
                }

            }
            logger.info("files {}/{} is read",i+1,memtraceData.size());
        }

        logger.info("defragmenting and updating strides...");
        logger.info("found {} pc mem regions for post processing",pcMems.size());

        for (int i = 0; i < pcMems.size(); i++){
            defragmentRegions(pcMems.get(i).getRegions());
            updateMostProbStride(pcMems.get(i).getRegions());
        }
        logger.info("Defragmenting and updating strides done!");
        return pcMems;
    }

    /* this rountine is sound defragmentation */
    static void defragmentRegions(ArrayList<MemoryInfo> memInfo){

	/*this will try to merge individual chunks of memory units info defragmented larger chunks*/
        boolean finished = false;

        while (!finished){

            int startSize = memInfo.size();

            for (int i = 0; i < memInfo.size(); i++){

                MemoryInfo candidate = memInfo.get(i);

                for (int j = i + 1; j < memInfo.size(); j++){

                    MemoryInfo current = memInfo.get(j);
                    if (current == candidate) continue;  /* this never happens? */

				/*check if they can be merged*/
                    if (current.getType() == candidate.getType()){

					/*can we merge the two? we will always update the candidate and delete the current if this can be merged*/
					/* we cannot possibly have a mem region captured within a mem region if update mem regions has done its job*/

                        boolean merged = false;

					/*if the candidate is a subset of the current? remove candidate*/

                        if((candidate.getStart()>=current.getStart()) && (candidate.getEnd() <= current.getEnd())){
                            current.setDirection(current.getDirection()|candidate.getDirection());
                            updateStrideFromVector(current.getStrideFreqs(), candidate.getStrideFreqs());
                            memInfo.remove(i);
                            i--;
                            break;
                        }

					/*if current is a subset of the candidate? remove current*/
                        else if ((current.getStart() >= candidate.getStart()) && (current.getEnd() <= candidate.getEnd())){
                            merged = true;
                        }
					/* prepend to the candidate?*/
                        else if ((current.getStart() < candidate.getStart()) && (current.getEnd() >= candidate.getStart())){
                            candidate.setStart(current.getStart());
                            merged = true;
                        }
					/* append to candidate?*/
                        else if ((current.getStart() <= candidate.getEnd()) && (current.getEnd() > candidate.getEnd())){
                            candidate.setEnd(current.getEnd());
                            merged = true;
                        }

                        if (merged){
                            candidate.setDirection(candidate.getDirection()|current.getDirection());
                            updateStrideFromVector(candidate.getStrideFreqs(),current.getStrideFreqs());
                            memInfo.remove(j);
                            j--;
                        }

                    }

                }
            }

            int endSize = memInfo.size();

            finished = (startSize == endSize);

        }


    }

    /* update most prob stride information */
    private static void updateMostProbStride(ArrayList<MemoryInfo> memInfo){

        for (int i = 0; i < memInfo.size(); i++){
            int stride = getMostProbableStride(memInfo.get(i).getStrideFreqs());
            memInfo.get(i).setProbStride(stride);
        }

    }

    private static int getMostProbableStride(ArrayList<StrideFrequencyPair> strides){
        int stride = -1;
        int maxFreq = -1;
        for (int i = 0; i < strides.size(); i++){
            if (maxFreq < strides.get(i).getFrequency()){
                maxFreq = strides.get(i).getFrequency();
                stride = strides.get(i).getStride();
            }
        }
        return stride;
    }

    private static void updateMemRegionsMain(ArrayList<PcMemoryRegion> pcMems, MemoryInput input) {

        PcMemoryRegion mem_region = getPcMemRegion(pcMems, input.getModule(), input.getPc());
        if (mem_region != null) { /* we found a mem region */
            updateMemRegions(mem_region.getRegions(), input);
        } else {
            mem_region = new PcMemoryRegion();
            mem_region.setPc(input.getPc());
            mem_region.setModule(input.getModule());
            updateMemRegions(mem_region.getRegions(),input);
            pcMems.add(mem_region);
        }

    }

    private static void updateMemRegions(ArrayList<MemoryInfo> memInfo, MemoryInput input){

        long addr = input.getMemAddress();
        int stride = input.getStride();
        boolean merged = false;

        int direction = 0;
        ArrayList<StrideFrequencyPair> strideAcc = new ArrayList<>();

        for (int i = 0; i < memInfo.size(); i++){
            MemoryInfo info = memInfo.get(i);
            if (info.getType() == input.getType()){

				/* is the address with in range?*/
                if ((addr >= info.getStart()) && (addr + stride <= info.getEnd())){
                    info.setDirection(info.getDirection()|(input.isWrite()?MEM_OUTPUT : MEM_INPUT));
                    updateStride(info.getStrideFreqs(),stride);
                    merged = true;
                }
				/* delete the memory region that is contained */
                else if ((addr < info.getStart()) && (addr + stride > info.getEnd())){

                    direction = direction | info.getDirection();
                    updateStrideFromVector(strideAcc, info.getStrideFreqs());
                    memInfo.remove(i);
                    i--;

                }
				/* can we prepend this to the start of the memory region? */
                else if ((addr + stride >= info.getStart()) && (addr < info.getStart())){

                    info.setStart(addr);
                    info.setDirection(info.getDirection()|(input.isWrite()?MEM_OUTPUT : MEM_INPUT));
                    info.setDirection(info.getDirection()|direction);
                    updateStride(info.getStrideFreqs(),stride);
                    merged = true;
                }

				/* can we append this to the end of the memory region? */
                else if ((addr <= info.getEnd()) && (addr + stride > info.getEnd())){

                    info.setEnd(addr+stride);
                    info.setDirection(info.getDirection()|(input.isWrite()?MEM_OUTPUT : MEM_INPUT));
                    info.setDirection(info.getDirection()|direction);
                    updateStride(info.getStrideFreqs(),stride);
                    merged = true;

                }

            }
            else{
				/* see if there are any collisions and report as errors - implement later */
            }

            if (merged) break;

        }

        if (!merged){ /* if not merged to an exising mem_region then we need to create a new region */
            MemoryInfo newRegion = new MemoryInfo();
            newRegion.setStart(addr);
            newRegion.setEnd(addr+stride); /* actually this should be stride - 1 */
            newRegion.setDirection(input.isWrite()?MEM_OUTPUT : MEM_INPUT);
            newRegion.setDirection(newRegion.getDirection()|direction);
            newRegion.setType(input.getType());
            updateStrideFromVector(newRegion.getStrideFreqs(),strideAcc);
            updateStride(newRegion.getStrideFreqs(),stride);
            memInfo.add(newRegion);
        }
    }

    private static void updateStrideFromVector(ArrayList<StrideFrequencyPair> strides, ArrayList<StrideFrequencyPair> old){
        for (int i = 0; i < old.size(); i++){
            boolean updated = false;
            for (int j = 0; j < strides.size(); j++){
                if (strides.get(j).getStride() == old.get(i).getStride()){
                    strides.get(j).setFrequency(strides.get(j).getFrequency()+old.get(i).getFrequency());
                    updated = true;
                    break;
                }
            }
            if (!updated){
                strides.add(old.get(i));
            }
        }
    }

    private static void updateStride(ArrayList<StrideFrequencyPair> strides, int stride){
        boolean updated = false;
        for (int i = 0; i < strides.size(); i++){
            if (strides.get(i).getStride() == stride){
                strides.get(i).setFrequency(strides.get(i).getFrequency()+1);
                updated = true;
                break;
            }
        }
        if (!updated){
            strides.add(new StrideFrequencyPair(stride,1));
        }
    }

    /* pc_mem_info_t related functions */
    private static PcMemoryRegion getPcMemRegion(ArrayList<PcMemoryRegion> pcMems, String module, int appPc) {
	/* first check whether we have the same app_pc */
        if (module.length() > 0) { /* here the module information comparison is requested by the input */
            for (int i = 0; i < pcMems.size(); i++) {
                if (pcMems.get(i).getModule().equals(module)) {
                /* now check for the app_pc */
                    if (pcMems.get(i).getPc() == appPc) {
                        return pcMems.get(i);
                    }
                }
            }
        } else {  /* module information is disregarded; here we get the first match with app_pc we do not try to see whether there are more than one match*/
            for (int i = 0; i < pcMems.size(); i++) {
                if (pcMems.get(i).getPc() == appPc) {
                    return pcMems.get(i);
                }
            }
        }
        return null;

    }

}
