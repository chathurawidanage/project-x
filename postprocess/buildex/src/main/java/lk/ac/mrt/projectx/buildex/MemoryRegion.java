package lk.ac.mrt.projectx.buildex;

import java.util.Arrays;
import java.util.List;

/**
 * @author Chathura Widanage
 */
public class MemoryRegion {
    private final static int DIMENSIONS = 3;

    private int bytesPerPixel;

    private long type;//memory region type based on dependency analysis
    private DumpType dumpType;//memory region type based on dump
    private long treeDirections;//indirect or not
    private long dimension;

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

    public long getDimension() {
        return dimension;
    }

    public void setDimension(long dimension) {
        this.dimension = dimension;
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
                ", dimension=" + dimension +
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


}
