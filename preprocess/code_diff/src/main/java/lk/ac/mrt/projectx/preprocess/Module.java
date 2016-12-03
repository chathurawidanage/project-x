package lk.ac.mrt.projectx.preprocess;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.HashSet;

public class Module {

    final static Logger logger = LogManager.getLogger(Module.class);

    private String name;
    private Integer mount;
    private HashSet<Integer> addresses;
    private Integer originalIndex;

    //    Set<String> names = new HashSet<String>();
    public Module() {
        this.name = "";
        mount = 0;
        originalIndex = 0;
        addresses = new HashSet<Integer>();
    }

    public Module(String name, Integer mount, Integer originalIndex) {
        this.name = name;
        this.mount = mount;
        this.originalIndex = originalIndex;
        addresses = new HashSet<>();
    }

    public Integer LoadByDRCovModuleLine(String line) {
        try {
            String[] splitted = line.split(",");
            this.name = splitted[2];
            this.mount = Integer.parseInt(splitted[0].trim());
            addresses.add(Integer.parseInt(splitted[1].trim()));
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
        builder.append(this.mount.toString());
        builder.append(" Number of Addresses : ");
        builder.append(this.addresses.size());
        return builder.toString();
    }

//    public Integer getMount() {
//        return mount;
//    }
//
//    public void setMount(Integer mount) {
//        this.mount = mount;
//    }
//
//    public String getName() {                 V
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }

    public HashSet<Integer> getAddresses() {
        return addresses;
    }

    public Integer getOriginalIndex() {
        return originalIndex;
    }

    public void setOriginalIndex(Integer originalIndex) {
        this.originalIndex = originalIndex;
    }

}