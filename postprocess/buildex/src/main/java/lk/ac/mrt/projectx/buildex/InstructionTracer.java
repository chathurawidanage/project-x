package lk.ac.mrt.projectx.buildex;

import javafx.util.Pair;
import lk.ac.mrt.projectx.buildex.files.InstructionTraceFile;
import lk.ac.mrt.projectx.buildex.models.common.StaticInfo;
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
    private static final Logger logger = LogManager.getLogger( InstructionTracer.class );
    private static InstructionTracer instance = new InstructionTracer();

    private InstructionTracer() {

    }

    public static InstructionTracer getInstance() {
        return instance;
    }

    public void performInstrunctionTrace(InstructionTraceFile debugDisassemblyInstructionTrace, InstructionTraceFile
            instructionTraceFile, List<Long> startPcs, List<Long> endPcs, long version) throws Exception {

        List<StaticInfo> debugDisassemblyStaticInfos = parseDebugDisassembly( debugDisassemblyInstructionTrace );

        printDissassemblyInformation( debugDisassemblyStaticInfos );


        if (startPcs == null || startPcs.size() == 0) {
            if (logger.isDebugEnabled()) {
                logger.debug( InstructionTracer.class.getName(), "Estimating start and end locations of the function\n" );
            }
            Pair<Long, Long> locs = estimateStartEndProgramCounters( debugDisassemblyStaticInfos );

            if (logger.isDebugEnabled()) {
                logger.debug( InstructionTracer.class.getName(), "start " + locs.getKey() + " end - " + locs.getValue() + "\n" );
            }

            if (startPcs == null)
                startPcs = new ArrayList<>();
            if (endPcs == null)
                endPcs = new ArrayList<>();

            startPcs.add( locs.getKey() );
            endPcs.add( locs.getValue() );
        }

        List<Pair<InstructionTraceUnit, StaticInfo>> instructionTraceForwardUnfiltered = walkFileAndGetInstructions( instructionTraceFile, debugDisassemblyStaticInfos, version );
        List<Pair<InstructionTraceUnit, StaticInfo>> instructionTrace = filterInstructionTrace( startPcs, endPcs, instructionTraceForwardUnfiltered );

        List<Pair<InstructionTraceUnit, StaticInfo>> instructionTraceBackward = new ArrayList<>();

        for (Pair<InstructionTraceUnit, StaticInfo> instruction : instructionTrace) {
            instructionTraceBackward.add( new Pair<InstructionTraceUnit, StaticInfo>( instruction.getKey().clone(), instruction.getValue().clone() ) );
        }


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

        try (BufferedReader br = new BufferedReader( new FileReader( disassemblyInstructionTrace ) )) {
            for (String line ; (line = br.readLine()) != null ; ) {
                lineSplit = line.split( "[,_]" );

                moduleNo = Long.parseLong( lineSplit[ 0 ] );
                app_pc = Long.parseLong( lineSplit[ 1 ] );
                disssasmblyString = lineSplit[ 2 ];
                moduleName = line.substring( line.indexOf( lineSplit[ 3 ] ) );

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

                    staticInfo.setModule_no( moduleNo );
                    staticInfo.setPc( app_pc );
                    staticInfo.setDissasembly( disssasmblyString );
                    staticInfo.setModuleName( moduleName );

                    staticInfos.add( staticInfo );
                }
            }
            //sort the list
            Collections.sort( staticInfos, new Comparator<StaticInfo>() {
                @Override
                public int compare(StaticInfo o1, StaticInfo o2) {
                    if (o1.getModule_no() == o2.getModule_no()) {
                        return (int) (o1.getPc() - o2.getPc());
                    } else
                        return (int) (o1.getModule_no() - o2.getModule_no());
                }
            } );
        }
        return staticInfos;
    }

    public Pair<Long, Long> estimateStartEndProgramCounters(List<StaticInfo> staticInfos) throws Exception {
        Pair<Long, Long> result = null;

        long start = staticInfos.get( 0 ).getPc();
        long end = 0;

        List<Long> pcList = new ArrayList<>();

        for (int i = 0 ; i < staticInfos.size() ; i++) {
            if (staticInfos.get( i ).getDissasembly().contains( "ret" ))
                pcList.add( staticInfos.get( i ).getPc() );
        }

        if (pcList.size() == 0) {
            throw new Exception( "Error: No return instructions found in instruction trace" );
        }

        for (int i = 0 ; i < pcList.size() ; i++) {
            if (pcList.get( i ) > start) {
                end = pcList.get( i );
                break;
            }
        }

        result = new Pair<>( start, end );
        return result;
    }

    public List<Pair<InstructionTraceUnit, StaticInfo>> walkFileAndGetInstructions(InstructionTraceFile instructionTraceFile, List<StaticInfo> staticInfo,
                                                                                   long version) throws IOException {
        String[] lineSplit;
        long moduleNo;
        long app_pc;
        StaticInfo info;
        List<Pair<InstructionTraceUnit, StaticInfo>> instructions = null;
        int count = 0;
        int itr = 0;
        InstructionTraceUnit traceUnit;
        InstructionTraceUnit.Operand operand;

        try (BufferedReader br = new BufferedReader( new FileReader( instructionTraceFile ) )) {
            instructions = new ArrayList<>();

            for (String line ; (line = br.readLine()) != null ; ) {
                if (line.length() > 0) {
                    lineSplit = line.split( "," );
                    traceUnit = new InstructionTraceUnit();

                    traceUnit.setOpcode( Long.parseLong( lineSplit[ itr++ ] ) );
                    traceUnit.setNum_dsts( Long.parseLong( lineSplit[ itr++ ] ) );

                    for (int i = 0 ; i < traceUnit.getNum_dsts() ; i++) {
                        operand = new InstructionTraceUnit.Operand();
                        itr = fillOperand( operand, lineSplit, itr, version );
                    }

                    traceUnit.setNum_srcs( Long.parseLong( lineSplit[ itr++ ] ) );

                    for (int i = 0 ; i < traceUnit.getNum_srcs() ; i++) {
                        operand = new InstructionTraceUnit.Operand();
                        itr = fillOperand( operand, lineSplit, itr, version );
                    }

                    traceUnit.setEflags( Long.parseLong( lineSplit[ itr++ ] ) );
                    traceUnit.setPc( Long.parseLong( lineSplit[ itr++ ] ) );

                    count++;

                    info = getStaticInfo( staticInfo, traceUnit.getPc() );

                    if (info == null) {
                        if (logger.isDebugEnabled()) {
                            logger.debug( InstructionTracer.class.getName(), "WARNING: static disassembly not found " + traceUnit.getPc() + ", " + count );
                        }
                        info = new StaticInfo();

                        info.setPc( traceUnit.getPc() );
                        info.setDissasembly( "unknown" );
                        info.setModule_no( 65535 );
                        info.setModuleName( "unknown" );
                        staticInfo.add( info );
                    }

                    instructions.add( new Pair<InstructionTraceUnit, StaticInfo>( traceUnit, info ) );
                }
            }

            Collections.sort( staticInfo, new Comparator<StaticInfo>() {
                @Override
                public int compare(StaticInfo o1, StaticInfo o2) {
                    if (o1.getModule_no() == o1.getModule_no())
                        return (int) (o1.getPc() - o2.getPc());
                    else
                        return (int) (o1.getModule_no() - o2.getModule_no());
                }
            } );

            return instructions;
        }
    }

    public StaticInfo getStaticInfo(List<StaticInfo> staticInfo, long pc) {
        if (staticInfo != null) {
            for (StaticInfo info : staticInfo) {
                if (pc == info.getPc()) {
                    return info;
                }
            }
        }
        return null;
    }

    public int fillOperand(InstructionTraceUnit.Operand op, String[] split, int start, long version) {
        op.setType( Long.parseLong( split[ start++ ] ) );
        op.setWidth( Long.parseLong( split[ start++ ] ) );

        if (op.getType() == InstructionTraceUnit.IMM_FLOAT_TYPE) {
            op.setValue( Float.parseFloat( split[ start++ ] ) );
        } else {
            op.setValue( Long.parseLong( split[ start++ ] ) );
        }

        if (version == VER_WITH_ADDR_OPND) {
            if (op.getType() == InstructionTraceUnit.MEM_STACK_TYPE || op.getType() == InstructionTraceUnit.MEM_HEAP_TYPE) {
                //we need to collect the addr operands
                op.addr = new InstructionTraceUnit.Operand[ 4 ];

                for (int i = 0 ; i < 4 ; i++) {
                    op.addr[ i ] = new InstructionTraceUnit.Operand();
                    op.addr[ i ].setType( Long.parseLong( split[ start++ ] ) );
                    op.addr[ i ].setWidth( Long.parseLong( split[ start++ ] ) );
                    op.addr[ i ].setValue( Long.parseLong( split[ start++ ] ) ); //TODO: handle failure
                }
            } else {
                op.addr = null;
            }
        } else if (version == VER_NO_ADDR_OPND) {
            op.addr = null;
        }
        return start;
    }

    public List<Pair<InstructionTraceUnit, StaticInfo>> filterInstructionTrace(List<Long> startPc, List<Long> endPc,
                                                                               List<Pair<InstructionTraceUnit, StaticInfo>> unfilteredInstructions) {
        boolean start = false;
        int index = -1;
        List<Pair<InstructionTraceUnit, StaticInfo>> filteredInstructions = new ArrayList<>();

        if (logger.isDebugEnabled()) {
            logger.debug( InstructionTracer.class.getName(), "Filtering the instruction trace by start,end pcs\\n" );

            for (Pair<InstructionTraceUnit, StaticInfo> unfilteredInstruction : unfilteredInstructions) {
                if (!start) {
                    index = -1;
                    for (int i = 0 ; i < startPc.size() ; i++) {
                        if (startPc.get( i ) == unfilteredInstruction.getKey().getPc()) {
                            index = i;
                            start = true;
                            break;
                        }
                    }
                }

                if (start) {
                    filteredInstructions.add( unfilteredInstruction );
                    unfilteredInstruction.getValue().setExampleLine( filteredInstructions.size() - 1 ); //TODO check this ?
                }

                if (start && unfilteredInstruction.getKey().getPc() == endPc.get( index )) {
                    start = false;
                }
            }
        }
        return filteredInstructions;
    }

    public void printDissassemblyInformation(List<StaticInfo> staticInfos) {
        for (StaticInfo staticInfo :
                staticInfos) {
            if (logger.isDebugEnabled()) {
                logger.debug( InstructionTracer.class.getName(), staticInfo.getDissasembly() );
            }
            System.out.println( staticInfo.getDissasembly() );
        }
    }

    public void updateRegistersToMemeoryRange(List<Pair<InstructionTraceUnit, StaticInfo>> instructions) {
        if (logger.isDebugEnabled()) {
            logger.debug( InstructionTracer.class.getName(), "Converting DR_REG to memory\n" );
            for (int i = 0 ; i < instructions.size() ; i++) {
                InstructionTraceUnit instruction = instructions.get( i ).getKey();
                for (int j = 0 ; j < instruction.getNum_srcs() ; j++) {
                    if (instruction.srcs.get( j ).getType() == InstructionTraceUnit.REG_TYPE &&
                            (long) instruction.srcs.get( j ).getValue() == 0) {
                        //instruction.srcs.get(j).se
                    }
                }
            }

        }
    }

    public lk.ac.mrt.projectx.buildex.models.Pair<Long, Long> getStartEndPcs(List<StaticInfo> staticInfos, StaticInfo first) {
        Long start = first.getPc();
        List<Long> pcs = new ArrayList<>();

        // get all ret instructions
        for (StaticInfo staticInfo : staticInfos) {
            if (staticInfo.getDissasembly().contains( "ret" )) {
                pcs.add( staticInfo.getPc() );
            }
        }

        assert pcs.size() > 0 : "ERROR : No return instructions found";

        Long end = 0L;

        for (Long pc : pcs) {
            if (pc > start) {
                end = pc;
                break;
            }
        }

        return new lk.ac.mrt.projectx.buildex.models.Pair<>( start, end );
    }
}
