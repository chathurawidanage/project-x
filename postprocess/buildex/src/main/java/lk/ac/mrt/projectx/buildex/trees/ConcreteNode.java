package lk.ac.mrt.projectx.buildex.trees;

import lk.ac.mrt.projectx.buildex.models.memoryinfo.MemoryRegion;
import lk.ac.mrt.projectx.buildex.X86Analysis;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

/**
 * Created by krv on 1/1/17.
 */
public class ConcreteNode <T> extends Node<T> implements Comparable {

    //region private variables

    private MemoryRegion region;

    //endregion private variables

    //region public constructors

    public ConcreteNode(Operand symbol) {
        this.symbol = symbol;
        this.operation = X86Analysis.Operation.op_unknown;
        this.order_num = -1;
        this.is_para = false;
        this.is_double = false;
        this.line = 0L;
        this.pc = 0L;
        this.region = null;
    }

    public ConcreteNode(Operand.OperandType type, T value, Integer width) {
        this.symbol = new Operand<>(type, value, width);
        this.operation = X86Analysis.Operation.op_unknown;
        this.order_num = -1;
        this.is_para = false;
        this.is_double = false;
        this.line = 0L;
        this.pc = 0L;
        this.region = null;
    }

    public ConcreteNode(Operand symbol, List<MemoryRegion> regions) {
        this.symbol = symbol;
        assignMemRegion(regions);
    }

    //endregion public constructors

    //region public methods

    @Override
    public int compareTo(Object o) {
        ConcreteNode node = (ConcreteNode) o;
        if (!node.srcs.isEmpty()) {
            return (this.operation == node.operation) &&
                    (this.srcs.size() == node.srcs.size()) ? 1 : 0;
        } else {
            if (symbol.type == node.symbol.type) {
                if (symbol.type == Operand.OperandType.IMM_INT_TYPE) {
                    return 1;
                } else if (symbol.type == Operand.OperandType.IMM_FLOAT_TYPE) {
                    return Float.compare((Float) symbol.value, (Float) node.symbol.value);
                } else {
                    return 1;
                }
            } else {
                return 0;
            }
        }
    }

    @Override
    public String getNodeString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.operation.toString());
        stringBuilder.append("\\n");
        stringBuilder.append(this.symbol.toString());
        stringBuilder.append("\n");
        stringBuilder.append(this.pc.toString());
        stringBuilder.append(" ");
        stringBuilder.append(this.line.toString());
        return stringBuilder.toString();
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

    private void assignMemRegion(List<MemoryRegion> regions) {
        if (this.symbol.type == Operand.OperandType.MEM_HEAP_TYPE ||
                this.symbol.type == Operand.OperandType.MEM_STACK_TYPE) {
//            this.region = getMemRegion(this.symbol.value, regions);
            throw new NoSuchMethodError("getMemRegion(ConcreteNode, List<MemoryRegion>");
        }
    }

    //endregion private methods

}
