package lk.ac.mrt.projectx.buildex;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author Chathura Widanage
 */
public class MemoryDumpFile {
    private File memoryDumpFile;
    private boolean write;
    private long baseProgramCounter;
    private byte memoryBuffer[];

    public MemoryDumpFile(File memoryDumpFile) throws Exception {
        this.memoryDumpFile = memoryDumpFile;

        String[] splits = memoryDumpFile.getName().split("_");//todo possible using regex, reversing the name would be required
        int readWriteIndex = splits.length - 2;
        int baseProgramCounterIndex = splits.length - 4;

        //validating for correct file name format
        if (!this.memoryDumpFile.getName().contains("memdump")
                || baseProgramCounterIndex < 0
                || readWriteIndex < 0
                || !(splits[readWriteIndex].equals("0")
                || splits[readWriteIndex].equals("1"))) {
            throw new Exception("Not a qualified memory dump file");
        }
        if (splits[readWriteIndex].equals("1")) {
            this.write = true;
        }
        this.baseProgramCounter = Long.parseLong(splits[baseProgramCounterIndex], 16);
    }

    public File getFile() {
        return memoryDumpFile;
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
        memoryBuffer = new byte[(int) this.getFile().length()];
        DataInputStream dis = new DataInputStream(new FileInputStream(this.getFile()));
        dis.readFully(memoryBuffer);
        dis.close();
        return memoryBuffer;
    }

    @Override
    public String toString() {
        return this.memoryDumpFile.toString();
    }
}
