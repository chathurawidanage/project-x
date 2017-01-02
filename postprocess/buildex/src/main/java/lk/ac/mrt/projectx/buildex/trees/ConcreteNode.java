package lk.ac.mrt.projectx.buildex.trees;

import lk.ac.mrt.projectx.buildex.MemoryRegion;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by krv on 1/1/17.
 */
public class ConcreteNode <T> extends Node <T> implements Comparable{

    //region pubclic constructors


    //endregion pubclic constructors

    //region private variables

    private MemoryRegion region;

    //endregion private variables

    //region public methods

    //endregion public methods

    //region overridden methods

    @Override
    public int compareTo(Object o) {
        ConcreteNode node = (ConcreteNode) o;
        if(!node.srcs.isEmpty()){
            return (this.operation == node.operation) &&
                    (this.srcs.size() == node.srcs.size()) ? 1 : 0;
        }else{
            if(symbol.type == node.symbol.type){
                if(symbol.type == Operand.OperandType.IMM_INT_TYPE){
                    return 1;
                }else if(symbol.type == Operand.OperandType.IMM_FLOAT_TYPE){
                    return Float.compare((Float)symbol.value, (Float)node.symbol.value);
                }else{
                    return 1;
                }
            }else{
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

    //endregion overridden methods

    //region private methods

    //endregion private methods

}
