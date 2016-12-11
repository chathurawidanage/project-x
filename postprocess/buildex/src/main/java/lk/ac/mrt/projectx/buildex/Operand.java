package lk.ac.mrt.projectx.buildex;

/**
 * Created by krv on 12/4/2016.
 */
//TODO : index sort comparable (check output.h line 24-33), this may be a linked list
public class Operand<T> {
    Long type = null;
    Long width = null;
    T value = null;
    Operand addr = null;
}
