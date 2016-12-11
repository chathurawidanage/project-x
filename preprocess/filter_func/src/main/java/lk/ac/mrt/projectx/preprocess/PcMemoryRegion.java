package lk.ac.mrt.projectx.preprocess;

import java.util.ArrayList;

/**
 * Created by Lasantha on 11-Dec-16.
 */
public class PcMemoryRegion {
    /* at least pc should be populated */
    private String module;
    private int pc;

    private ArrayList<PcMemoryRegion> fromRegions;  /* these are memory depedancies */
    private ArrayList<MemoryInfo> regions;
    private ArrayList<PcMemoryRegion> toRegions;   /* these are memory dependencies */


    public PcMemoryRegion() {
        fromRegions = new ArrayList<>();
        regions = new ArrayList<>();
        toRegions = new ArrayList<>();
    }

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

    public ArrayList<PcMemoryRegion> getFromRegions() {
        return fromRegions;
    }

    public void setFromRegions(ArrayList<PcMemoryRegion> fromRegions) {
        this.fromRegions = fromRegions;
    }

    public ArrayList<MemoryInfo> getRegions() {
        return regions;
    }

    public void setRegions(ArrayList<MemoryInfo> regions) {
        this.regions = regions;
    }

    public ArrayList<PcMemoryRegion> getToRegions() {
        return toRegions;
    }

    public void setToRegions(ArrayList<PcMemoryRegion> toRegions) {
        this.toRegions = toRegions;
    }
}
