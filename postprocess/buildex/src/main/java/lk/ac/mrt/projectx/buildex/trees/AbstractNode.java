package lk.ac.mrt.projectx.buildex.trees;

import com.sun.org.glassfish.gmbal.AMXMBeanInterface;
import lk.ac.mrt.projectx.buildex.MemoryRegion;
import lk.ac.mrt.projectx.buildex.X86Analysis;
import sun.invoke.empty.Empty;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;

import static java.lang.Math.abs;

/**
 * Created by krv on 12/30/16.
 * Depends on classes: Node, MemoryRegion
 */
public class AbstractNode <T> extends Node<T> implements Comparable {

    public static enum AbstractNodeType {
        OPERATION_ONLY,
        INPUT_NODE,
        OUTPUT_NODE,
        INTERMEDIATE_NODE,
        IMMEDIATE_INT,
        IMMEDIATE_FLOAT,
        PARAMETER,
        UNRESOLVED_SYMBOL,
        SUBTREE_BOUNDARY,
    }

    //region private variables

    private AbstractNodeType type;
    private Integer dimensions;
    private Integer headDiemensions;
    private ArrayList<ArrayList<Integer>> indexes;
    private ArrayList<Integer> pos;
    private MemoryRegion associatedMem;

    //endregion private variables


    //region public methods

    public String GetMemString() {
        StringBuilder memSt = new StringBuilder();
        memSt.append(this.associatedMem.getName());
        memSt.append("(");
        for (int i = 0 ; i < this.dimensions - 1 ; i++) {
            memSt.append(this.pos.get(i));
            memSt.append(",");
        }
        memSt.append(this.pos.get(this.dimensions - 1));
        memSt.append(")");
        return memSt.toString();
    }

    //region overridden methods

    @Override
    public int compareTo(Object o) {
        AbstractNode node = (AbstractNode) o;
        if (this.type == node.type) {
            if (type == AbstractNodeType.OPERATION_ONLY) {
                return (this.operation == node.operation) &&
                        (this.srcs.size() == node.srcs.size()) ? 1 : 0;
            } else {
                if (this.type == AbstractNodeType.IMMEDIATE_INT) {
//                    return this.symbol.value == node.symbol.value;
                    return 1;
                } else if (this.type == AbstractNodeType.IMMEDIATE_FLOAT) {
                    return Float.compare((Float) node.symbol.value, (Float) this.symbol.value);
                } else if (this.type == AbstractNodeType.INPUT_NODE || this.type == AbstractNodeType.OUTPUT_NODE
                        || this.type == AbstractNodeType.INTERMEDIATE_NODE) {
                    if (node.srcs.size() > 0 &&
                            ((Node) node.srcs.get(0)).operation == X86Analysis.Operation.op_indirect) {
                        return 1;
                    }
                }
                return 1;

            }
        } else {
            return 0;
        }
    }

    @Override
    public String getNodeString() {
        if(this.type == AbstractNodeType.INPUT_NODE || this.type == AbstractNodeType.OUTPUT_NODE
                || this.type == AbstractNodeType.INTERMEDIATE_NODE){
            return GetMemString();
        }else if(this.type == AbstractNodeType.IMMEDIATE_INT || this.type == AbstractNodeType.IMMEDIATE_FLOAT){
            return this.symbol.value.toString();
        }else if(this.type == AbstractNodeType.OPERATION_ONLY){
            return this.operation.toString();
        }else if(this.type == AbstractNodeType.PARAMETER){
            return "p_" + this.para_num.toString();
        }
        return "";
    }

    @Override
    public String getDotString() {
        return getDotString();
    }

    @Override
    public String getSimpleString() {
        throw new NotImplementedException();
    }
    //endregion overridden methods
    //endregion public methods
}
