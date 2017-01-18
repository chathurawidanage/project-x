package lk.ac.mrt.projectx.buildex.models.memoryinfo;


import lk.ac.mrt.projectx.buildex.models.output.MemoryType;

/**
 * @author Chathura Widanage
 */
public class MemoryInput {
    private String module;
    private long pc;

    private long memAddress;
    private boolean write;
    private int stride;
    private MemoryType type;

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public long getPc() {
        return pc;
    }

    public void setPc(long pc) {
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

    public MemoryType getType() {
        return type;
    }

    public void setType(MemoryType type) {
        this.type = type;
    }
}
