package lk.ac.mrt.projectx.buildex;

import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Chathura Widanage
 */
public class MemoryRegion {
    private static final Logger logger = LogManager.getLogger(MemoryRegion.class);
    private final static int DIMENSIONS = 3;

    private int bytesPerPixel;

    private long type;//memory region type based on dependency analysis
    private DumpType dumpType;//memory region type based on dump
    private long treeDirections;//indirect or not
    private long dimentsion;

    /* indirect or not */
    private boolean dependant;

    private Direction direction;

    /* physical demarcations of the memory regions */
    private long startMemory, endMemory;

    private String name; // krv - needed in AbstractNode

    //halide buffer_t emulation
    private long extents[];
    private long strides[];
    private long min[];

    private long paddingField;//right left up and down
    private long padding[];//padding for four directions

    private List<Long> referingPCs;

    public MemoryRegion() {
        extents = new long[DIMENSIONS];
        strides = new long[DIMENSIONS];
        min = new long[DIMENSIONS];

        type = 0;
        direction = Direction.READ;
        dumpType = DumpType.OUTPUT_BUFFER;
        treeDirections = 0;
        dependant = false;
    }

    public static int getDIMENSIONS() {
        return DIMENSIONS;
    }

    public int getBytesPerPixel() {
        return bytesPerPixel;
    }

    public void setBytesPerPixel(int bytesPerPixel) {
        this.bytesPerPixel = bytesPerPixel;
    }

    public long getType() {
        return type;
    }

    public void setType(long type) {
        this.type = type;
    }

    public DumpType getDumpType() {
        return dumpType;
    }

    public void setDumpType(DumpType dumpType) {
        this.dumpType = dumpType;
    }

    public long getTreeDirections() {
        return treeDirections;
    }

    public void setTreeDirections(long treeDirections) {
        this.treeDirections = treeDirections;
    }

    public long getDimentsion() {
        return dimentsion;
    }

    public void setDimentsion(long dimentsion) {
        this.dimentsion = dimentsion;
    }

    public boolean isDependant() {
        return dependant;
    }

    public void setDependant(boolean dependant) {
        this.dependant = dependant;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public long getStartMemory() {
        return startMemory;
    }

    public void setStartMemory(long startMemory) {
        this.startMemory = startMemory;
    }

    public long getEndMemory() {
        return endMemory;
    }

    public void setEndMemory(long endMemory) {
        this.endMemory = endMemory;
    }

    public long[] getExtents() {
        return extents;
    }

    public void setExtents(long[] extents) {
        this.extents = extents;
    }

    public long[] getStrides() {
        return strides;
    }

    public void setStrides(long[] strides) {
        this.strides = strides;
    }

    public long[] getMin() {
        return min;
    }

    public void setMin(long[] min) {
        this.min = min;
    }

    public long getPaddingField() {
        return paddingField;
    }

    public void setPaddingFilled(long paddingField) {
        this.paddingField = paddingField;
    }

    public long[] getPadding() {
        return padding;
    }

    public void setPadding(long[] padding) {
        this.padding = padding;
    }

    public List<Long> getReferingPCs() {
        return referingPCs;
    }

    public void setReferingPCs(List<Long> referingPCs) {
        this.referingPCs = referingPCs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "MemoryRegion{" +
                "bytesPerPixel=" + bytesPerPixel +
                ", type=" + type +
                ", dumpType=" + dumpType +
                ", treeDirections=" + treeDirections +
                ", dimentsion=" + dimentsion +
                ", dependant=" + dependant +
                ", direction=" + direction +
                ", startMemory=" + startMemory +
                ", endMemory=" + endMemory +
                ", name='" + name + '\'' +
                ", extents=" + Arrays.toString(extents) +
                ", strides=" + Arrays.toString(strides) +
                ", min=" + Arrays.toString(min) +
                ", paddingField=" + paddingField +
                ", padding=" + Arrays.toString(padding) +
                ", referingPCs=" + referingPCs +
                '}';
    }

    public enum DumpType {
        OUTPUT_BUFFER, INPUT_BUFFER
    }

    /**
     * Memory read/write or both
     * TODO: check the better convention READ, WRITE .. convention or INPUT, OUTPUT...
     */
    public enum Direction {
        READ, WRITE, BOTH_READ_WRITE, MEM_INPUT, MEM_OUTPUT, MEM_INTERMEDIATE
    } // krv - added input, output and intermediate coz Helium seems to be using this convention words

    //region public methods

    /* abstracting memory locations from mem_regions */
    public static long getMemLocation(ArrayList<Integer> base, ArrayList<Integer> offset, MemoryRegion memRegion) {

        // success boolean parameter ignored.

        if (base.size() != memRegion.getDimentsion()) {
            logger.error("ERROR: dimensions dont match up");
        }

        for (int i = 0; i < base.size(); i++) {
            base.set(i, base.get(i) + offset.get(i));
        }

        for (int i = 0; i < base.size(); i++) {
            if (base.get(i) >= memRegion.getExtents()[i]) {
                return 0;
            }
        }

        long retAddr;
        if (memRegion.getStartMemory() < memRegion.getEndMemory()) {
            retAddr = memRegion.getStartMemory();
            for (int i = 0; i < base.size(); i++) {
                retAddr += memRegion.getStrides()[i] * base.get(i);
            }
        } else {
            retAddr = memRegion.getStartMemory();
            for (int i = 0; i < base.size(); i++) {
                retAddr -= memRegion.getStrides()[i] * base.get(i);
            }
        }

        return retAddr;

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

        for (int i = (int) memoryRegion.getDimentsion() - 1; i >= 0; i--) {
            int pointOffset = (int) (offset / memoryRegion.getStrides()[i]);
            if (pointOffset >= memoryRegion.getExtents()[i]) {
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
    //endregion public methods

}
