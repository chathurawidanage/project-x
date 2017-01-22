package lk.ac.mrt.projectx.buildex.trees;

import lk.ac.mrt.projectx.buildex.models.Pair;
import lk.ac.mrt.projectx.buildex.models.common.StaticInfo;
import lk.ac.mrt.projectx.buildex.models.memoryinfo.MemoryRegion;
import lk.ac.mrt.projectx.buildex.models.output.Output;
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
    private static final Logger logger = LogManager.getLogger(ConcreteTreeUtils.class);

    private static final short FILE_BEGINNING = -2;
    private static final short FILE_ENDING = -1;

    public static List<List<ConcreteTree>> clusterTrees(List<MemoryRegion> memRegions, List<MemoryRegion> totalRegions,
                                                        List<Integer> startPoints, List<Pair<Output, StaticInfo>> instrs,
                                                        Integer farthest, String outputFolder, List<FunctionInfo> funcInfo){

        logger.debug( "Building trees for all locations in the output and clustering" );
        MemoryRegion mem = MemoryRegionUtils.getRandomOutputRegion( ((ArrayList<MemoryRegion>) memRegions) );

        List<ConcreteTree> trees = new ArrayList<>();
        List<List<Integer>> indexes = mem.getIndexList();
        List<Integer> offset = indexes.get( 0 );

        boolean success = true;

        int i = 0;
        if(mem.getStartMemory() > mem.getEndMemory()){
            i = indexes.size() -1;
        }

        boolean done = false;
        long count = 0;

        while (!done) {
            Long location = mem.getMemLocation(indexes.get( i ), offset);
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

    private static ConcreteTree buildConcreteTree(Long location, int bytesPerPixel, List<Integer> startPoints,
                                                  short fileBeginning, short fileEnding, ConcreteTree tree,
                                                  List<Pair<Output, StaticInfo>> instrs, Integer farthest,
                                                  List<MemoryRegion> totalRegions, List<FunctionInfo> funcInfo) {
        throw new NotImplementedException();
    }
}
