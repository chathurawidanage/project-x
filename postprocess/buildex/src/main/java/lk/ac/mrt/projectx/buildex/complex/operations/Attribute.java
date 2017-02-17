package lk.ac.mrt.projectx.buildex.complex.operations;

/**
 * @author Chathura Widanage
 */
public class Attribute {
    private String name;
    private String code;
    private double value;

    public Attribute(String name, String code, double value) {
        this.name = name;
        this.code = code;
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public double getValue() {
        return value;
    }
}
