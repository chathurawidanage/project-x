package lk.ac.mrt.projectx.buildex.files;

import lk.ac.mrt.projectx.buildex.Configurations;
import lk.ac.mrt.projectx.buildex.exceptions.NoSuitableFileFoundException;

import java.io.File;
import java.io.FileFilter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wik2kassa on 12/2/2016.
 */
public class InstructionTraceFile extends File {

    public InstructionTraceFile(String pathname) {
        super(pathname);
    }

    public InstructionTraceFile(String parent, String child) {
        super(parent, child);
    }

    public InstructionTraceFile(File parent, String child) {
        super(parent, child);
    }

    public InstructionTraceFile(URI uri) {
        super(uri);
    }

    public static InstructionTraceFile fromFile(File file) {
        return new InstructionTraceFile(file.toURI());
    }

    public static InstructionTraceFile getInstructionTraceFile(final String executableName, final String imageName, int threadId) {
        long maxlen = 0;
        InstructionTraceFile selectedFile = null;
        File[] files = Configurations.getOutputFolder().listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().matches("instrace_" + executableName + "_" + imageName + "_instr_\\d+\\.log");
            }
        });

        for (File file : files) {
            if (file != null && file.length() > maxlen) {
                selectedFile = (InstructionTraceFile) file;
                maxlen = file.length();
            }
        }
        return selectedFile;
    }

    public static InstructionTraceFile getDisassemblyInstructionTrace(final String executableName, final String imageName, int threadId) {
        long maxlen = 0;
        File selectedFile = null;
        File[] files = Configurations.getOutputFolder().listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                String filename = new File(pathname.getName()).getName();
                return filename.matches("instrace_" + executableName + "_" + imageName + "_asm_instr_\\d+\\.log");
            }
        });

        for (File file : files
                ) {
            if (file != null && file.length() > maxlen) {
                selectedFile = file;
                maxlen = file.length();
            }
        }
        return InstructionTraceFile.fromFile(selectedFile);
    }

    public static List<InstructionTraceFile> filterInstructionTraceFiles(List<File> allFiles) {
        List<InstructionTraceFile> instructionTraceFiles = new ArrayList<>();
        for (File f : allFiles) {
            try {
                if (f.getName().matches("instrace_.+_d+\\.log")) {
                    InstructionTraceFile instructionTraceFile = InstructionTraceFile.fromFile(f);
                    instructionTraceFiles.add(instructionTraceFile);
                }
            } catch (Exception e) {

            }
        }
        return instructionTraceFiles;
    }

    public static InstructionTraceFile filterLargestInstructionTraceFile(List<File> allFiles,
                                                                         String inputImageName, boolean disAsm) throws NoSuitableFileFoundException {
        InstructionTraceFile largestInstructionTraceFile = null;
        long fileSize = 0;
        for (File f : allFiles) {
            if (f.getName().matches("instrace_.+_" + inputImageName + "_" + (disAsm ? "asm_" : "") + "instr_\\d+\\.log")) {
                if (f.length() > fileSize) {
                    largestInstructionTraceFile = InstructionTraceFile.fromFile(f);
                    fileSize = f.length();
                }
            }
        }
        if (largestInstructionTraceFile == null)
            throw new NoSuitableFileFoundException("suitable " + (disAsm ? "disasm" : "instrace") + " file cannot be located");
        return largestInstructionTraceFile;
    }
}
