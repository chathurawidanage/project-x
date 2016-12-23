package lk.ac.mrt.projectx.buildex;

import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by wik2kassa on 12/2/2016.
 */
public class InstructionTracer {
    public static final long VER_NO_ADDR_OPND = 0;
    public static final long VER_WITH_ADDR_OPND = 1;
    private static final Logger logger = LogManager.getLogger(InstructionTracer.class);
    private static InstructionTracer instance = new InstructionTracer();

    private InstructionTracer() {

    }

    public static InstructionTracer getInstance() {
        return instance;
    }

    /**
     * Parse the disassembly instruction trace file. The format of a single file is as follows
     * [module no], [app program counter]_[dissassembly string]_, [module name]
     * eg:
     * 1,159799_or     ah, 0x0c_C:\Program Files (x86)\IrfanView\Plugins\Effects.dll
     * module no               : 1
     * app program counter     : 159799
     * dissembly string        : or     ah
     * module name             : 0x0c_C:\Program Files (x86)\IrfanView\Plugins\Effects.dll
     *
     * @param disassemblyInstructionTrace Reference to File
     * @return
     */
    public List<StaticInfo> parseDebugDisassembly(InstructionTraceFile disassemblyInstructionTrace) throws IOException {
        String[] lineSplit;
        ArrayList<StaticInfo> staticInfos = new ArrayList<>();
        StaticInfo staticInfo = null;

        long moduleNo;
        long app_pc;
        String disssasmblyString;
        String moduleName = "";
        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader(disassemblyInstructionTrace))) {
            for (String line; (line = br.readLine()) != null; ) {
                lineSplit = line.split("[,_]");

                moduleNo = Long.parseLong(lineSplit[0]);
                app_pc = Long.parseLong(lineSplit[1]);
                disssasmblyString = lineSplit[2];
                moduleName = line.substring(line.indexOf(lineSplit[3]));

                found = false;
                for (StaticInfo stinfo : staticInfos
                        ) {
                    if (staticInfo.getModule_no() == moduleNo && stinfo.getPc() == app_pc) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    staticInfo = new StaticInfo();

                    staticInfo.setModule_no(moduleNo);
                    staticInfo.setPc(app_pc);
                    staticInfo.setDissasembly(disssasmblyString);
                    staticInfo.setModuleName(moduleName);

                    staticInfos.add(staticInfo);
                }
            }
            //sort the list
            Collections.sort(staticInfos, new Comparator<StaticInfo>() {
                @Override
                public int compare(StaticInfo o1, StaticInfo o2) {
                    if (o1.getModule_no() == o2.getModule_no()) {
                        return (int) (o1.getPc() - o2.getPc());
                    } else
                        return (int) (o1.getModule_no() - o2.getModule_no());
                }
            });
        }
        return staticInfos;
    }

    public Pair<Long, Long> estimateStartEndProgramCounters(List<StaticInfo> staticInfos) throws Exception {
        Pair<Long, Long> result = null;

        long start = staticInfos.get(0).getPc();
        long end = 0;

        List<Long> pcList = new ArrayList<>();

        for (int i = 0; i < staticInfos.size(); i++) {
            if (staticInfos.get(i).getDissasembly().contains("ret"))
                pcList.add(staticInfos.get(i).getPc());
        }

        if (pcList.size() == 0) {
            throw new Exception("Error: No return instructions found in instruction trace");
        }

        for (int i = 0; i < pcList.size(); i++) {
            if (pcList.get(i) > start) {
                end = pcList.get(i);
                break;
            }
        }

        result = new Pair<>(start, end);
        return result;
    }

    public List<Pair<Long, StaticInfo>> walkFileAndGetInstructions(InstructionTraceFile instructionTraceFile, StaticInfo staticInfo,
                                                                   long version) {
        //TODO: Implement
        return null;
    }

    public InstructionTraceUnit getNextInstructionFromStream(BufferedReader br, long version) throws IOException {
        InstructionTraceUnit instruction = new InstructionTraceUnit();

        String line = br.readLine();
        String[] split;
        if (line.length() > 0) {
            split = line.split(",");
            instruction.setOpcode(Long.parseLong(split[0]));
            instruction.setNum_dsts(Long.parseLong(split[1]));

        }
        //TODO : Complete
        return null;
    }

    public long fillOperand(InstructionTraceUnit.Operand op, String[] split, int start, int version) {
        op.setType(Long.parseLong(split[start++]));
        op.setWidth(Long.parseLong(split[start++]));

        if (op.getType() == InstructionTraceUnit.IMM_FLOAT_TYPE) {
            op.setValue(Float.parseFloat(split[start++]));
        } else {
            op.setValue(Long.parseLong(split[start++]));
        }
        //TODO :  Complete this
        if (version == VER_WITH_ADDR_OPND) {
//            if(op.getType() == InstructionTraceUnit.MEM_STACK_TYPE || InstructionTraceUnit.MEM_HEAP_TYPE) {
//
//            }
        }
        return 0;
    }

    public void printDissassemblyInformation(List<StaticInfo> staticInfos, long app_pc) {
        for (StaticInfo staticInfo :
                staticInfos) {
            if (logger.isDebugEnabled()) {
                logger.debug(InstructionTracer.class.getName(), staticInfo.getDissasembly());
            }
            System.out.println(staticInfo.getDissasembly());
        }
    }

}
