package lk.ac.mrt.projectx.preprocess;

import java.util.ArrayList;

/**
 * Created by Lasantha on 04-Dec-16.
 */
public class ModuleInfo {

    private ModuleInfo next;    // next module information
    private String name;    // module full path
    private long startAddress;
    private ArrayList<FunctionInfo> functions;

    public ModuleInfo() {
        this.functions = new ArrayList<>();
    }

    public ModuleInfo getNext() {
        return next;
    }

    public void setNext(ModuleInfo next) {
        this.next = next;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(long startAddress) {
        this.startAddress = startAddress;
    }

    public ArrayList<FunctionInfo> getFunctions() {
        return functions;
    }

    public void setFunctions(ArrayList<FunctionInfo> functions) {
        this.functions = functions;
    }
}
