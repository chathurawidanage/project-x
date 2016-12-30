package lk.ac.mrt.projectx.preprocess;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 * Created by Lasantha on 03-Dec-16.
 */
public class AppPcData{

    private static final Logger logger = LogManager.getLogger(AppPcData.class);

    private String modulename;
    private String fileName;
    private ArrayList<Integer> candidateInstructions;

    public AppPcData(String fileName) {
        this.setFileName(fileName);
    }

    public String getModuleName() {
        return modulename;
    }

    public void setModuleName(String name) {
        this.modulename = name;
    }

    public ArrayList<Integer> getCandidateInstructions() {
        return candidateInstructions;
    }

    public void setCandidateInstructions(ArrayList<Integer> candidateInstructions) {
        this.candidateInstructions = candidateInstructions;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void saveDataToFile(){
        logger.info("Writing App PC Data to the file...");
        try{
            File file = new File(this.getFileName());
            FileWriter fileWriter = new FileWriter(file, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.append("1");
            bufferedWriter.newLine();
            bufferedWriter.append("\""+ modulename +"\"");
            bufferedWriter.newLine();
            bufferedWriter.append(candidateInstructions.size()+"");
            bufferedWriter.newLine();

            for (int i = 0; i < candidateInstructions.size(); i++) {
                bufferedWriter.append(candidateInstructions.get(i)+"");
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
            logger.info("Writing App PC Data to the file - Success!");
        }catch (Exception e){
            logger.error("Writing App PC Data to the file - Failed!");
        }

    }
}
