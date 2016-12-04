package lk.ac.mrt.projectx.buildex;

import com.sun.org.apache.xpath.internal.operations.String;

import java.util.List;

/**
 * Created by krv on 12/4/2016.
 */
public abstract class Node<T> {
    Integer operation;  // Operation of this node
    Boolean sign;   // Signed operation or not
    Boolean minus;
    String functionName;

    //TODO: operant_t
    Operand<T> synbol;

    List<Node> srcs;
    List<Node> prev;

    List<Long> pos;

    Long pc;
    Long line;

    // Auxiliary variables
    Integer order_num;
    Integer para_num;
    Boolean is_para;
    Boolean is_double;

    Boolean visited;

    public abstract String getNodeString();

    public abstract String getDotString();

    public abstract String getSimpleString();

    public Boolean removeForwardReferenceSingle(Node ref) {
        Boolean erased = false;
        Integer idx = srcs.indexOf(ref);
        erased = srcs.remove(ref);
        if (erased) {
            // Updating the reference
            for (int j = 0; j < ref.pos.size(); j++) {
                if(ref.prev.get(j) == this && ref.pos.get(j) == idx){
                    ref.prev.remove(j); //TODO : Removing inside the loop
                    ref.pos.remove(j);
                    break;
                }
            }
        }

        return erased;
    }
}
