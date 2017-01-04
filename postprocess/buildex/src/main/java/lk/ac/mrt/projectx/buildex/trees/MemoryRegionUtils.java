package lk.ac.mrt.projectx.buildex.trees;

import lk.ac.mrt.projectx.buildex.MemoryRegion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lasantha on 04-Jan-17.
 */
public class MemoryRegionUtils {

    private static final Logger logger = LogManager.getLogger(MemoryRegionUtils.class);

    //region public methods

    /* abstracting memory locations from mem_regions */
    public static long getMemLocation(ArrayList<Integer> base, ArrayList<Integer> offset, MemoryRegion memRegion){

        // success boolean parameter ignored.

        if(base.size()!=memRegion.getDimension()){
            logger.error("ERROR: dimensions dont match up");
        }

        for (int i = 0; i < base.size(); i++){
            base.set(i,base.get(i)+offset.get(i));
        }

        for (int i = 0; i < base.size(); i++){
            if (base.get(i) >= memRegion.getExtents()[i]){
                return 0;
            }
        }

        long retAddr;
        if (memRegion.getStartMemory() < memRegion.getEndMemory()){
            retAddr = memRegion.getStartMemory();
            for (int i = 0; i < base.size(); i++){
                retAddr += memRegion.getStrides()[i] * base.get(i);
            }
        }
        else{
            retAddr = memRegion.getStartMemory();
            for (int i = 0; i < base.size(); i++){
                retAddr -= memRegion.getStrides()[i] * base.get(i);
            }
        }

        return retAddr;

    }

    public static ArrayList<Integer> getMemPosition(MemoryRegion memoryRegion, long memValue){

        ArrayList<Integer> pos = new ArrayList<>();
        ArrayList<Integer> rPos = new ArrayList<>();

	/* dimensions would always be width dir(x), height dir(y) */

	/*get the row */

        long offset;

        if (memoryRegion.getStartMemory() < memoryRegion.getEndMemory()){
            offset = memValue - memoryRegion.getStartMemory();
        }
        else{
            offset = memoryRegion.getStartMemory() - memValue;
        }

        for (int i = (int)memoryRegion.getDimension() - 1; i >= 0; i--){
            int pointOffset = (int)(offset / memoryRegion.getStrides()[i]);
            if (pointOffset >= memoryRegion.getExtents()[i]){
                pointOffset = -1;
            }
            rPos.add(pointOffset);

            offset -= pointOffset * memoryRegion.getStrides()[i];

        }

        for (int i = 0; i < rPos.size(); i++) {
            pos.add(rPos.get(i));
        }

        return pos;

    }

    public static MemoryRegion getMemRegion(Integer value, List<MemoryRegion> memoryRegions) {
        MemoryRegion region = null;
        for (MemoryRegion memRegion : memoryRegions) {
            if (memRegion.getStartMemory() < memRegion.getEndMemory()) {
                // start <= value <= end
                if ((memRegion.getStartMemory() <= value) && memRegion.getEndMemory() >= value){
                    region = memRegion;
                    break;
                }
            }else{
                // end <= value <= start
                if((memRegion.getStartMemory() >= value) && (memRegion.getEndMemory() <= value)){
                    region = memRegion;
                    break;
                }
            }
        }
        return region;
    }

    public static boolean isWithinMemRegion(MemoryRegion memoryRegion, int value){

        if (memoryRegion.getStartMemory() < memoryRegion.getEndMemory()){
            return (value >= memoryRegion.getStartMemory()) && (value <= memoryRegion.getEndMemory());
        }
        else{
            return (value >= memoryRegion.getEndMemory()) && (value <= memoryRegion.getStartMemory());
        }
    }
    //endregion public methods

}
