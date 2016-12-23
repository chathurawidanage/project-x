package lk.ac.mrt.projectx.buildex;

/**
 * Created by krv on 12/4/2016.
 */
//TODO : index sort comparable (check output.h line 24-33), this may be a linked list
public class Operand<T> implements Comparable {
    Long type = null;
    Long width = null;
    T value = null;
    Operand addr = null;

    @Override
    public int compareTo(Object other) {
        if (!(other instanceof Operand))
            throw new ClassCastException("A Operand object expected.");
        Float f1 = (float) this.value;
        Float f2 = (float) ((Operand) other).value;
        return Float.compare(f1, f2);
    }
}
