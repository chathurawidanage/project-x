package lk.ac.mrt.projectx.buildex.halide;

/**
 * @author Chathura Widanage
 */
public class Expression {
    private String name;
    private String condition;
    private String truthValue;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getTruthValue() {
        return truthValue;
    }

    public void setTruthValue(String truthValue) {
        this.truthValue = truthValue;
    }
}
