package lk.ac.mrt.projectx.buildex.files;

import lk.ac.mrt.projectx.buildex.exceptions.NoSuitableFileFoundException;

import java.io.File;
import java.util.List;

/**
 * @author Chathura Widanage
 */
public class ImageConfigFile extends File {
    public ImageConfigFile(String pathname) {
        super(pathname);
    }

    public static ImageConfigFile filterImageConfigFile(List<File> allFiles) throws NoSuitableFileFoundException {
        for (File f : allFiles) {
            if (f.getName().matches(".+_config\\.log")) {
                ImageConfigFile imageConfigFile = new ImageConfigFile(f.getAbsolutePath());
                return imageConfigFile;
            }
        }
        throw new NoSuitableFileFoundException("no suitable config file found");
    }
}
