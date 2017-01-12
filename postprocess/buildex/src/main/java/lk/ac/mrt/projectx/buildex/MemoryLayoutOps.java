package lk.ac.mrt.projectx.buildex;

import lk.ac.mrt.projectx.buildex.files.InstructionTraceFile;
import lk.ac.mrt.projectx.buildex.models.Pair;
import lk.ac.mrt.projectx.buildex.models.memoryinfo.MemDirection;
import lk.ac.mrt.projectx.buildex.models.memoryinfo.MemoryInfo;
import lk.ac.mrt.projectx.buildex.models.memoryinfo.MemoryInput;
import lk.ac.mrt.projectx.buildex.models.output.Operand;
import lk.ac.mrt.projectx.buildex.models.output.Output;
import lk.ac.mrt.projectx.buildex.models.output.MemoryType;
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

    private static Logger logger = LogManager.getLogger(MemoryLayoutOps.class);

    private static Operand parseOperand(StringTokenizer stringTokenizer, int version) {
        Operand operand = new Operand();
        operand.setType(Integer.parseInt(stringTokenizer.nextToken()));
        operand.setWidth(Integer.parseInt(stringTokenizer.nextToken()));

        //todo check whether float_value and value is essential if double is used
        if (operand.getType() == MemoryType.IMM_FLOAT_TYPE) {
            operand.setValue(Double.parseDouble(stringTokenizer.nextToken()));
        } else {
            operand.setValue(Long.parseLong(stringTokenizer.nextToken()));
        }

        if (version == VER_WITH_ADDR_OPND) {
            if (operand.getType() == MemoryType.MEM_STACK_TYPE || operand.getType() == MemoryType.MEM_HEAP_TYPE) {
            /* we need to collect the address operands */
                for (int j = 0; j < 4; j++) {
                    Operand address = new Operand();
                    address.setType(Integer.parseInt(stringTokenizer.nextToken()));
                    address.setWidth(Integer.parseInt(stringTokenizer.nextToken()));
                    address.setValue(Long.parseLong(stringTokenizer.nextToken()));
                    operand.getAddress().add(address);
                }
            }
        } else if (version == VER_NO_ADDR_OPND) {
            //address is null
        }
        return operand;
    }

    private static List<Output> getInstructionsList(InstructionTraceFile instructionTraceFile, int version) throws FileNotFoundException {
        Scanner instrScanner = new Scanner(instructionTraceFile);
        List<Output> instructions = new ArrayList<>();
        while (instrScanner.hasNextLine()) {
            String newLine = instrScanner.nextLine().trim();
            if (newLine.length() > 0) {
                Output instr = new Output();
                StringTokenizer stringTokenizer = new StringTokenizer(newLine, ",");
                instr.setOpcode(Integer.parseInt(stringTokenizer.nextToken()));

                //destinations
                instr.setNumOfDestinations(Integer.parseInt(stringTokenizer.nextToken()));
                for (int i = 0; i < instr.getNumOfDestinations(); i++) {
                    Operand destination = parseOperand(stringTokenizer, version);
                    instr.getDsts().add(destination);
                }

                //get the number of sources
                instr.setNumOfSources(Integer.parseInt(stringTokenizer.nextToken()));

                for (int i = 0; i < instr.getNumOfSources(); i++) {
                    Operand source = parseOperand(stringTokenizer, version);
                    instr.getSrcs().add(source);
                }

                instr.setEflags(Long.parseLong(stringTokenizer.nextToken()));
                instr.setPc(Long.parseLong(stringTokenizer.nextToken()));

                instructions.add(instr);
            }
        }
        return instructions;
    }

    private static void updateStride(List<Pair<Integer, Integer>> strides, List<Pair<Integer, Integer>> old) {
        for (int i = 0; i < old.size(); i++) {
            int oldStride = old.get(i).first;
            int oldFrequency = old.get(i).second;
            boolean updated = false;
            for (int j = 0; j < strides.size(); j++) {
                if (strides.get(j).first == oldStride) {
                    strides.get(j).second += oldFrequency;
                    updated = true;
                    break;
                }
            }

            if (!updated) {
                strides.add(old.get(i));
            }
        }
    }

    private static void updateStride(List<Pair<Integer, Integer>> strides, int stride) {
        boolean updated = false;
        for (int i = 0; i < strides.size(); i++) {
            Pair<Integer, Integer> stridePr = strides.get(i);
            if (stridePr.first == stride) {
                stridePr.second++;
                updated = true;
                break;
            }
        }
        if (!updated) {
            strides.add(new Pair<>(stride, 1));
        }
    }

    private static void updateMemoryRegions(List<MemoryInfo> memoryInfoList, MemoryInput memoryInput) {
        long address = memoryInput.getMemAddress();
        int stride = memoryInput.getStride();
        boolean merged = false;

        int direction = 0;
        List<Pair<Integer, Integer>> strideAcc = new ArrayList<>();

        for (int i = 0; i < memoryInfoList.size(); i++) {
            MemoryInfo memoryInfo = memoryInfoList.get(i);
            if (memoryInfo.getType() == memoryInput.getType()) {
                /* is the address with in range?*/
                if ((address >= memoryInfo.getStart()) && (address + stride <= memoryInfo.getEnd())) {
                    int memInfDirection = memoryInfo.getDirection();
                    memInfDirection |= memoryInput.isWrite() ? MemDirection.MEM_OUTPUT.getValue() : MemDirection.MEM_INPUT.getValue();
                    memoryInfo.setDirection(memInfDirection);
                    updateStride(memoryInfo.getStrideFrequency(), stride);
                    merged = true;
                }
                /* delete the memory region that is contained */
                else if ((address < memoryInfo.getStart()) && (address + stride > memoryInfo.getEnd())) {
                    direction |= memoryInfo.getDirection();
                    updateStride(strideAcc, memoryInfo.getStrideFrequency());
                    //todo there was a erase in CPP
                }
                /* can we prepend this to the start of the memory region? */
                else if ((address + stride >= memoryInfo.getStart()) && (address < memoryInfo.getStart())) {
                    memoryInfo.setStart(address);
                    int memoryInfoDirection = memoryInfo.getDirection();
                    memoryInfoDirection |= memoryInput.isWrite() ? MemDirection.MEM_OUTPUT.getValue() : MemDirection.MEM_INPUT.getValue();
                    memoryInfoDirection |= direction;
                    memoryInfo.setDirection(memoryInfoDirection);
                    updateStride(memoryInfo.getStrideFrequency(), stride);
                    merged = true;
                }

				/* can we append this to the end of the memory region? */
                else if ((address <= memoryInfo.getEnd()) && (address + stride > memoryInfo.getEnd())) {
                    memoryInfo.setEnd(address + stride);
                    int memoryInfoDirection = memoryInfo.getDirection();
                    memoryInfoDirection |= memoryInput.isWrite() ? MemDirection.MEM_OUTPUT.getValue() : MemDirection.MEM_INPUT.getValue();
                    memoryInfoDirection |= direction;
                    memoryInfo.setDirection(memoryInfoDirection);
                    updateStride(memoryInfo.getStrideFrequency(), stride);
                    merged = true;
                }

            } else {
                /* see if there are any collisions and report as errors - implement later */
            }

            if (merged) break;

        }

        if (!merged) { /* if not merged to an exising mem_region then we need to create a new region */
            MemoryInfo newRegion = new MemoryInfo();
            newRegion.setStart(address);
            newRegion.setEnd(address + stride);/* actually this should be stride - 1 *///todo
            int memoryInfoDirection = memoryInput.isWrite() ? MemDirection.MEM_OUTPUT.getValue() : MemDirection.MEM_INPUT.getValue();
            memoryInfoDirection |= direction;
            newRegion.setDirection(memoryInfoDirection);
            newRegion.setType(memoryInput.getType());
            updateStride(newRegion.getStrideFrequency(), strideAcc);
            updateStride(newRegion.getStrideFrequency(), stride);
            memoryInfoList.add(newRegion);
        }
    }

    public static List<MemoryInfo> createMemoryLayout(InstructionTraceFile instructionTraceFile, int version) throws FileNotFoundException {
        int count = 0;
        List<MemoryInfo> memoryInfoList = new ArrayList<>();

        logger.debug("Creating memory layout : [Memory Info]");

        List<Output> instructions = getInstructionsList(instructionTraceFile, version);
        Iterator<Output> instructionsIterator = instructions.iterator();

        while (instructionsIterator.hasNext()) {
            Output instr = instructionsIterator.next();
            MemoryInput memoryInput = new MemoryInput();

            if (instr != null) {
                for (Operand opSrc : instr.getSrcs()) {//safely using Double as generic
                    if (opSrc.getType() == MemoryType.MEM_HEAP_TYPE || opSrc.getType() == MemoryType.MEM_STACK_TYPE) {
                        boolean skip = true;
                        if (opSrc.getType() == MemoryType.MEM_STACK_TYPE) {
                            for (int addr = 0; addr < 2; addr++) {
                                long reg = opSrc.getAddress().get(addr).getValue().longValue();//todo check casting issue
                                if (reg != 0 && reg != DefinesDotH.Registers.DR_REG_EBP.ordinal() && reg != DefinesDotH.Registers.DR_REG_ESP.ordinal()) {
                                    skip = false;
                                }
                            }
                        } else {
                            skip = false;
                        }

                        if (skip) {
                            continue;
                        }

                        memoryInput.setMemAddress(opSrc.getValue().longValue());//todo check cast problem
                        memoryInput.setStride(opSrc.getWidth());
                        memoryInput.setWrite(false);
                        memoryInput.setType(opSrc.getType());
                        if (memoryInput.getStride() != 0) {
                            updateMemoryRegions(memoryInfoList, memoryInput);
                        }
                    }
                }
                for (Operand opDst : instr.getDsts()) {
                    if (opDst.getType() == MemoryType.MEM_HEAP_TYPE || opDst.getType() == MemoryType.MEM_STACK_TYPE) {
                        boolean skip = true;
                        if (opDst.getType() == MemoryType.MEM_STACK_TYPE) {
                            for (int addr = 0; addr < 2; addr++) {
                                long reg = opDst.getAddress().get(addr).getValue().longValue();//todo check cast problem
                                if (reg != 0 && reg != DefinesDotH.Registers.DR_REG_EBP.ordinal() && reg != DefinesDotH.Registers.DR_REG_ESP.ordinal()) {
                                    skip = false;
                                }
                            }
                        } else {
                            skip = false;
                        }

                        if (skip) {
                            continue;
                        }

                        memoryInput.setMemAddress(opDst.getValue().longValue());//todo check casting problem
                        memoryInput.setStride(opDst.getWidth());
                        memoryInput.setWrite(true);
                        memoryInput.setType(opDst.getType());
                        if (memoryInput.getStride() != 0) {
                            updateMemoryRegions(memoryInfoList, memoryInput);
                        }
                    }
                }

            }
        }
        postProcesseMemoryRegions(memoryInfoList);
        logger.debug("Creating memory layout : [Memory Info] - Done");
        return memoryInfoList;
    }

    private static void postProcesseMemoryRegions(List<MemoryInfo> memoryInfoList) {
        defragmentMemoryRegions(memoryInfoList);
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
                            current.setDirection(currentDirection);
                            updateStride(current.getStrideFrequency(), candidate.getStrideFrequency());
                            toRemove.add(candidate);
                            break;
                        }
                    /*if current is a subset of the candidate? remove current*/
                        else if ((current.getStart() >= candidate.getStart()) && (current.getEnd() <= candidate.getEnd())) {
                            merged = true;
                        }
                    /* prepend to the candidate?*/
                        else if ((current.getStart() < candidate.getStart()) && (current.getEnd() >= candidate.getStart())) {
                            candidate.setStart(current.getStart());
                            merged = true;
                        }
                    /* append to candidate?*/
                        else if ((current.getStart() <= candidate.getEnd()) && (current.getStart() > candidate.getEnd())) {
                            candidate.setEnd(current.getEnd());
                            merged = true;
                        }

                        if (merged) {
                            int candidateDirection = candidate.getDirection();
                            candidateDirection |= current.getDirection();
                            candidate.setDirection(candidateDirection);
                            updateStride(candidate.getStrideFrequency(), current.getStrideFrequency());
                            toRemove.add(current);
                        }
                    }

                }
            }

            finished = !memoryInfoList.removeAll(toRemove);
        }
    }


}
