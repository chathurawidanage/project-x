package lk.ac.mrt.projectx.buildex;

import com.sun.org.apache.xpath.internal.operations.String;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by krv on 12/4/2016.
 */
public abstract class Node<T> {
    final static Logger logger = LogManager.getLogger(Node.class);

    //TODO : Check access modifiers later
    public X86Analysis.Operation operation;  // Operation of this node
    Boolean sign;   // Signed operation or not
    Boolean minus;
    String functionName;

    Operand<T> symbol;

    public List<Node> srcs; ///< forward references also srcs of the destination
    public List<Node> prev; ///< keep the backward references
    public List<Long> pos; ///< position of the parent node's srcs list for this child

    Long pc;
    Long line;

    // Auxiliary variables
    Integer order_num;
    Integer para_num;
    Boolean is_para;
    Boolean is_double;

    Boolean visited;

    public Node() {
        operation = null;
        sign = null;
        minus = null;
        functionName = null;
        srcs = new ArrayList<>();
        prev = new ArrayList<>();
        pos = new ArrayList<>();
        pc = null;
        line = pc;
        order_num = null;
        para_num = null;
        is_double = null;
        is_para = null;
        visited = false;
    }

    public static Boolean isOperationAssociative(X86Analysis.Operation operation) {
        Boolean result;
        switch (operation) {
            case op_add:
            case op_mul:
                result = true;
                break;
            default:
                result = false;
                break;
        }
        return result;
    }

    public abstract String getNodeString();

    public abstract String getDotString();

    public abstract String getSimpleString();


    public int removeForwardReference(Node ref) {
        int count = 0;
        while (removeForwardReferenceSingle(ref)) {
            count++;
        }
        return count;
    }

    /**
     * This function is removing a one forward reference, forward references are in the srcs list
     * Because pos list contains backward references parents srcs lists positions for a node after every update of srcs
     * list need to update the pos list of all the forward references also
     *
     * @param ref
     * @return
     */
    private Boolean removeForwardReferenceSingle(Node ref) {
        // Removing from the src list
        Integer idx = srcs.indexOf(ref);
        if (srcs.remove(ref)) {
            // Updating the backward references of deleted node
            int jidx = ref.prev.indexOf(this);
            if (jidx != -1 && ref.pos.get(jidx) == idx) {
                ref.prev.remove(jidx);
                ref.pos.remove(jidx);
            } else {
                logger.warn("backward reference not found from deleted node");
            }
            // Update backward references of still connected nodes
            // Need to update pos list indexes of others connected to this
            //Since src list is changed
            // TODO : Check the possibility of being a own function
            for (int i = 0; i < srcs.size(); i++) {
                Node curSrcNode = srcs.get(i);
                int thisPosition = curSrcNode.prev.indexOf(this);
                if (thisPosition != -1) {
                    curSrcNode.pos.set(thisPosition, i);
                } else {
                    logger.warn("backward reference not found from src");
                }
                //TODO: Check why Chairth has used a difference logic
//                for (int j = 0; j < prev.size(); j++) {
//                    if (curSrcNode.prev.get(j) == this) {
//                        curSrcNode.pos.set(j, i);
//                        break;
//                    }
//                }
            }

            return true;
        }
        return false;
    }

     public int removeBackwardReference(Node ref) {
        int count = 0;
        while (removeBackwardReferenceSingle(ref)) {
            count++;
        }
        return count;
    }

    private Boolean removeBackwardReferenceSingle(Node ref) {
        int idx = this.prev.indexOf(ref);
        if (idx != -1) {
            this.prev.remove(idx);
            this.pos.remove(idx);
            return true;
        }
        return true;
    }

    /**
     * Adding a node to srcs (forward) list
     *
     * @param ref node to be added
     */
    public void addForwardRefrence(Node ref) {
        this.srcs.add(ref);
        ref.prev.add(this);
        ref.pos.add(this.srcs.size() - 1);
    }

    /**
     * This node has no backward refrences to others then delete backward references from others
     * to this and assume java delete this automatically
     * Note : Delete only if not the head
     *
     * @param head refrence to the head of the tree
     */
    public void safetly_delete(Node head) {
        if (prev.size() == 0 && this != head) {
            for (Node node : srcs) {
                node.removeBackwardReference(this);
            }
        }
    }

    /**
     * This operation will carry out the below logic at the end
     * (dst -> this) => (dst -> src)
     * @param dst dst Node
     * @param src src Node
     */
    private void changeReference(Node dst, Node src){
        /* In place forward reference and push back back ward reference
	        replacing it at the exact same location is important for
	        non-associative operations*/
        //TODO : Check whether need to run a loop and do this to many srcs of dst, currently doing it only to idx index

        int idx = dst.srcs.indexOf(this);
        if(idx != -1){
            dst.srcs.set(idx, src);
            src.prev.add(dst);
            src.pos.add(idx);
        }
//        dst.forwardReference(this); // Redundant
    }

    /**
     * Check whether this node refrence to any node having a indirect operation
     * @return if found a node with indirect operation return the index, -1 otherwise
     */
    public int isIndirect(){
        for(int i = 0 ; i < srcs.size(); i++){
            Node node = srcs.get(i);
            if(node.operation == X86Analysis.Operation.op_indirect){
                return i;
            }
        }
        return -1;
    }

    /**
     * Remove all srcs
     */
    public void removeAllForwardSrcs(){
        for(Node node : srcs){
            this.removeForwardReference(node);
        }
    }

    /**
     * (dst -> this -> src)  => (dst -> src)
     * @param dst
     * @param src
     */
    public void removeIntermediateNode(Node dst, Node src){
        this.changeReference(dst, src);
        this.removeForwardReference(src);
    }

}
