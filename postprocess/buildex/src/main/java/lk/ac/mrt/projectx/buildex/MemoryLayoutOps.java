package lk.ac.mrt.projectx.buildex;

import lk.ac.mrt.projectx.buildex.files.InstructionTraceFile;
import lk.ac.mrt.projectx.buildex.models.memoryinfo.MemoryInfo;
import lk.ac.mrt.projectx.buildex.models.memoryinfo.MemoryInput;
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

    private Logger logger = LogManager.getLogger(MemoryLayoutOps.class);

    private Operand parseOperand(StringTokenizer stringTokenizer, int version) {
        Operand operand = new Operand();
        operand.setType(Integer.parseInt(stringTokenizer.nextToken()));
        operand.setWidth(Integer.parseInt(stringTokenizer.nextToken()));

        //todo check whether float_value and value is essential if double is used

        operand.setValue(Double.parseDouble(stringTokenizer.nextToken()));

        if (version == VER_WITH_ADDR_OPND) {
            if (operand.getType() == Operand.MEM_STACK_TYPE || operand.getType() == Operand.MEM_HEAP_TYPE) {
            /* we need to collect the address operands */
                Operand address = new Operand();
                for (int j = 0; j < 4; j++) {
                    address.setType(Integer.parseInt(stringTokenizer.nextToken()));
                    address.setWidth(Integer.parseInt(stringTokenizer.nextToken()));
                    address.setValue(Double.parseDouble(stringTokenizer.nextToken()));
                }
                operand.setAddress(address);
            }
        } else if (version == VER_NO_ADDR_OPND) {
            //address is null
        }
        return operand;
    }

    private List<Output> getInstructionsList(InstructionTraceFile instructionTraceFile, int version) throws FileNotFoundException {
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

                instr.setEflags(Integer.parseInt(stringTokenizer.nextToken()));
                instr.setPc(Integer.parseInt(stringTokenizer.nextToken()));

                instructions.add(instr);
            }
        }
        return instructions;
    }

    public List<MemoryInfo> createMemoryLayout(InstructionTraceFile instructionTraceFile, int version) throws FileNotFoundException {
        int count = 0;

        logger.debug("Creating memory layout : [Memory Info]");

        List<Output> instructions = getInstructionsList(instructionTraceFile, version);
        Iterator<Output> instructionsIterator = instructions.iterator();

        while (instructionsIterator.hasNext()) {
            Output instr = instructionsIterator.next();
            MemoryInput memoryInput = new MemoryInput();

            if (instr != null) {
                for (Operand opSrc : instr.getSrcs()) {
                    if (opSrc.getType() == Operand.MEM_HEAP_TYPE || opSrc.getType() == Operand.MEM_STACK_TYPE) {
                        boolean skip = true;
                        if (opSrc.getType() == Operand.MEM_STACK_TYPE) {
                            for (int addr = 0; addr < 2; addr++) {
                                long reg = (long) opSrc.getAddress().getValue();//todo check casting issue
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

                        memoryInput.setMemAddress((long) opSrc.getValue());//todo check cast problem
                        memoryInput.setStride(opSrc.getWidth());
                        memoryInput.setWrite(false);
                        memoryInput.setType(opSrc.getType());
                        if (memoryInput.getStride() != 0) {
                            //update_mem_regions(mem_info, input);//todo implement
                        }
                    }
                }
                for (Operand opDst : instr.getDsts()) {
                    if (opDst.getType() == Operand.MEM_HEAP_TYPE || opDst.getType() == Operand.MEM_STACK_TYPE) {
                        boolean skip = true;
                        if (opDst.getType() == Operand.MEM_STACK_TYPE) {
                            for (int addr = 0; addr < 2; addr++) {
                                long reg = (long) opDst.getAddress().getValue();//todo check cast problem
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

                        memoryInput.setMemAddress((long) opDst.getValue());//todo check casting problem
                        memoryInput.setStride(opDst.getWidth());
                        memoryInput.setWrite(true);
                        memoryInput.setType(opDst.getType());
                        if (memoryInput.getStride() != 0) {
                            //update_mem_regions(mem_info, input);//todo implement
                        }
                    }
                }

            }
        }
        //postprocess_mem_regions(mem_info);//todo implement
        logger.debug("Creating memory layout : [Memory Info] - Done");
        return null;
    }
}
