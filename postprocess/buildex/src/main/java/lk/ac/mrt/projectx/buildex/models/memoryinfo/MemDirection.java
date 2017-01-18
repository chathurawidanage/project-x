package lk.ac.mrt.projectx.buildex.models.memoryinfo;

/**
 * @author Chathura Widanage
 */
public enum MemDirection {
    READ(0x4), WRITE(0x5), BOTH_READ_WRITE(0x6),
    MEM_INPUT(0x1), MEM_OUTPUT(0x2), MEM_INTERMEDIATE(0x3);

    private int value;
    MemDirection(int value){
        this.value=value;
    }

    public int getValue() {
        return value;
    }
}
