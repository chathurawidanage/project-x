package lk.ac.mrt.projectx.buildex.trees;

import com.sun.org.apache.xpath.internal.operations.String;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;

/**
 * Created by krv on 12/30/16.
 */



public class AbstractNode<T> extends Node<T> {
    public static enum AbstractNodeType{
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

    //region Private Variables
    private Integer dimenstions;
    private Integer headDiemensions;
    private ArrayList<ArrayList<Integer>> indexes;
    private ArrayList<Integer> pos;
    //endregion

    public String GetMemString(){
        return NULL;
    }

    @Override
    public String getNodeString() {
        throw new NotImplementedException();
    }

    @Override
    public String getDotString() {
        throw new NotImplementedException();
    }

    @Override
    public String getSimpleString() {
        throw new NotImplementedException();
    }
}
