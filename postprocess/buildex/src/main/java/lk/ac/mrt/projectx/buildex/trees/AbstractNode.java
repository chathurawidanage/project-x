package lk.ac.mrt.projectx.buildex.trees;

import com.sun.org.glassfish.gmbal.AMXMBeanInterface;
import lk.ac.mrt.projectx.buildex.MemoryRegion;
import lk.ac.mrt.projectx.buildex.X86Analysis;
import sun.invoke.empty.Empty;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

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

    public String GetSymbolicString(List<String> vars) {
        throw new NotImplementedException();
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
        if (this.type == AbstractNodeType.INPUT_NODE || this.type == AbstractNodeType.OUTPUT_NODE
                || this.type == AbstractNodeType.INTERMEDIATE_NODE) {
            return GetMemString();
        } else if (this.type == AbstractNodeType.IMMEDIATE_INT || this.type == AbstractNodeType.IMMEDIATE_FLOAT) {
            return this.symbol.value.toString();
        } else if (this.type == AbstractNodeType.OPERATION_ONLY) {
            return this.operation.toString();
        } else if (this.type == AbstractNodeType.PARAMETER) {
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

    //region private methods
    private String getMemString(List<String> vars) {
        StringBuilder retSt = new StringBuilder();
        String in_string = (this.associatedMem.getDirection() == MemoryRegion.Direction.MEM_OUTPUT) ? "_buf_in" : "";

        retSt.append(this.associatedMem.getName());
        retSt.append(in_string);
        retSt.append("(");
        boolean first = true;

        for (int i = 0 ; i < this.dimensions ; i++) {
            for (int j = 0 ; j < this.headDiemensions ; j++) {
                if (this.indexes.get(i).get(j) == 1) {
                    if (!first) {
                        retSt.append("+");
                    }
                    retSt.append(vars.get(j));
                    first = false;
                } else if (this.indexes.get(i).get(j) != 0) {
                    if (!first) {
                        retSt.append("+");
                    }
                    retSt.append("(");
                    retSt.append(this.indexes.get(i).get(j).toString());
                    retSt.append(")");
                    retSt.append("*");
                    retSt.append(vars.get(j));
                    first = false;

                }
            }

            if (!first) {
                if (this.indexes.get(i).get(this.headDiemensions) != 0) {
                    retSt.append("+");
                    retSt.append(this.indexes.get(i).get(this.headDiemensions).toString());
                }
            } else {
                retSt.append(this.indexes.get(i).get(this.headDiemensions).toString());
            }

            if (this.dimensions - 1 != i) {
                retSt.append(",");
            } else {
                retSt.append(")");
            }
        }
        return retSt.toString();
    }
}
