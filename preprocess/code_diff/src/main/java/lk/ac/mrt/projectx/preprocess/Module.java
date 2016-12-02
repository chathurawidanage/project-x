package lk.ac.mrt.projectx.preprocess;


import java.util.ArrayList;

public class Module {
    private String name;
    private Integer mount;
    private ArrayList<Integer> addresses;

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