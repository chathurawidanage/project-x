package lk.ac.mrt.projectx.buildex.files;

import lk.ac.mrt.projectx.buildex.exceptions.NoSuitableFileFoundException;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Chathura Widanage
 */
public class MemoryDumpFile extends File {
    // private File memoryDumpFile;
    private boolean write;
    private long baseProgramCounter;
    private byte memoryBuffer[];

    public MemoryDumpFile(String path) throws NoSuitableFileFoundException {
        super(path);
        String[] splits = this.getName().split("_");//todo possible using regex, reversing the name would be required
        int readWriteIndex = splits.length - 2;
        int baseProgramCounterIndex = splits.length - 4;

        //validating for correct file name format
        if (!this.getName().contains("memdump")
                || baseProgramCounterIndex < 0
                || readWriteIndex < 0
                || !(splits[readWriteIndex].equals("0")
                || splits[readWriteIndex].equals("1"))) {
            throw new NoSuitableFileFoundException("Not a qualified memory dump file");
        }
        if (splits[readWriteIndex].equals("1")) {
            this.write = true;
        }
        this.baseProgramCounter = Long.parseLong(splits[baseProgramCounterIndex], 16);
    }

    public boolean isWrite() {
        return write;
    }

    public long getBaseProgramCounter() {
        return baseProgramCounter;
    }

    public byte[] getMemoryBuffer() throws IOException {
        if (memoryBuffer != null) {
            return memoryBuffer;
        }
        memoryBuffer = new byte[(int) this.length()];
        DataInputStream dis = new DataInputStream(new FileInputStream(this));
        dis.readFully(memoryBuffer);
        dis.close();
        return memoryBuffer;
    }

    public static List<MemoryDumpFile> filterMemoryDumpFiles(List<File> allFiles) throws NoSuitableFileFoundException {
        List<MemoryDumpFile> memoryDumpFileList = new ArrayList<>();
        for (File f : allFiles) {
            if (f.getName().matches("memdump_.+_\\d+_\\d+_\\d+_\\d+\\.log")) {
                MemoryDumpFile memoryDumpFile = new MemoryDumpFile(f.getAbsolutePath());
                memoryDumpFileList.add(memoryDumpFile);
            }
        }
        if (memoryDumpFileList.isEmpty()) {
            throw new NoSuitableFileFoundException("No suitable memory dump files found");
        }
        return memoryDumpFileList;
    }
}
