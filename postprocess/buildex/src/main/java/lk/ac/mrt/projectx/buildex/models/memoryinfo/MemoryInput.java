package lk.ac.mrt.projectx.buildex.models.memoryinfo;

/**
 * @author Chathura Widanage
 */
public class MemoryInput {
    private String module;
    private int pc;

    private long memAddress;
    private boolean write;
    private int stride;
    private int type;

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public int getPc() {
        return pc;
    }

    public void setPc(int pc) {
        this.pc = pc;
    }

    public long getMemAddress() {
        return memAddress;
    }

    public void setMemAddress(long memAddress) {
        this.memAddress = memAddress;
    }

    public boolean isWrite() {
        return write;
    }

    public void setWrite(boolean write) {
        this.write = write;
    }

    public int getStride() {
        return stride;
    }

    public void setStride(int stride) {
        this.stride = stride;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
