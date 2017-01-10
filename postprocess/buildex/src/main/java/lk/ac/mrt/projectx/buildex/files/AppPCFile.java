package lk.ac.mrt.projectx.buildex.files;

import lk.ac.mrt.projectx.buildex.exceptions.NoSuitableFileFoundException;

import java.io.File;
import java.util.List;

/**
 * Application program counter file
 *
 * @author Chathura Widanage
 */
public class AppPCFile extends File {
    public AppPCFile(String pathname) {
        super(pathname);
    }

    public static AppPCFile filterAppPCFile(List<File> allFiles,String exec) throws NoSuitableFileFoundException {
        for (File f : allFiles) {
            if (f.getName().matches(".+_"+exec+"_app_pc\\.log")) {
                AppPCFile appPCFile = new AppPCFile(f.getAbsolutePath());
                return appPCFile;
            }
        }
        throw new NoSuitableFileFoundException("no suitable app pc file found");
    }
}
