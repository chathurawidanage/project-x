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

    public static ModuleInfo getPopulatedModuleInfo(byte[] bytes) {

        logger.info("populating module information....");

        if (bytes == null || bytes.length == 0) {
            logger.error("Profile data error!");
            return null;
        }

        Scanner data = new Scanner(new String(bytes));
        ModuleInfo head = new ModuleInfo();

        //get the number of modules
        int moduleCount = Integer.parseInt(data.nextLine());
        logger.info("Number of modules = {}", moduleCount);

        ModuleInfo current = null;
        for (int i = 0; i < moduleCount; i++) {

            if (current == null) {
                current = head;
            } else {
                current.setNext(new ModuleInfo());
                current = current.getNext();
            }

            // get the module name
            current.setName(data.nextLine());
            logger.info("Module{} name = {}", i, current.getName());

            // module start address
            current.setStartAddress(Long.parseLong(data.nextLine(),16));
            logger.info("Module{} start address = {}", i, current.getStartAddress());

            //number of bbs
            int basicBlockCount = Integer.parseInt(data.nextLine());
            logger.info("Module{} basicBlockCount = {}", i, basicBlockCount);

            for (int j = 0; j < basicBlockCount; j++) {
                int index = 0;
                String[] line = data.nextLine().split(",");

                long funcStartAddress = Long.parseLong(line[index++], 16);

                // find if there is a function already in the function list of the module
                ArrayList<FunctionInfo> functions = current.getFunctions();
                int funcCount = functions.size();
                FunctionInfo functionInfo = null;
                for (int k = 0; k < funcCount; k++) {
                    functionInfo = functions.get(k);
                    if (functionInfo.getStartAddress() == funcStartAddress) {
                        functions.remove(functionInfo); // remove it. we add it again later
                        break;
                    }
                }

                if (functionInfo == null) {
                    functionInfo = new FunctionInfo();
                }

                functions.add(functionInfo);    // add the function info
                functionInfo.setStartAddress(funcStartAddress);
                BasicBlockInfo basicBlockInfo = new BasicBlockInfo();
                functionInfo.getBasicBlocks().add(basicBlockInfo);

                // get basic block info
                basicBlockInfo.setStartAddress(Long.parseLong(line[index++], 16));
                basicBlockInfo.setSize(Integer.parseInt(line[index++]));
                basicBlockInfo.setFrequency(Integer.parseInt(line[index++]));
                basicBlockInfo.setCall(Integer.parseInt(line[index++]) == 1);
                basicBlockInfo.setRet(Integer.parseInt(line[index++]) == 1);
                basicBlockInfo.setCallTarget(Integer.parseInt(line[index++]) == 1);
                basicBlockInfo.setFunctionAddress(0);

                int fromBBs = Integer.parseInt(line[index++]);

                for (int k = 0; k < fromBBs; k++) {
                    TargetInfo info = new TargetInfo();
                    info.setTarget(Integer.parseInt(line[index++],16));
                    info.setFrequency(Integer.parseInt(line[index++]));
                    basicBlockInfo.getFromBasicBlocks().add(info);
                }

                int toBBs = Integer.parseInt(line[index++]);

                for (int k = 0; k < toBBs; k++) {
                    TargetInfo info = new TargetInfo();
                    info.setTarget(Integer.parseInt(line[index++],16));
                    info.setFrequency(Integer.parseInt(line[index++]));
                    basicBlockInfo.getToBasicBlocks().add(info);
                }

                int calledFrom = Integer.parseInt(line[index++]);

                for (int k = 0; k < calledFrom; k++) {
                    TargetInfo info = new TargetInfo();
                    info.setTarget(Integer.parseInt(line[index++],16));
                    info.setFrequency(Integer.parseInt(line[index++]));
                    basicBlockInfo.getCallees().add(info);
                }

                int calledTo = Integer.parseInt(line[index++]);

                for (int k = 0; k < calledTo; k++) {
                    TargetInfo info = new TargetInfo();
                    info.setTarget(Integer.parseInt(line[index++],16));
                    info.setFrequency(Integer.parseInt(line[index++]));
                    basicBlockInfo.getCallers().add(info);
                }

            }

        }

        logger.info("All data loaded! Populating function frequency...");

        ModuleInfo temp = head;

        while (temp!=null){
            ArrayList<FunctionInfo> functions = temp.getFunctions();

            for (int i = 0; i < functions.size(); i++) {
                FunctionInfo func = functions.get(i);
                func.setFrequency(0);

                ArrayList<BasicBlockInfo> bbs = func.getBasicBlocks();
                for (int j = 0; j < bbs.size(); j++) {
                    ArrayList<TargetInfo> callees = bbs.get(j).getCallees();
                    int callCount = 0;
                    for (int k = 0; k < callees.size(); k++) {
                        callCount+=callees.get(k).getFrequency();
                    }
                    // TODO This seems to be wrong. It should be += ?(moduleinfo.cpp - line 29)
                    func.setFrequency(callCount);   // keeping as in cpp file
                }

                if(func.getFrequency()==0){
                    func.setFrequency(func.getBasicBlocks().get(0).getFrequency());
                }
            }

            temp = temp.getNext();
        }

        logger.info("populating module information - DONE!");

        return head;
    }


    /* mining information from the module structure */
    public static ModuleInfo findModule(ModuleInfo head,  long startAddress){
        while (head != null){
            if (head.getStartAddress()==startAddress){
                return head;
            }
            head = head.getNext();
        }
        return null;
    }

}
