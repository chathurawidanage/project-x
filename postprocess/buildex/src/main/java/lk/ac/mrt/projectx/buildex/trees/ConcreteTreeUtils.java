package lk.ac.mrt.projectx.buildex.trees;

import lk.ac.mrt.projectx.buildex.models.Pair;
import lk.ac.mrt.projectx.buildex.models.common.StaticInfo;
import lk.ac.mrt.projectx.buildex.models.memoryinfo.MemoryRegion;
import lk.ac.mrt.projectx.buildex.models.output.Operand;
import lk.ac.mrt.projectx.buildex.models.output.Output;
import lk.ac.mrt.projectx.buildex.models.output.ReducedInstruction;
import lk.ac.mrt.projectx.buildex.models.output.ReducedInstructionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.servlet.jsp.tagext.FunctionInfo;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by krv on 1/21/17.
 */
public class ConcreteTreeUtils {

    private static final Logger logger = LogManager.getLogger( ConcreteTreeUtils.class );

    private static final int FILE_BEGINNING = -2;
    private static final int FILE_ENDING = -1;

    public static List<List<ConcreteTree>> clusterTrees(List<MemoryRegion> memRegions, List<MemoryRegion> totalRegions,
                                                        List<Long> startPoints, List<Pair<Output, StaticInfo>> instrs,
                                                        Long farthest, String outputFolder, List<FunctionInfo> funcInfo) {

        logger.debug( "Building trees for all locations in the output and clustering" );
        MemoryRegion mem = MemoryRegionUtils.getRandomOutputRegion( ((ArrayList<MemoryRegion>) memRegions) );

        List<ConcreteTree> trees = new ArrayList<>();
        List<List<Integer>> indexes = mem.getIndexList();
        List<Integer> offset = indexes.get( 0 );

        boolean success = true;

        int i = 0;
        if (mem.getStartMemory() > mem.getEndMemory()) {
            i = indexes.size() - 1;
        }

        boolean done = false;
        long count = 0;

        while (!done) {
            Long location = mem.getMemLocation( indexes.get( i ), offset );
            assert location != null : "Getting mem location error";

            logger.debug( "Building tree for location error" );

            ConcreteTree tree = new ConcreteTree();
            tree.setTreeNum( i );

            ConcreteTree initialTree = buildConcreteTree( location, mem.getBytesPerPixel(), startPoints, FILE_BEGINNING,
                    FILE_ENDING, tree, instrs, farthest, totalRegions, funcInfo );
        }

        throw new NotImplementedException();
//        return null;
    }

    private static ConcreteTree buildConcreteTree(Long destination, Integer stride, List<Long> startPoints,
                                                  Integer startTrace, Integer endTrace, ConcreteTree tree,
                                                  List<Pair<Output, StaticInfo>> instrs, Long farthest,
                                                  List<MemoryRegion> regions, List<FunctionInfo> funcInfo) {
        logger.debug( "Build tree multi  " );
        Integer initialEntracne = endTrace;

        Pair<Integer, Integer> points = getStartAndEndPoints( startPoints, destination, stride, startTrace,
                endTrace, instrs );

        startTrace =  points.first;
        endTrace = points.second;

        if(endTrace == FILE_ENDING){
            endTrace = instrs.size();
        }

        if (startTrace == FILE_BEGINNING) {
            startTrace = 0;
        }

        assert endTrace >= startTrace : "Trace end should be greater than the trace start";

        Long initialStart = startTrace.longValue();
        Integer curPos = startTrace;

        // now we need to read the next lint adn start from the correct destination
        Output instr = instrs.get( curPos ).first;
        List<ReducedInstruction> rinstr = ReducedInstructionUtils.cinstrToRinstrsEflags( instr,
                instrs.get( curPos ).second.getDissasembly(), curPos );
        int index = -1;
        boolean destPresent = false;
        for (int i = 0 ; i < rinstr.size() ; i++) {
            ReducedInstruction rins = rinstr.get( i );
            if (rins.getDst().getValue() == destination) {
                index = i;
                destPresent = true;
                break;
            }
        }

        if(!destPresent || index < 0 ){
            return null;
        }

        // build the initial part of the tree
        for (int i = index ; i >= 0  ; i--) {
            tree.updateDependencyBackward( rinstr.get( i ), instrs.get( curPos ).first, instrs.get( curPos ).second,
                    curPos, regions, funcInfo );
        }


        throw new NotImplementedException();
    }

    /**
     *  Start is the one after the Output which points to the destination given
     *  end is the next largest or equal value in startPoints list
     * @param startPoints
     * @param destination
     * @param stride
     * @param startTrace
     * @param endTrace
     * @param instrs
     * @return Pair <start, end> Integer values
     */
    private static Pair<Integer, Integer> getStartAndEndPoints(List<Long> startPoints, Long destination, Integer stride,
                                                               Integer startTrace, Integer endTrace, List<Pair<Output, StaticInfo>> instrs) {
        Integer start = startTrace;
        Integer end = endTrace;

        if (startTrace == FILE_BEGINNING) {
            boolean found = false;
            for (int i = 0 ; i < instrs.size() ; i++) {
                Output opt = instrs.get( i ).first;
                for (int j = 0 ; j < opt.getDsts().size() ; j++) {
                    Operand dstOperand = opt.getDsts().get( j );
                    if (dstOperand.getValue() == destination && dstOperand.getWidth() == stride) {
                        start = i;
                        found = true;
                        break;
                    }
                    if (found) {
                        break;
                    }
                }
            }
        }

        // TODO : Unsafe casting
        if(endTrace == FILE_ENDING){
            for (int i = 0 ; i < startPoints.size() ; i++) {
                if (start <= startPoints.get( i )) {
                    end = startPoints.get( i ).intValue();
                    break;
                }
            }
        }

        return new Pair<>( start, end );
    }
}
