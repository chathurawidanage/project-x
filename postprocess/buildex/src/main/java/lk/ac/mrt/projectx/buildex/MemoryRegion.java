package lk.ac.mrt.projectx.buildex;

/**
 * @author Chathura Widanage
 */
public class MemoryRegion {
    private int bytesPerPixel;
    private int type;
    private int dumpType;
    private int treeDirections;
    private int dimentsion;

    public Direction direction;

    public long startMemory, endMemory;


    /**
     * Memory read/write or both
     */
    public enum Direction {
        READ, WRITE, BOTH_READ_WRITE
    }

}
