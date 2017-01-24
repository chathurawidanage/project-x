package lk.ac.mrt.projectx.buildex;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

/**
 * @author Chathura Widanage
 */
public class GeneralUtils {
    private final static Logger logger = LogManager.getLogger(GeneralUtils.class);

    public static void assertAndFail(boolean assertOperation, String errorLog) {
        if (!assertOperation) {
            logger.fatal(errorLog);
            logger.fatal(Thread.currentThread().getStackTrace());
            System.exit(0);
        }
    }

    public static Object deepCopy(Serializable object) {//since clonable not always works
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
            objectOutputStream.close();
            byteArrayOutputStream.close();
            byte[] byteData = byteArrayOutputStream.toByteArray();

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteData);
            return new ObjectInputStream(byteArrayInputStream).readObject();
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Error in cloning",e);
            System.exit(0);//todo exiting for now
            return null;
        }
    }
}
