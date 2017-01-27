package lk.ac.mrt.projectx.buildex.trees;

import lk.ac.mrt.projectx.buildex.models.memoryinfo.MemDirection;
import lk.ac.mrt.projectx.buildex.models.memoryinfo.MemoryRegion;
import lk.ac.mrt.projectx.buildex.x86.X86Analysis;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

import static lk.ac.mrt.projectx.buildex.models.output.MemoryType.MEM_HEAP_TYPE;
import static lk.ac.mrt.projectx.buildex.models.output.MemoryType.MEM_STACK_TYPE;


/**
 * Created by krv on 12/30/16.
 * Depends on classes: Node, MemoryRegion
 */
public class AbstractNode extends Node implements Comparable, Cloneable {//chathura - generics removed

    //region public enum

    private AbstractNodeType type;

    //endregion public enum

    //region private variables
    private Integer dimensions;
    private Integer headDiemensions;
    private ArrayList<ArrayList<Integer>> indexes;
    private ArrayList<Integer> pos;
    private MemoryRegion associatedMem;

    public AbstractNode() {
        super();
    }

    public void setType(AbstractNodeType type) {
        this.type = type;
    }

    public Integer getDimensions() {
        return dimensions;
    }

    public void setDimensions(Integer dimensions) {
        this.dimensions = dimensions;
    }

    public Integer getHeadDiemensions() {
        return headDiemensions;
    }

    public void setHeadDiemensions(Integer headDiemensions) {
        this.headDiemensions = headDiemensions;
    }

    public ArrayList<ArrayList<Integer>> getIndexes() {
        return indexes;
    }

    public void setIndexes(ArrayList<ArrayList<Integer>> indexes) {
        this.indexes = indexes;
    }

    public ArrayList<Integer> getPos() {
        return pos;
    }

    public void setPos(ArrayList<Integer> pos) {
        this.pos = pos;
    }

    public MemoryRegion getAssociatedMem() {
        return associatedMem;
    }

    public void setAssociatedMem(MemoryRegion associatedMem) {
        this.associatedMem = associatedMem;
    }

    //endregion private variables

    //region public constructors

    public AbstractNode(AbstractNode node) {//todo is this for cloning? If yes, this won't clone -chathura
        super(node);
    }

    public AbstractNode(ConcreteNode head, ConcreteNode concreteNode, List<MemoryRegion> memRegions) {
        //TODO : Finish it refer (abs_node.cpp)
        super(concreteNode);
        this.minus = false;
        this.operation = concreteNode.operation;
        this.para_num = concreteNode.para_num;
        this.symbol = concreteNode.symbol;
        this.functionName = concreteNode.functionName;

        boolean filer = false;
        if (concreteNode == head) {
            filer = true;
        } else if (concreteNode.srcs.isEmpty()) {
            filer = true;
        } else {
            for (Object nd : concreteNode.srcs) {
                Node node = (Node) nd;
                if (node.operation == X86Analysis.Operation.op_indirect) {
                    filer = true;
                    //TODO : No break here in Helium
                    break;
                }
            }
        }

        this.associatedMem = null;
        MemoryRegion mem = null;
        if (concreteNode.symbol.getType() == MEM_STACK_TYPE || concreteNode.symbol.getType() == MEM_HEAP_TYPE) {
            mem = MemoryRegionUtils.getMemRegion((Integer) concreteNode.symbol.getValue(), memRegions);
        }

        if ((mem != null) && filer) {

        }
        throw new NotImplementedException();
    }

    public AbstractNodeType getType() {
        return type;
    }

    //endregion public constructors

    //region public methods

    public String GetSymbolicString(List<String> vars) {
        String ret = "";
        if ((type == AbstractNodeType.INPUT_NODE) || (type == AbstractNodeType.OUTPUT_NODE) || (type == AbstractNodeType.INTERMEDIATE_NODE)){
            ret = getMemString(vars);
        }
        else if (type == AbstractNodeType.IMMEDIATE_INT){
            ret = getImmediateString(vars);
        }
        else{
            ret = getNodeString();
        }

        //ret += " " + to_string(this->minus);
        return ret;
    }

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
                    return Float.compare((Float) node.symbol.getValue(), (Float) this.symbol.getValue());
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

    private String getSymbolicString(List<String> vars) {
        String stRet;
        if (this.type == AbstractNodeType.INPUT_NODE || this.type == AbstractNodeType.OUTPUT_NODE
                || this.type == AbstractNodeType.INTERMEDIATE_NODE) {
            stRet = getMemString(vars);
        } else if (this.type == AbstractNodeType.IMMEDIATE_INT) {
            stRet = getImmediateString(vars);
        } else {
            stRet = getNodeString();
        }
        return stRet;
    }

    @Override
    public String getNodeString() {
        if (this.type == AbstractNodeType.INPUT_NODE || this.type == AbstractNodeType.OUTPUT_NODE
                || this.type == AbstractNodeType.INTERMEDIATE_NODE) {
            return GetMemString();
        } else if (this.type == AbstractNodeType.IMMEDIATE_INT || this.type == AbstractNodeType.IMMEDIATE_FLOAT) {
            return this.symbol.getValue().toString();
        } else if (this.type == AbstractNodeType.OPERATION_ONLY) {
            return this.operation.toString();
        } else if (this.type == AbstractNodeType.PARAMETER) {
            return "p_" + this.para_num.toString();
        }
        return "";
    }

    public String GetMemString() {
        StringBuilder memSt = new StringBuilder();
        memSt.append(this.associatedMem.getName());
        memSt.append("(");
        for (int i = 0; i < this.dimensions - 1; i++) {
            memSt.append(this.pos.get(i));
            memSt.append(",");
        }
        memSt.append(this.pos.get(this.dimensions - 1));
        memSt.append(")");
        return memSt.toString();
    }

    @Override
    public String getDotString() {
        return getDotString();
    }

    @Override
    public String getSimpleString() {
        throw new NotImplementedException();
    }

    //endregion public methods

    //region private methods

    private String getMemString(List<String> vars) {
        StringBuilder stringBuilder = new StringBuilder();
        String in_string = (this.associatedMem.getMemDirection() == MemDirection.MEM_OUTPUT) ? "_buf_in" : "";

        stringBuilder.append(this.associatedMem.getName());
        stringBuilder.append(in_string);
        stringBuilder.append("(");
        boolean first = true;

        for (int i = 0; i < this.dimensions; i++) {
            for (int j = 0; j < this.headDiemensions; j++) {
                if (this.indexes.get(i).get(j) == 1) {
                    if (!first) {
                        stringBuilder.append("+");
                    }
                    stringBuilder.append(vars.get(j));
                    first = false;
                } else if (this.indexes.get(i).get(j) != 0) {
                    if (!first) {
                        stringBuilder.append("+");
                    }
                    stringBuilder.append("(");
                    stringBuilder.append(this.indexes.get(i).get(j).toString());
                    stringBuilder.append(")");
                    stringBuilder.append("*");
                    stringBuilder.append(vars.get(j));
                    first = false;

                }
            }

            if (!first) {
                if (this.indexes.get(i).get(this.headDiemensions) != 0) {
                    stringBuilder.append("+");
                    stringBuilder.append(this.indexes.get(i).get(this.headDiemensions).toString());
                }
            } else {
                stringBuilder.append(this.indexes.get(i).get(this.headDiemensions).toString());
            }

            if (this.dimensions - 1 != i) {
                stringBuilder.append(",");
            } else {
                stringBuilder.append(")");
            }
        }
        return stringBuilder.toString();
    }

    private String getImmediateString(List<String> vars) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < dimensions; i++) {
            if (headDiemensions == i) {
                stringBuilder.append(indexes.get(0).get(i).toString());
            } else if (0 != indexes.get(0).get(i)) {
                stringBuilder.append(indexes.get(0).get(i).toString());
                stringBuilder.append(" * ");
                stringBuilder.append(vars.get(i));
                stringBuilder.append(" + ");
            }
        }
        return stringBuilder.toString();
    }

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

    //endregion private methods

}