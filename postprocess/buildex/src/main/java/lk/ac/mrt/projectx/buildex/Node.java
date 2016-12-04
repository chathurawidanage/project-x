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

    List<Node> srcs; ///< forward references also srcs of the destination
    List<Node> prev; ///< keep the backward references
    List<Long> pos; ///< position of the parent node's srcs list for this child

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
            int jidx = ref.prev.indexOf(this);
            if(jidx != -1 && ref.pos.get(jidx) == idx){
                ref.prev.remove(jidx);
                ref.pos.remove(jidx);
            }
            // Update backward references of still connected node
        }

        return erased;
    }
}
