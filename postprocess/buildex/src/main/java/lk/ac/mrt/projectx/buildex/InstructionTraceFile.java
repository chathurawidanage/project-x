package lk.ac.mrt.projectx.buildex;

import java.io.File;
import java.io.FileFilter;
import java.net.URI;

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

        for (File file : files
                ) {
            if(file != null && file.length() > maxlen) {
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
            if(file != null && file.length() > maxlen) {
                selectedFile = file;
                maxlen = file.length();
            }
        }
        return InstructionTraceFile.fromFile(selectedFile);
    }
}
