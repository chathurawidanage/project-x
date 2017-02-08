package lk.ac.mrt.projectx.buildex.trees;

import lk.ac.mrt.projectx.buildex.DefinesDotH;
import lk.ac.mrt.projectx.buildex.models.Pair;
import lk.ac.mrt.projectx.buildex.models.common.CommonUtil;
import lk.ac.mrt.projectx.buildex.models.common.StaticInfo;
import lk.ac.mrt.projectx.buildex.models.common.StaticInfoUtil;
import lk.ac.mrt.projectx.buildex.models.memoryinfo.MemDirection;
import lk.ac.mrt.projectx.buildex.models.memoryinfo.MemoryInfo;
import lk.ac.mrt.projectx.buildex.models.memoryinfo.MemoryRegion;
import lk.ac.mrt.projectx.buildex.models.memoryinfo.PCMemoryRegion;
import lk.ac.mrt.projectx.buildex.models.output.MemoryType;
import lk.ac.mrt.projectx.buildex.models.output.Operand;
import lk.ac.mrt.projectx.buildex.models.output.Output;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static lk.ac.mrt.projectx.buildex.models.memoryinfo.MemDirection.MEM_INPUT;
import static lk.ac.mrt.projectx.buildex.models.memoryinfo.MemDirection.MEM_OUTPUT;

/**
 * Created by Lasantha on 04-Jan-17.
 */
public class MemoryRegionUtils {

    private static final Logger logger = LogManager.getLogger( MemoryRegionUtils.class );

    //region public methods

    // stride was a pointer. - Not considered it as a pointer here. if needs, it should be implemented
    public static ArrayList<Long> getNbdOfRandomPoints2(ArrayList<MemoryRegion> memoryRegions, int seed, int stride) {

        MemoryRegion randomMemRegion = getRandomOutputRegion( memoryRegions );
        //cout << hex << random_mem_region->start << endl;
        long memLocation = getRandomMemLocation( randomMemRegion, seed );
        logger.info( "random mem location we got - {}", memLocation );
        stride = randomMemRegion.getBytesPerPixel();

        ArrayList<Long> nbdLocations = new ArrayList<>();
        ArrayList<Integer> base = getMemPosition( randomMemRegion, memLocation );
        nbdLocations.add( memLocation );

        /*
        // printing - ignored
        cout << "base : " << endl;
        for (int j = 0; j < base.size(); j++){
            cout << base[j] << ",";
        }
        cout << endl;
        */

        //get a nbd of locations - diagonally choose pixels
        int boundary = (int) ((randomMemRegion.getDimension() + 2) / 2.0);
        logger.info( "boundary : {}", boundary );

        int count = 0;

        int[] val = new int[ (int) randomMemRegion.getDimension() ];

        for (int i = -boundary ; i <= boundary ; i++) {

            ArrayList<Integer> offset = new ArrayList<>();
            int affected = count % (int) randomMemRegion.getDimension();
            if (count == 4) {
                count++;
                val[ affected ]++;
                continue;
            }
            for (int j = 0 ; j < base.size() ; j++) {
                if (j == affected) {
                    val[ j ]++;
                    if (val[ j ] + base.get( j ) < randomMemRegion.getExtents()[ j ] && val[ j ] + base.get( j ) > 0) {
                        offset.add( val[ j ] );
                    } else {
                        offset.add( 0 );
                    }

                } else offset.add( 0 );
            }

            /*
            cout << "offset" << endl;
            for (int j = 0; j < offset.size(); j++){
                cout << offset[j] << ",";
            }
            cout << endl;
            */


            memLocation = getMemLocation( base, offset, randomMemRegion );

            if (memLocation == 0) {
                logger.error( "ERROR: random memory location out of bounds" );
            }

            nbdLocations.add( memLocation );
            count++;
        }

        return nbdLocations;


    }

    public static long getRandomMemLocation(MemoryRegion region, int seed) {

        logger.info( "selecting a random output location now....." );

        Random rand = new Random();
        rand.setSeed( seed );
        int random_num = rand.nextInt();

        ArrayList<Integer> base = new ArrayList<>();
        ArrayList<Integer> offset = new ArrayList<>();
        for (long i = 0 ; i < region.getDimension() ; i++) {
            offset.add( 0 );  // TODO this is not sure for 100%
        }
        //vector<int> offset(region->dimensions, 0);

        for (int i = 0 ; i < region.getDimension() ; i++) {
            base.add( random_num % (int) region.getExtents()[ i ] );
//            cout << dec << base[i] << endl;
//            cout << "reg extents : " << region->extents[i] << endl;
        }

//        for (int i = 0; i < region->dimensions; i++){
//            cout << "strides: " << region->strides[i] << endl;
//            cout << "extent: " << region->extents[i] << endl;
//        }


        long memLocation = getMemLocation( base, offset, region );

        if (memLocation == 0) {
            logger.error( "ERROR: random memory location out of bounds" );
        }

        return memLocation;
    }

    /* abstracting memory locations from mem_regions */
    public static long getMemLocation(ArrayList<Integer> base, ArrayList<Integer> offset, MemoryRegion memRegion) {

        // success boolean parameter ignored.

        if (base.size() != memRegion.getDimension()) {
            logger.error( "ERROR: dimensions dont match up" );
        }

        for (int i = 0 ; i < base.size() ; i++) {
            base.set( i, base.get( i ) + offset.get( i ) );
        }

        for (int i = 0 ; i < base.size() ; i++) {
            if (base.get( i ) >= memRegion.getExtents()[ i ]) {
                return 0;
            }
        }

        long retAddr;
        if (memRegion.getStartMemory() < memRegion.getEndMemory()) {
            retAddr = memRegion.getStartMemory();
            for (int i = 0 ; i < base.size() ; i++) {
                retAddr += memRegion.getStrides()[ i ] * base.get( i );
            }
        } else {
            retAddr = memRegion.getStartMemory();
            for (int i = 0 ; i < base.size() ; i++) {
                retAddr -= memRegion.getStrides()[ i ] * base.get( i );
            }
        }

        return retAddr;

    }

    public static MemoryRegion getRandomOutputRegion(ArrayList<MemoryRegion> regions) {

        logger.info( "selecting a random output region now......." );

	    /*get the number of intermediate and output regions*/
        int noRegions = 0;
        for (int i = 0 ; i < regions.size() ; i++) {
            if (regions.get( i ).getMemDirection() == MemDirection.MEM_INTERMEDIATE || regions.get( i ).getMemDirection() == MEM_OUTPUT) {
                noRegions++;
            }
        }

        Random rand = new Random();
        int random = rand.nextInt( noRegions );

        noRegions = 0;

        for (int i = 0 ; i < regions.size() ; i++) {
            if (regions.get( i ).getMemDirection() == MemDirection.MEM_INTERMEDIATE || regions.get( i ).getMemDirection() == MEM_OUTPUT) {
                if (noRegions == random) {
                    logger.info( "random output region seleted" );
                    return regions.get( i );
                }
                noRegions++;
            }
        }

        return null; /*should not reach this point*/


    }

    public static ArrayList<Integer> getMemPosition(MemoryRegion memoryRegion, long memValue) {

        ArrayList<Integer> pos = new ArrayList<>();
        ArrayList<Integer> rPos = new ArrayList<>();

	/* dimensions would always be width dir(x), height dir(y) */

	/*get the row */

        long offset;

        if (memoryRegion.getStartMemory() < memoryRegion.getEndMemory()) {
            offset = memValue - memoryRegion.getStartMemory();
        } else {
            offset = memoryRegion.getStartMemory() - memValue;
        }

        for (int i = (int) memoryRegion.getDimension() - 1 ; i >= 0 ; i--) {
            int pointOffset = (int) (offset / memoryRegion.getStrides()[ i ]);
            if (pointOffset >= memoryRegion.getExtents()[ i ]) {
                pointOffset = -1;
            }
            rPos.add( pointOffset );

            offset -= pointOffset * memoryRegion.getStrides()[ i ];

        }

        for (int i = 0 ; i < rPos.size() ; i++) {
            pos.add( rPos.get( i ) );
        }

        return pos;

    }

    public static long getRegionSize(MemoryRegion region) {

        long size = 1;
        int dimension = (int) region.getDimension();
        for (int i = 0 ; i < dimension ; i++) {
            size *= region.getExtents()[ i ];
        }
        return size;

    }

    public static MemoryRegion getMemRegion(Integer value, List<MemoryRegion> memoryRegions) {
        MemoryRegion region = null;
        for (MemoryRegion memRegion : memoryRegions) {
            if (memRegion.getStartMemory() < memRegion.getEndMemory()) {
                // start <= value <= end
                if ((memRegion.getStartMemory() <= value) && memRegion.getEndMemory() >= value) {
                    region = memRegion;
                    break;
                }
            } else {
                // end <= value <= start
                if ((memRegion.getStartMemory() >= value) && (memRegion.getEndMemory() <= value)) {
                    region = memRegion;
                    break;
                }
            }
        }
        return region;
    }

    public static boolean isWithinMemRegion(MemoryRegion memoryRegion, int value) {

        if (memoryRegion.getStartMemory() < memoryRegion.getEndMemory()) {
            return (value >= memoryRegion.getStartMemory()) && (value <= memoryRegion.getEndMemory());
        } else {
            return (value >= memoryRegion.getEndMemory()) && (value <= memoryRegion.getStartMemory());
        }
    }

    public static Long getFarthestMemAccessPoint(List<MemoryRegion> regions) {
        Long maxAddr = 0L;
        for (MemoryRegion region : regions) {
            if ((region.getStartMemory() < region.getEndMemory()) && (maxAddr < region.getEndMemory())) {
                maxAddr = region.getEndMemory();
            } else if ((region.getStartMemory() > region.getEndMemory()) && (maxAddr < region.getStartMemory())) {
                maxAddr = region.getStartMemory();
            }
        }
        return maxAddr + 16;
    }

    public static void removePossibleStackFrames(List<PCMemoryRegion> pcMem, List<MemoryInfo> mem, List<StaticInfo>
            info, List<Pair<Output, StaticInfo>> instrs) {
        logger.debug( "Removing stack frames and out of scope mem infos" );
        logger.debug( "Mem info size - %d", mem.size() );

        for (Iterator<MemoryInfo> memoryInfoIterator = mem.iterator() ; memoryInfoIterator.hasNext() ; ) {
            boolean found = false;
            MemoryInfo curMem = memoryInfoIterator.next();
            for (PCMemoryRegion pcMemRegion : pcMem) {
                List<MemoryInfo> memoryInfos = pcMemRegion.getRegions();
                for (MemoryInfo memInfo : memoryInfos) {
                    if (CommonUtil.isOverlapped( curMem.getStart(), curMem.getEnd(), memInfo.getStart(), memInfo.getEnd() )) {
                        StaticInfo staticInfo = StaticInfoUtil.getStaticInfo( info, pcMemRegion.getPc() );
                        if (staticInfo.getExampleLine() == -1) {
                            continue;
                        }
                        Output instr = instrs.get( staticInfo.getExampleLine() ).first;
                        if ((memInfo.getDirection() & MemDirection.MEM_OUTPUT.ordinal()) == MemDirection.MEM_OUTPUT.ordinal()) {
                            for (Operand opnd : instr.getDsts()) {
                                if ((opnd.getType() == MemoryType.MEM_HEAP_TYPE) || (opnd.getType() == MemoryType.MEM_STACK_TYPE)) {
                                    // TODO : why only two addresses
                                    for (int addr = 0 ; addr < 2 ; addr++) {
                                        if (opnd.getAddress().get( addr ).getValue() != 0) {
                                            if ((opnd.getAddress().get( addr ).memRangeToRegister() != DefinesDotH
                                                    .DR_REG.DR_REG_RBP) && (opnd.getAddress().get( addr ).memRangeToRegister() != DefinesDotH
                                                    .DR_REG.DR_REG_RSP)) {
                                                found = true;
                                                break;
                                            }
                                        }
                                    } // for (int addr = 0 ; addr < 2 ; addr++)
                                }
                            } // for (Operand opnd : instr.getDsts())
                        } //if ((memInfo.getDirection() & MemDirection.MEM_OUTPUT.ordinal()) == MemDirection.MEM_OUTPUT.ordinal())

                        if ((memInfo.getDirection() & MEM_INPUT.ordinal()) == MEM_INPUT.ordinal()) {
                            for (Operand opnd : instr.getSrcs()) {
                                if ((opnd.getType() == MemoryType.MEM_HEAP_TYPE) || (opnd.getType() == MemoryType.MEM_STACK_TYPE)) {
                                    for (int addr = 0 ; addr < 2 ; addr++) {
                                        if (opnd.getAddress().get( addr ).getValue() != 0) {
                                            if ((opnd.getAddress().get( addr ).memRangeToRegister() != DefinesDotH
                                                    .DR_REG.DR_REG_RBP) && (opnd.getAddress().get( addr ).memRangeToRegister() != DefinesDotH
                                                    .DR_REG.DR_REG_RSP)) {
                                                found = true;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    }
                    if (found) {
                        break;
                    }
                }
                if (found) {
                    break;
                }
            }

            if (!found) {
                mem.remove( curMem );
            }
        }

        logger.debug( "mem infor size after - %d", mem.size() );
    }

    /* extracting random locations from the mem regions */
    ArrayList<Long> getNbdOfRandomPoints(ArrayList<MemoryRegion> memoryRegions, int seed, int stride) {

	    /*ok we need find a set of random locations */
        MemoryRegion randomMemRegion = getRandomOutputRegion( memoryRegions );
        //cout << hex << random_mem_region->start << endl;
        long memLocation = getRandomMemLocation( randomMemRegion, seed );
        logger.info( "random mem location we got - {}", memLocation );
        stride = randomMemRegion.getBytesPerPixel();

        ArrayList<Long> nbdLocations = new ArrayList<>();
        ArrayList<Integer> base = getMemPosition( randomMemRegion, memLocation );
        nbdLocations.add( memLocation );

        //get a nbd of locations - diagonally choose pixels
        int boundary = (int) ((randomMemRegion.getDimension() + 2) / 2.0);
        logger.info( "boundary : {}", boundary );

        int count = 0;

        int[] val = new int[ (int) randomMemRegion.getDimension() ];

        for (int i = -boundary ; i <= boundary ; i++) {

            if (i == 0) {
                continue;
            }

            ArrayList<Integer> offset = new ArrayList<>();
            int affected = count % (int) randomMemRegion.getDimension();
            for (int j = 0 ; j < base.size() ; j++) {
                if (j == affected) {
                    if (base.get( j ) + i < 0 || base.get( j ) + i >= randomMemRegion.getExtents()[ j ]) {
                        offset.add( 0 );
                    } else {
                        offset.add( i );
                    }

                } else offset.add( 0 );
            }

            memLocation = getMemLocation( base, offset, randomMemRegion );

            if (memLocation == 0) {
                logger.error( "ERROR: random memory location out of bounds" );
            }

            nbdLocations.add( memLocation );
            count++;
        }

        return nbdLocations;
    }
    //endregion public methods

}
