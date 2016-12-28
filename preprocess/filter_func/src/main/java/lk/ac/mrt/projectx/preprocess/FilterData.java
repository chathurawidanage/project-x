package lk.ac.mrt.projectx.preprocess;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * Created by Lasantha on 03-Dec-16.
 */
public class FilterData {
    private static final Logger logger = LogManager.getLogger(FilterData.class);

    private String moduleName;
    private String fileName;
    private long functionAddress;


    public FilterData(String fileName) {
        this.setFileName(fileName);
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String exeFileName) {
        this.moduleName = exeFileName;
    }

    public long getFunctionAddress() {
        return functionAddress;
    }

    public void setFunctionAddress(long functionAddress) {
        this.functionAddress = functionAddress;
    }

    public void saveDataToFile(){
        logger.info("Writing Filter Data to the file...");
        try{
            File file = new File(this.getFileName());
            FileWriter fileWriter = new FileWriter(file, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.append("1");
            bufferedWriter.newLine();
            bufferedWriter.append("\""+ moduleName +"\"");
            bufferedWriter.newLine();
            bufferedWriter.append("1");
            bufferedWriter.newLine();
            bufferedWriter.append(functionAddress+"");
            bufferedWriter.newLine();
            bufferedWriter.close();
            logger.info("Writing Filter Data to the file - Success!");
        }catch (Exception e){
            logger.error("Writing Filter Data to the file - Failed!");
        }

    }
}
