package lk.ac.mrt.projectx.preprocess;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Lasantha on 04-Dec-16.
 */
public class ModuleInfo {
    private static final Logger logger = LogManager.getLogger(ModuleInfo.class);

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

    public static ModuleInfo getPopulatedModuleInfo(byte[] bytes){

        logger.info("populating module information....");

        if(bytes==null || bytes.length==0){
            logger.error("Profile data error!");
            return null;
        }

        Scanner data = new Scanner(new String(bytes));
        ModuleInfo head = new ModuleInfo();

        //get the number of modules
        int moduleCount = Integer.parseInt(data.nextLine());
        logger.info("Number of modules = {}",moduleCount);

        ModuleInfo current = null;
        for (int i = 0; i < moduleCount; i++) {

            if(current==null){
                current = head;
            }else{
                current.setNext(new ModuleInfo());
                current = current.getNext();
            }

            // get the module name
            current.setName(data.nextLine());
            logger.info("Module{} name = {}",i,current.getName());

            // module start address
            current.setStartAddress(Long.parseLong(data.nextLine()));
            logger.info("Module{} start address = {}",i,current.getStartAddress());

            //number of bbs
            int basicBrockCount = Integer.parseInt(data.nextLine());

            for (int j = 0; j < basicBrockCount; j++) {

            }

        }

        return head;
    }
}
