package lk.ac.mrt.projectx.preprocess;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DRCoveStructure {

    final static Logger logger = LogManager.getLogger(DRCoveStructure.class);

    Integer noOfBasicBlocks;
    Integer noOfModules;
    ArrayList<Module> modules;
    ArrayList<Integer> duplicateIndexes;
    ArrayList<Integer> locateOriginals;
    Integer drcovVersion;

    public static void main(String[] args) {
        // Prints "Hello, World" to the terminal window.
        System.out.println("Hello, World");
    }

    public void LoadFromFile(String fileName){
        BufferedReader bufferedReader = null;
        FileReader fileReader = null;
        String currentLine;
        try {
            fileReader = new FileReader(fileName);
            bufferedReader = new BufferedReader(fileReader);

            while ((currentLine = bufferedReader.readLine()) != null) {
                System.out.println(currentLine);
            }


        }catch (FileNotFoundException e){
            logger.error(e.getMessage());
        }catch(IOException e){
            logger.error(e.getMessage());
        }
    }
}

