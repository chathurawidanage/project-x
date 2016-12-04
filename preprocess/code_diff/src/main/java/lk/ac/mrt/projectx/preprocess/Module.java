package lk.ac.mrt.projectx.preprocess;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

/**
 * @author krv
 */
public class Module implements Comparable<Module> {

    final static Logger logger = LogManager.getLogger(Module.class);

    private String name;
    private Integer id;
    private Set<Integer> addresses;
    private Integer originalIndex;

    //    Set<String> names = new HashSet<String>();
    public Module() {
        this.name = "";
        id = 0;
        originalIndex = -1;
        addresses = new HashSet<>();
    }

    public Module(String name, Integer id, Integer originalIndex) {
        this.name = name;
        this.id = id;
        this.originalIndex = originalIndex;
        addresses = new HashSet<>();
    }

    // Read all the module details
    // eg :  11, 40960, C:\Windowow64\LPK.dll
    public Integer LoadByDRCovModuleLine(String line) {
        try {
            String[] splitted = line.split(",");
            this.name = splitted[2];
            this.id = Integer.parseInt(splitted[0].trim());
            logger.debug("Parsing line DRCov model : ", this.toString());
            return 1;
        } catch (NumberFormatException e) {
            logger.error(e.getMessage());
        }
        return -1;
    }

    // Trying to find if equal using name
    @Override
    public boolean equals(Object o) {
        return o.equals(this.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Name : ");
        builder.append(this.name.toString());
        builder.append(" Mount : ");
        builder.append(this.id.toString());
        builder.append(" Number of Addresses : ");
        builder.append(this.addresses.size());
        return builder.toString();
    }


    public String getName() {
        return name;
    }


    public Set<Integer> getAddresses() {
        return addresses;
    }

    public Integer getOriginalIndex() {
        return originalIndex;
    }

    public void setOriginalIndex(Integer originalIndex) {
        this.originalIndex = originalIndex;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public int compareTo(Module o) {
        return this.name.compareTo(o.name);
    }
}