package lk.ac.mrt.projectx.buildex.trees;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

import static lk.ac.mrt.projectx.buildex.trees.AbstractNode.AbstractNodeType.INPUT_NODE;
import static lk.ac.mrt.projectx.buildex.trees.AbstractNode.AbstractNodeType.OUTPUT_NODE;
import static lk.ac.mrt.projectx.buildex.trees.AbstractNode.AbstractNodeType.INTERMEDIATE_NODE;
import static lk.ac.mrt.projectx.buildex.trees.AbstractNode.AbstractNodeType.IMMEDIATE_INT;


/**
 * Created by krv on 1/2/17.
 */
public class CompleteAbstractNode <T> extends Node<T> implements Comparable {

    //region private variables

    private List<AbstractNode> nodes;

    //endregion private variables

    //region public constructors

    public CompleteAbstractNode(){
        super();
    }

    public CompleteAbstractNode(Node node){
        super(node);
    }

    public CompleteAbstractNode(List<AbstractNode> nodes){
        this.nodes = nodes;
    }

    //endregion public constructors

    //region public methods

    @Override
    public int compareTo(Object o) {
        CompleteAbstractNode other = (CompleteAbstractNode) o;
        int ret;
        if (this.nodes.isEmpty()) {

            ret = other.nodes.isEmpty() ? 1 : 0;

        } else if (other.nodes.isEmpty()) {

            ret = this.nodes.isEmpty() ? 1 : 0;

        } else {

            ret = (nodes.get(0) == other.nodes.get(0)) ? 1 : 0;

        }
        return ret;
    }

    @Override
    public String getNodeString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (AbstractNode node : nodes) {
            if (node.getType() == INPUT_NODE ||
                    node.getType() == OUTPUT_NODE ||
                    node.getType() == INTERMEDIATE_NODE ||
                    node.getType() == IMMEDIATE_INT) {
                stringBuilder.append(node.getNodeString());
                stringBuilder.append("\\n");
            } else {
                stringBuilder.append(node.getNodeString());
                break;
            }
        }
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

}
