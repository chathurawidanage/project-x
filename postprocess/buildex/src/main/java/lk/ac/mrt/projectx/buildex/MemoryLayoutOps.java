package lk.ac.mrt.projectx.buildex;

import lk.ac.mrt.projectx.buildex.files.InstructionTraceFile;
import lk.ac.mrt.projectx.buildex.models.Pair;
import lk.ac.mrt.projectx.buildex.models.common.CommonUtil;
import lk.ac.mrt.projectx.buildex.models.memoryinfo.MemDirection;
import lk.ac.mrt.projectx.buildex.models.memoryinfo.MemoryInfo;
import lk.ac.mrt.projectx.buildex.models.memoryinfo.MemoryInput;
import lk.ac.mrt.projectx.buildex.models.memoryinfo.PCMemoryRegion;
import lk.ac.mrt.projectx.buildex.models.output.MemoryType;
import lk.ac.mrt.projectx.buildex.models.output.Operand;
import lk.ac.mrt.projectx.buildex.models.output.Output;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.util.*;

/**
 * @author Chathura Widanage
 */
public class MemoryLayoutOps {

    public static final int VER_NO_ADDR_OPND = 0;
    public static final int VER_WITH_ADDR_OPND = 1;

    private static Logger logger = LogManager.getLogger( MemoryLayoutOps.class );

    public static List<MemoryInfo> createMemoryLayoutMemoryInfo(InstructionTraceFile instructionTraceFile, int version) throws FileNotFoundException {
        List<MemoryInfo> memoryInfoList = new ArrayList<>();

        logger.debug( "Creating memory layout : [Memory Info]" );

        List<Output> instructions = getInstructionsList( instructionTraceFile, version );
        Iterator<Output> instructionsIterator = instructions.iterator();

        while (instructionsIterator.hasNext()) {
            Output instr = instructionsIterator.next();
            MemoryInput memoryInput = new MemoryInput();

            if (instr != null) {
                for (Operand opSrc : instr.getSrcs()) {//safely using Double as generic
                    if (opSrc.getType() == MemoryType.MEM_HEAP_TYPE || opSrc.getType() == MemoryType.MEM_STACK_TYPE) {
                        boolean skip = true;
                        if (opSrc.getType() == MemoryType.MEM_STACK_TYPE) {
                            for (int addr = 0 ; addr < 2 ; addr++) {
                                long reg = opSrc.getAddress().get( addr ).getValue().longValue();//todo check casting issue
                                if (reg != 0 && reg != DefinesDotH.DR_REG.DR_REG_EBP.ordinal() && reg != DefinesDotH.DR_REG.DR_REG_ESP.ordinal()) {
                                    skip = false;
                                }
                            }
                        } else {
                            skip = false;
                        }

                        if (skip) {
                            continue;
                        }

                        memoryInput.setMemAddress( opSrc.getValue().longValue() );//todo check cast problem
                        memoryInput.setStride( opSrc.getWidth() );
                        memoryInput.setWrite( false );
                        memoryInput.setType( opSrc.getType() );
                        if (memoryInput.getStride() != 0) {
                            updateMemoryRegionsMemInfo( memoryInfoList, memoryInput );
                        }
                    }
                }
                for (Operand opDst : instr.getDsts()) {
                    if (opDst.getType() == MemoryType.MEM_HEAP_TYPE || opDst.getType() == MemoryType.MEM_STACK_TYPE) {
                        boolean skip = true;
                        if (opDst.getType() == MemoryType.MEM_STACK_TYPE) {
                            for (int addr = 0 ; addr < 2 ; addr++) {
                                long reg = opDst.getAddress().get( addr ).getValue().longValue();//todo check cast problem
                                if (reg != 0 && reg != DefinesDotH.DR_REG.DR_REG_EBP.ordinal() && reg != DefinesDotH.DR_REG.DR_REG_ESP.ordinal()) {
                                    skip = false;
                                }
                            }
                        } else {
                            skip = false;
                        }

                        if (skip) {
                            continue;
                        }

                        memoryInput.setMemAddress( opDst.getValue().longValue() );//todo check casting problem
                        memoryInput.setStride( opDst.getWidth() );
                        memoryInput.setWrite( true );
                        memoryInput.setType( opDst.getType() );
                        if (memoryInput.getStride() != 0) {
                            updateMemoryRegionsMemInfo( memoryInfoList, memoryInput );
                        }
                    }
                }

            }
        }
        postProcessMemoryRegionsMemoryInfo( memoryInfoList );

        //assigning order
        int order = 0;
        for (MemoryInfo memoryInfo : memoryInfoList) {
            memoryInfo.setOrder( order++ );
        }

        logger.debug( "Creating memory layout : [Memory Info] - Done" );
        return memoryInfoList;
    }

    private static List<Output> getInstructionsList(InstructionTraceFile instructionTraceFile, int version) throws FileNotFoundException {
        Scanner instrScanner = new Scanner( instructionTraceFile );
        List<Output> instructions = new ArrayList<>();
        while (instrScanner.hasNextLine()) {
            String newLine = instrScanner.nextLine().trim();
            if (newLine.length() > 0) {
                Output instr = new Output();
                StringTokenizer stringTokenizer = new StringTokenizer( newLine, "," );
                instr.setOpcode( Integer.parseInt( stringTokenizer.nextToken() ) );

                //destinations
                int numOfDestinations = Integer.parseInt( stringTokenizer.nextToken() );
                for (int i = 0 ; i < numOfDestinations ; i++) {
                    Operand destination = parseOperand( stringTokenizer, version );
                    instr.getDsts().add( destination );
                }

                //get the number of sources
                int numOfSources = Integer.parseInt( stringTokenizer.nextToken() );

                for (int i = 0 ; i < numOfSources ; i++) {
                    Operand source = parseOperand( stringTokenizer, version );
                    instr.getSrcs().add( source );
                }

                instr.setEflags( Long.parseLong( stringTokenizer.nextToken() ) );
                instr.setPc( Long.parseLong( stringTokenizer.nextToken() ) );

                instructions.add( instr );
            }
        }
        return instructions;
    }

    private static Operand parseOperand(StringTokenizer stringTokenizer, int version) {
        Operand operand = new Operand();
        operand.setType( Integer.parseInt( stringTokenizer.nextToken() ) );
        operand.setWidth( Integer.parseInt( stringTokenizer.nextToken() ) );

        //todo check whether float_value and value is essential if double is used
        if (operand.getType() == MemoryType.IMM_FLOAT_TYPE) {
            operand.setValue( Double.parseDouble( stringTokenizer.nextToken() ) );
        } else {
            operand.setValue( Long.parseLong( stringTokenizer.nextToken() ) );
        }

        if (version == VER_WITH_ADDR_OPND) {
            if (operand.getType() == MemoryType.MEM_STACK_TYPE || operand.getType() == MemoryType.MEM_HEAP_TYPE) {
            /* we need to collect the address operands */
                for (int j = 0 ; j < 4 ; j++) {
                    Operand address = new Operand();
                    address.setType( Integer.parseInt( stringTokenizer.nextToken() ) );
                    address.setWidth( Integer.parseInt( stringTokenizer.nextToken() ) );
                    address.setValue( Long.parseLong( stringTokenizer.nextToken() ) );
                    operand.getAddress().add( address );
                }
            }
        } else if (version == VER_NO_ADDR_OPND) {
            //address is null
        }
        return operand;
    }

    private static void updateMemoryRegionsMemInfo(List<MemoryInfo> memoryInfoList, MemoryInput memoryInput) {
        long address = memoryInput.getMemAddress();
        int stride = memoryInput.getStride();
        boolean merged = false;

        int direction = 0;
        List<Pair<Integer, Integer>> strideAcc = new ArrayList<>();

        for (int i = 0 ; i < memoryInfoList.size() ; i++) {
            MemoryInfo memoryInfo = memoryInfoList.get( i );
            if (memoryInfo.getType() == memoryInput.getType()) {
                /* is the address with in range?*/
                if ((address >= memoryInfo.getStart()) && (address + stride <= memoryInfo.getEnd())) {
                    int memInfDirection = memoryInfo.getDirection();
                    memInfDirection |= memoryInput.isWrite() ? MemDirection.MEM_OUTPUT.getValue() : MemDirection.MEM_INPUT.getValue();
                    memoryInfo.setDirection( memInfDirection );
                    updateStride( memoryInfo.getStrideFrequency(), stride );
                    merged = true;
                }
                /* delete the memory region that is contained */
                else if ((address < memoryInfo.getStart()) && (address + stride > memoryInfo.getEnd())) {
                    direction |= memoryInfo.getDirection();
                    updateStride( strideAcc, memoryInfo.getStrideFrequency() );
                    //todo there was a erase in CPP
                }
                /* can we prepend this to the start of the memory region? */
                else if ((address + stride >= memoryInfo.getStart()) && (address < memoryInfo.getStart())) {
                    memoryInfo.setStart( address );
                    int memoryInfoDirection = memoryInfo.getDirection();
                    memoryInfoDirection |= memoryInput.isWrite() ? MemDirection.MEM_OUTPUT.getValue() : MemDirection.MEM_INPUT.getValue();
                    memoryInfoDirection |= direction;
                    memoryInfo.setDirection( memoryInfoDirection );
                    updateStride( memoryInfo.getStrideFrequency(), stride );
                    merged = true;
                }

				/* can we append this to the end of the memory region? */
                else if ((address <= memoryInfo.getEnd()) && (address + stride > memoryInfo.getEnd())) {
                    memoryInfo.setEnd( address + stride );
                    int memoryInfoDirection = memoryInfo.getDirection();
                    memoryInfoDirection |= memoryInput.isWrite() ? MemDirection.MEM_OUTPUT.getValue() : MemDirection.MEM_INPUT.getValue();
                    memoryInfoDirection |= direction;
                    memoryInfo.setDirection( memoryInfoDirection );
                    updateStride( memoryInfo.getStrideFrequency(), stride );
                    merged = true;
                }

            } else {
                /* see if there are any collisions and report as errors - implement later */
            }

            if (merged) break;

        }

        if (!merged) { /* if not merged to an exising mem_region then we need to create a new region */
            MemoryInfo newRegion = new MemoryInfo();
            newRegion.setStart( address );
            newRegion.setEnd( address + stride );/* actually this should be stride - 1 *///todo
            int memoryInfoDirection = memoryInput.isWrite() ? MemDirection.MEM_OUTPUT.getValue() : MemDirection.MEM_INPUT.getValue();
            memoryInfoDirection |= direction;
            newRegion.setDirection( memoryInfoDirection );
            newRegion.setType( memoryInput.getType() );
            updateStride( newRegion.getStrideFrequency(), strideAcc );
            updateStride( newRegion.getStrideFrequency(), stride );
            memoryInfoList.add( newRegion );
        }
    }

    private static void updateStride(List<Pair<Integer, Integer>> strides, List<Pair<Integer, Integer>> old) {
        for (int i = 0 ; i < old.size() ; i++) {
            int oldStride = old.get( i ).first;
            int oldFrequency = old.get( i ).second;
            boolean updated = false;
            for (int j = 0 ; j < strides.size() ; j++) {
                if (strides.get( j ).first == oldStride) {
                    strides.get( j ).second += oldFrequency;
                    updated = true;
                    break;
                }
            }

            if (!updated) {
                strides.add( old.get( i ) );
            }
        }
    }

    private static void updateStride(List<Pair<Integer, Integer>> strides, int stride) {
        boolean updated = false;
        for (int i = 0 ; i < strides.size() ; i++) {
            Pair<Integer, Integer> stridePr = strides.get( i );
            if (stridePr.first == stride) {
                stridePr.second++;
                updated = true;
                break;
            }
        }
        if (!updated) {
            strides.add( new Pair<>( stride, 1 ) );
        }
    }

    /*POST PROCESSING*/
    private static void postProcessMemoryRegionsMemoryInfo(List<MemoryInfo> memoryInfoList) {
        defragmentMemoryRegions( memoryInfoList );
        updateMostProbStride( memoryInfoList );
    }

    private static void defragmentMemoryRegions(List<MemoryInfo> memoryInfoList) {
        /*this will try to merge individual chunks of memory units info defragmented larger chunks*/
        boolean finished = false;

        while (!finished) {
            Set<MemoryInfo> toRemove = new HashSet<>();
            ListIterator<MemoryInfo> memoryInfoIterator = memoryInfoList.listIterator();

            while (memoryInfoIterator.hasNext()) {
                int i = memoryInfoIterator.nextIndex();
                MemoryInfo candidate = memoryInfoIterator.next();

                ListIterator<MemoryInfo> memoryInfoIteratorSub = memoryInfoList.listIterator();
                while (memoryInfoIteratorSub.hasNext()) {
                    MemoryInfo current = memoryInfoIteratorSub.next();
                    if (current == candidate) continue;  /* this never happens? */

				/*check if they can be merged*/
                    if (current.getType() == candidate.getType()) {

					/*can we merge the two? we will always update the candidate and delete the current if this can be merged*/
                    /* we cannot possibly have a mem region captured within a mem region if update mem regions has done its job*/

                        boolean merged = false;

					/*if the candidate is a subset of the current? remove candidate*/
                        if ((candidate.getStart() >= current.getStart()) && (candidate.getEnd() <= current.getEnd())) {
                            int currentDirection = current.getDirection();
                            currentDirection |= candidate.getDirection();
                            current.setDirection( currentDirection );
                            updateStride( current.getStrideFrequency(), candidate.getStrideFrequency() );
                            toRemove.add( candidate );
                            break;
                        }
                    /*if current is a subset of the candidate? remove current*/
                        else if ((current.getStart() >= candidate.getStart()) && (current.getEnd() <= candidate.getEnd())) {
                            merged = true;
                        }
                    /* prepend to the candidate?*/
                        else if ((current.getStart() < candidate.getStart()) && (current.getEnd() >= candidate.getStart())) {
                            candidate.setStart( current.getStart() );
                            merged = true;
                        }
                    /* append to candidate?*/
                        else if ((current.getStart() <= candidate.getEnd()) && (current.getStart() > candidate.getEnd())) {
                            candidate.setEnd( current.getEnd() );
                            merged = true;
                        }

                        if (merged) {
                            int candidateDirection = candidate.getDirection();
                            candidateDirection |= current.getDirection();
                            candidate.setDirection( candidateDirection );
                            updateStride( candidate.getStrideFrequency(), current.getStrideFrequency() );
                            toRemove.add( current );
                        }
                    }

                }
            }

            finished = !memoryInfoList.removeAll( toRemove );
        }
    }

    private static void updateMostProbStride(List<MemoryInfo> memoryInfoLis) {
        for (MemoryInfo memoryInfo : memoryInfoLis) {
            long stride = getMostProbableStride( memoryInfo.getStrideFrequency() );
            memoryInfo.setProbStride( stride );
        }
    }

    private static long getMostProbableStride(List<Pair<Integer, Integer>> strideFrequencies) {
        long stride = -1;
        long maxFreq = -1;

        for (Pair<Integer, Integer> pr : strideFrequencies) {
            if (maxFreq < pr.second) {
                maxFreq = pr.second;
                stride = pr.first;
            }
        }
        return stride;
    }

    public static List<PCMemoryRegion> createMemoryLayoutPCMemoryRegion(InstructionTraceFile instructionTraceFile, int version) throws FileNotFoundException {
        logger.debug( "Creating memory layout : [PC Memory Region]" );
        List<PCMemoryRegion> pcMemoryRegionList = new ArrayList<>();

        List<Output> instructions = getInstructionsList( instructionTraceFile, version );
        Iterator<Output> instructionsIterator = instructions.iterator();

        while (instructionsIterator.hasNext()) {
            Output instr = instructionsIterator.next();
            MemoryInput memoryInput = new MemoryInput();
            if (instr != null) {
                for (Operand opSrc : instr.getSrcs()) {
                    if (opSrc.getType() == MemoryType.MEM_HEAP_TYPE || opSrc.getType() == MemoryType.MEM_STACK_TYPE) {
                        boolean skip = true;
                        if (opSrc.getType() == MemoryType.MEM_STACK_TYPE) {
                            for (int addr = 0 ; addr < 2 ; addr++) {
                                long reg = opSrc.getAddress().get( addr ).getValue().longValue();
                                if (reg != 0 && reg != DefinesDotH.DR_REG.DR_REG_EBP.ordinal() && reg != DefinesDotH.DR_REG.DR_REG_ESP.ordinal()) {
                                    skip = false;
                                }
                            }
                        } else {
                            skip = false;
                        }

                        if (skip) continue;

                        memoryInput.setPc( instr.getPc() );
                        memoryInput.setMemAddress( opSrc.getValue().longValue() );
                        memoryInput.setStride( opSrc.getWidth() );
                        memoryInput.setWrite( false );
                        memoryInput.setType( opSrc.getType() );
                        if (memoryInput.getStride() != 0) {
                            updateMemoryRegionsPCMemoryRegion( pcMemoryRegionList, memoryInput );
                        }
                    }
                }
                for (Operand opDst : instr.getDsts()) {
                    if (opDst.getType() == MemoryType.MEM_HEAP_TYPE || opDst.getType() == MemoryType.MEM_STACK_TYPE) {
                        boolean skip = true;
                        if (opDst.getType() == MemoryType.MEM_STACK_TYPE) {
                            for (int addr = 0 ; addr < 2 ; addr++) {
                                long reg = opDst.getAddress().get( addr ).getValue().longValue();//todo check cast problem
                                if (reg != 0 && reg != DefinesDotH.DR_REG.DR_REG_EBP.ordinal() && reg != DefinesDotH.DR_REG.DR_REG_ESP.ordinal()) {
                                    skip = false;
                                }
                            }
                        } else {
                            skip = false;
                        }

                        if (skip) {
                            continue;
                        }

                        memoryInput.setPc( instr.getPc() );
                        memoryInput.setMemAddress( opDst.getValue().longValue() );//todo check casting problem
                        memoryInput.setStride( opDst.getWidth() );
                        memoryInput.setWrite( true );
                        memoryInput.setType( opDst.getType() );
                        if (memoryInput.getStride() != 0) {
                            updateMemoryRegionsPCMemoryRegion( pcMemoryRegionList, memoryInput );
                        }
                    }
                }
            }
        }
        postProcessMemoryRegionsPCMemoryRegion( pcMemoryRegionList );
        logger.debug( "Creating memory layout : [Memory Info] - Done" );
        return pcMemoryRegionList;
    }

    private static void updateMemoryRegionsPCMemoryRegion(List<PCMemoryRegion> pcMemoryRegionList, MemoryInput memoryInput) {
        PCMemoryRegion pcMemoryRegion = getPCMemoryRegion( pcMemoryRegionList, memoryInput.getModule(), memoryInput.getPc() );
        if (pcMemoryRegion != null) {
            updateMemoryRegionsMemInfo( pcMemoryRegion.getRegions(), memoryInput );
        } else {
            pcMemoryRegion = new PCMemoryRegion();
            pcMemoryRegion.setPc( memoryInput.getPc() );
            pcMemoryRegion.setModule( memoryInput.getModule() );
            updateMemoryRegionsMemInfo( pcMemoryRegion.getRegions(), memoryInput );
            pcMemoryRegionList.add( pcMemoryRegion );
        }
    }
    /*END OF POST PROCESSING*/

    private static PCMemoryRegion getPCMemoryRegion(List<PCMemoryRegion> pcMemoryRegions, String module, long appPC) {
        /* first check whether we have the same app_pc */
        if (module != null && module.trim().length() != 0) { /* here the module information comparison is requested by the input */
            for (PCMemoryRegion pcMemoryRegion : pcMemoryRegions) {
                GeneralUtils.assertAndFail( pcMemoryRegion.getModule().trim().length() != 0,
                        "module information is missing from mem_regions" );
                if (pcMemoryRegion.getModule().equals( module )) {
                /* now check for the app_pc */
                    if (pcMemoryRegion.getPc() == appPC) {
                        return pcMemoryRegion;
                    }
                }
            }
        } else {  /* module information is disregarded; here we get the first match with app_pc we do not try to see whether there are more than one match*/
            for (PCMemoryRegion pcMemoryRegion : pcMemoryRegions) {
                if (pcMemoryRegion.getPc() == appPC) {
                    return pcMemoryRegion;
                }
            }
        }
        return null;
    }

    private static void postProcessMemoryRegionsPCMemoryRegion(List<PCMemoryRegion> pcMemoryRegionList) {
        logger.debug( "Post processing {} memory regions", pcMemoryRegionList.size() );
        for (PCMemoryRegion pcMemoryRegion : pcMemoryRegionList) {
            logger.debug( " app_pc {} mem region size before {}", pcMemoryRegion.getPc(), pcMemoryRegion.getRegions().size() );
            defragmentMemoryRegions( pcMemoryRegion.getRegions() );
            updateMostProbStride( pcMemoryRegion.getRegions() );
            logger.debug( " mem region size after {}", pcMemoryRegion.getRegions().size() );
        }
    }


    /*Linking and Merging*/

    public static void linkMemoryRegionsGreedy(List<MemoryInfo> memoryInfoList, long appPc) {
        Collections.sort( memoryInfoList, MemoryInfoComparators.getComparatorByStart() );

        List<MemoryInfo> removeMemoryInfos = new ArrayList<>();
        /*End of MemoryMerger Helper*/

        boolean ret = true;

        while (ret) {

            ret = false;
            for (int i = 0 ; i < memoryInfoList.size() ; i++) {

                if (i + 2 >= memoryInfoList.size()) continue; //at least three regions should be connected
                long gapFirst = memoryInfoList.get( i + 1 ).getStart() - memoryInfoList.get( i ).getEnd();
                long gapSecond = memoryInfoList.get( i + 2 ).getStart() - memoryInfoList.get( i + 1 ).getEnd();

                long sizeFirst = memoryInfoList.get( i ).getEnd() - memoryInfoList.get( i ).getStart();
                long sizeMiddle = memoryInfoList.get( i + 1 ).getEnd() - memoryInfoList.get( i + 1 ).getEnd();
                long sizeLast = memoryInfoList.get( i + 2 ).getEnd() - memoryInfoList.get( i + 2 ).getStart();

                boolean size_match = (sizeFirst == sizeMiddle && sizeMiddle == sizeLast);

                if (gapFirst == gapSecond && size_match) { /* ok we can now merge the regions */
                    MemoryInfo newMemInfo = new MemoryInfo();

                    long gap = gapFirst;

                    final MemoryInfo firstMemoryInfo = memoryInfoList.get( i );
                    MemoryInfo middleMemoryInfo = memoryInfoList.get( i + 1 );
                    MemoryInfo lastMemoryInfo = memoryInfoList.get( i + 2 );

                    newMemInfo.setDirection( firstMemoryInfo.getDirection() );
                    newMemInfo.setProbStride( firstMemoryInfo.getProbStride() );
                    newMemInfo.setType( firstMemoryInfo.getType() );
                    newMemInfo.setStrideFrequency( firstMemoryInfo.getStrideFrequency() );

                    newMemInfo.setStart( firstMemoryInfo.getStart() );
                    newMemInfo.setEnd( lastMemoryInfo.getEnd() );
                    newMemInfo.getMergedMemoryInfos().addAll( firstMemoryInfo.getMergedMemoryInfos() );
                    newMemInfo.getMergedMemoryInfos().addAll( middleMemoryInfo.getMergedMemoryInfos() );
                    newMemInfo.getMergedMemoryInfos().addAll( lastMemoryInfo.getMergedMemoryInfos() );


                    MemoryMerger.mergeInfoToFirst( newMemInfo, middleMemoryInfo );
                    MemoryMerger.mergeInfoToFirst( newMemInfo, lastMemoryInfo );


                    long index = i + 2;
                    for (int j = i + 3 ; j < memoryInfoList.size() ; j++) {
                        long gapNow = memoryInfoList.get( j ).getStart() - newMemInfo.getEnd();
                        long sizeNow = memoryInfoList.get( j ).getEnd() - memoryInfoList.get( j ).getStart();

                        if (gapNow == gap && sizeNow == sizeLast) {
                            //mem[i]->end = mem[j]->end;
                            //merge_info_to_first(mem[i], mem[j]);
                            newMemInfo.getMergedMemoryInfos().add( memoryInfoList.get( j ) );
                            newMemInfo.setEnd( memoryInfoList.get( j ).getEnd() );
                            MemoryMerger.mergeInfoToFirst( newMemInfo, memoryInfoList.get( j ) );
                            index = j;
                        } else {
                            break;
                        }
                    }

                    newMemInfo.setEnd( newMemInfo.getEnd() + gap );

                    logger.debug( "app_pc {} merged indexes from {} to {}", appPc, i, index );
                    for (int j = i ; j <= index ; j++) {
                        //delete mem[i + 1];
                        removeMemoryInfos.add( memoryInfoList.get( j ) );
                        //mem.erase(mem.begin() + i);
                    }
                    memoryInfoList.add( i, newMemInfo );
                    //mem.insert(mem.begin() + i, new_mem_info);


                    newMemInfo.setOrder( Integer.MAX_VALUE );
                    for (int j = 0 ; j < newMemInfo.getMergedMemoryInfos().size() ; j++) {
                        if (newMemInfo.getOrder() > newMemInfo.getMergedMemoryInfos().get( j ).getOrder()) {
                            newMemInfo.setOrder( newMemInfo.getMergedMemoryInfos().get( j ).getOrder() );
                        }
                    }

                    logger.info( "linked memory infos" );
                    logger.info( "New memory info start: {} , end : {}, merged amount : {}", newMemInfo.getStart(), newMemInfo.getEnd(), newMemInfo.getMergedMemoryInfos().size() );
                    ret = true;
                }
            }
        }
        logger.debug( "Removing {} merged memory infos from array", removeMemoryInfos.size() );
        memoryInfoList.removeAll( removeMemoryInfos );
    }

    public static void mergeMemoryInfoPCMemoryRegion(List<MemoryInfo> memoryInfoList, List<PCMemoryRegion> pcMemoryRegionList) {
        List<List<MemoryInfo>> mergeOpportunities = getMergeOpportunities( memoryInfoList, pcMemoryRegionList );
        mergeMemoryRegionsPC( mergeOpportunities, memoryInfoList );
    }

    //todo this function was not tested due to absence of merge opportunities in blur filter data
    private static List<List<MemoryInfo>> getMergeOpportunities(List<MemoryInfo> memoryInfoList, List<PCMemoryRegion> pcMemoryRegionList) {
        List<List<MemoryInfo>> ret = new ArrayList<>();
        Collections.sort( memoryInfoList, MemoryInfoComparators.getComparatorByStart() );

        logger.debug( "Getting merge opportunities" );


        for (int i = 0 ; i < pcMemoryRegionList.size() ; i++) {
            List<MemoryInfo> overlapped = new ArrayList<>();
            List<MemoryInfo> pc_mem_info = pcMemoryRegionList.get( i ).getRegions();

            for (int j = 0 ; j < memoryInfoList.size() ; j++) {
                for (int k = 0 ; k < pc_mem_info.size() ; k++) {
                    if (MergeOpportunityUtils.isOverlapped(
                            pc_mem_info.get( k ).getStart(),
                            pc_mem_info.get( k ).getEnd(),
                            memoryInfoList.get( j ).getStart(),
                            memoryInfoList.get( j ).getEnd()
                    )) {
                        overlapped.add( memoryInfoList.get( j ) );
                        break;
                    }
                }
            }

            logger.info( "{} overlapped - ", pcMemoryRegionList.get( i ).getPc(), overlapped.toString() );

            List<List<MemoryInfo>> regions = new ArrayList<>();
            List<MemoryInfo> tempInfo = new ArrayList<>();
            /* find sequences which are fairly close to each other */
            for (int j = 1 ; j < overlapped.size() ; j++) {
                long firstDim = MergeOpportunityUtils.getNumberOfDimensions( overlapped.get( j - 1 ) );
                long firstExtent = MergeOpportunityUtils.getExtents( overlapped.get( j - 1 ), firstDim, firstDim );
                long firstStride = MergeOpportunityUtils.getStride( overlapped.get( j - 1 ), firstDim, firstDim );
                long rightFirst = overlapped.get( j - 1 ).getEnd() + firstExtent * firstStride;

                long secondDim = MergeOpportunityUtils.getNumberOfDimensions( overlapped.get( j ) );
                long secondExtent = MergeOpportunityUtils.getExtents( overlapped.get( j ), secondDim, secondDim );
                long secondStride = MergeOpportunityUtils.getStride( overlapped.get( j ), secondDim, secondDim );
                long leftSecond = overlapped.get( j ).getStart() - secondExtent * secondStride;

                tempInfo.add( overlapped.get( j - 1 ) );
                if (rightFirst < leftSecond) {
                    regions.add( new ArrayList<>( tempInfo ) );
                    tempInfo.clear();
                }
            }

            tempInfo.add( overlapped.get( overlapped.size() - 1 ) );
            regions.add( tempInfo );

            logger.info( "Regions : {}", regions );

            for (int k = 0 ; k < regions.size() ; k++) {
                //sort(regions[k].begin(), regions[k].end());

                if (regions.get( k ).size() >= 2) {

                    boolean added = false;

                    for (int j = 0 ; j < ret.size() ; j++) {
                    /* 1000 is just a heuristic value of the set size() -> make it dynamic for more resilience*/
                        /*vector<mem_info_t *>temp(1000);
                        vector<mem_info_t *>::iterator it;*/

                        Set<MemoryInfo> intersection = new HashSet<>( regions.get( k ) );
                        intersection.retainAll( new HashSet<>( ret.get( j ) ) );

                        //it = set_intersection(regions[k].begin(), regions[k].end(), ret[j].begin(), ret[j].end(), temp.begin());
                       /* if (it != temp.begin()) {
                            it = set_union(regions[k].begin(), regions[k].end(), ret[j].begin(), ret[j].end(), temp.begin());
                            temp.resize(it - temp.begin());
                            ret[j] = temp;
                            added = true;
                            break;
                        }*/
                        if (!intersection.isEmpty()) {//todo check porting
                            Set<MemoryInfo> union = new HashSet<>( regions.get( k ) );
                            union.addAll( ret.get( j ) );
                            ret.set( j, new ArrayList<>( union ) );
                            added = true;
                            break;
                        }

                    }

                    if (!added) {
                        ret.add( regions.get( k ) );
                    }
                }
            }
        }
        return ret;
    }

    //todo this function was not tested due to absence of merge opportunities in blur filter data
    private static void mergeMemoryRegionsPC(List<List<MemoryInfo>> mergable, List<MemoryInfo> memoryInfoList) {
        logger.info( "Merging memory info regions" );

	/* take the biggest region and expand - assume that the ghost zones would be minimal compared to the actual buffer sizes accessed */
        for (int i = 0 ; i < mergable.size() ; i++) {
            List<MemoryInfo> regionSet = mergable.get( i );
            Collections.sort( regionSet, MemoryInfoComparators.getComparatorByStart() );
            long maxVal = 0;
            int index = 0;

            for (int j = 0 ; j < regionSet.size() ; j++) {
                if ((regionSet.get( j ).getEnd() - regionSet.get( j ).getStart()) > maxVal) {
                    maxVal = regionSet.get( j ).getEnd() - regionSet.get( j ).getStart();
                    index = j;
                }
            }


            long dims = MergeOpportunityUtils.getNumberOfDimensions( regionSet.get( index ) );
            MemoryInfo merged = regionSet.get( index );

		/* check neighbours and if faraway do not merge; for now if not input do not merge */
            if (merged.getDirection() == MemDirection.MEM_OUTPUT.getValue()) continue;

            logger.debug( "merge_region before: {} {}", merged.getStart(), merged.getEnd() );
            for (int j = 1 ; j <= dims ; j++) {
                long stride = MergeOpportunityUtils.getStride( merged, j, dims );
                long extents = MergeOpportunityUtils.getExtents( merged, j, dims );
                logger.debug( "dim {}, extent {}, stride {}", j, extents, stride );
            }

            if (index != 0) {

                if (dims == 1) {
                    merged.setStart( regionSet.get( 0 ).getStart() );
                } else {
                    long stride = MergeOpportunityUtils.getStride( merged, dims, dims );
                    while (merged.getStart() > regionSet.get( 0 ).getStart()) {
                        MemoryInfo newMemoryRegion = new MemoryInfo();
                        newMemoryRegion.setType( merged.getType() );
                        newMemoryRegion.setDirection( merged.getDirection() );
                        newMemoryRegion.setStart( merged.getStart() - stride );
                        newMemoryRegion.setEnd( newMemoryRegion.getStart()
                                + MergeOpportunityUtils.getExtents( merged, dims - 1, dims ) );
                        newMemoryRegion.setProbStride( merged.getMergedMemoryInfos().get( 0 ).getProbStride() );
                        merged.getMergedMemoryInfos().add( 0, newMemoryRegion );
                        merged.setStart( stride );
                    }
                }

            }

            if (index != regionSet.size() - 1) {

                if (dims == 1) {
                    merged.setEnd( regionSet.get( regionSet.size() - 1 ).getEnd() );
                } else {
                    long stride = MergeOpportunityUtils.getStride( merged, dims, dims );
                    while (merged.getEnd() < regionSet.get( regionSet.size() - 1 ).getEnd()) {
                        MemoryInfo new_region = new MemoryInfo();
                        new_region.setType( merged.getType() );
                        new_region.setDirection( merged.getDirection() );
                        new_region.setEnd( merged.getEnd() + MergeOpportunityUtils.getExtents( merged, dims - 1, dims ) );
                        new_region.setStart( merged.getEnd() );
                        new_region.setProbStride( merged.getMergedMemoryInfos().get( 0 ).getProbStride() );
                        merged.getMergedMemoryInfos().add( new_region );
                        merged.setEnd( merged.getEnd() + stride );
                    }
                }

            }
        }
    }

    /**
     * MemoryMerger helper class
     */
    private static class MemoryMerger {

        static void mergeInfoToFirst(MemoryInfo memoryInfoFirst, MemoryInfo memoryInfoSecond) {
            int direction = memoryInfoFirst.getDirection();
            direction |= memoryInfoSecond.getDirection();
            memoryInfoFirst.setDirection( direction );
            updateStride( memoryInfoFirst.getStrideFrequency(), memoryInfoSecond.getStrideFrequency() );
            memoryInfoFirst.setProbStride( getMostProbableStride( memoryInfoFirst.getStrideFrequency() ) );
        }
    }

    private static class MergeOpportunityUtils {

        //TODO [MERGE] : lk.ac.mrt.projectx.buildex.models.common.CommonUtil isOverlapped
        public static boolean isOverlapped(long start1, long end1, long start2, long end2) {
            // both functions are same
            return CommonUtil.isOverlapped( start1, end1, start2, end2 );
        }

        public static long getNumberOfDimensions(MemoryInfo memoryInfo) {
            long dim = 1;
            MemoryInfo localMemInfo = memoryInfo;
            while (!localMemInfo.getMergedMemoryInfos().isEmpty()) {
                dim++;
                localMemInfo = localMemInfo.getMergedMemoryInfos().get( 0 );
            }
            return dim;
        }

        public static long getExtents(MemoryInfo memoryInfo, long dim, long totalDims) {
            List<MemoryInfo> localMemoryInfos = new ArrayList<>();
            localMemoryInfos.add( memoryInfo );

            MemoryInfo local = memoryInfo;

            while (!local.getMergedMemoryInfos().isEmpty()) {
                localMemoryInfos.add( local.getMergedMemoryInfos().get( 0 ) );
                local = local.getMergedMemoryInfos().get( 0 );
            }

            MemoryInfo wanted = localMemoryInfos.get( (int) (totalDims - dim) );
            if (!wanted.getMergedMemoryInfos().isEmpty()) {
                return wanted.getMergedMemoryInfos().size();
            } else {
                return (wanted.getEnd() - wanted.getStart()) / wanted.getProbStride();
            }

        }

        public static long getStride(MemoryInfo memoryInfo, long dim, long totalDims) {
            List<MemoryInfo> localMemoryInfos = new ArrayList<>();
            localMemoryInfos.add( memoryInfo );

            MemoryInfo local = memoryInfo;

            while (!local.getMergedMemoryInfos().isEmpty()) {
                localMemoryInfos.add( local.getMergedMemoryInfos().get( 0 ) );
                local = local.getMergedMemoryInfos().get( 0 );
            }

            MemoryInfo wanted = localMemoryInfos.get( (int) (totalDims - dim) );
            if (!wanted.getMergedMemoryInfos().isEmpty()) {
                return wanted.getMergedMemoryInfos().get( 1 ).getStart() - wanted.getMergedMemoryInfos().get( 0 ).getStart();
            } else {
                return wanted.getProbStride();
            }

        }
    }

    public static class MemoryInfoComparators {

        public static Comparator<MemoryInfo> getComparatorByStart() {
            return new Comparator<MemoryInfo>() {
                @Override
                public int compare(MemoryInfo o1, MemoryInfo o2) {
                    return (int) (o1.getStart() - o2.getStart());
                }
            };
        }
    }
}
