package lk.ac.mrt.projectx.buildex;

import java.io.File;
import java.net.URI;

/**
 * Created by wik2kassa on 12/3/2016.
 */
public class AppProgramCounterFile extends File{

    public static AppProgramCounterFile getFileByParameters(String executableName) {
        AppProgramCounterFile file = new AppProgramCounterFile(Configurations.getFilterFolder() + File.pathSeparator +
        "filter_" + executableName + "_app_pc.log");
        return  file;
    }
    public AppProgramCounterFile(String pathname) {
        super(pathname);
    }

    public AppProgramCounterFile(String parent, String child) {
        super(parent, child);
    }

    public AppProgramCounterFile(File parent, String child) {
        super(parent, child);
    }

    public AppProgramCounterFile(URI uri) {
        super(uri);
    }
}
