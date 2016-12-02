package lk.ac.mrt.projectx.preprocess;


import java.util.ArrayList;

public class Module {
    private String name;
    private Integer mount;
    private ArrayList<Integer> addresses;

    public Module (){
        this.name = "";
        mount = 0;
        addresses = new ArrayList<Integer>();
    }

    public Integer LoadByDRCovModuleLine(String line){
        String [] splitted = line.split(",");
        this.name = splitted[splitted.length-1];
        return 1;
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append("Name : ");
        builder.append(this.name.toString());
        builder.append(" Mount : ");
        builder.append(this.mount.toString());
        builder.append(" Number of Addresses : ");
        builder.append(this.addresses.size());
        System.out.println(builder.toString());
        return builder.toString();
    }
    public Integer getMount() {
        return mount;
    }

    public void setMount(Integer mount) {
        this.mount = mount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Integer> getAddresses() {
        return addresses;
    }

    public void setAddresses(ArrayList<Integer> addresses) {
        this.addresses = addresses;
    }
}