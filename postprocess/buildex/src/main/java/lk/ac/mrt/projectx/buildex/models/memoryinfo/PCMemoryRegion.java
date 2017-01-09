package lk.ac.mrt.projectx.buildex.models.memoryinfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chathura Widanage
 */
public class PCMemoryRegion {
    /* at least pc should be populated */
    private String module;
    private long pc;

    List<PCMemoryRegion> fromRegion;/* these are memory dependencies */
    List<MemoryInfo> regions;
    List<PCMemoryRegion> toRegion; /* these are memory dependencies */

    public PCMemoryRegion() {
        this.fromRegion = new ArrayList<>();
        this.regions = new ArrayList<>();
        this.toRegion = new ArrayList<>();
    }

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

    public List<PCMemoryRegion> getFromRegion() {
        return fromRegion;
    }

    public void setFromRegion(List<PCMemoryRegion> fromRegion) {
        this.fromRegion = fromRegion;
    }

    public List<MemoryInfo> getRegions() {
        return regions;
    }

    public void setRegions(List<MemoryInfo> regions) {
        this.regions = regions;
    }

    public List<PCMemoryRegion> getToRegion() {
        return toRegion;
    }

    public void setToRegion(List<PCMemoryRegion> toRegion) {
        this.toRegion = toRegion;
    }
}
