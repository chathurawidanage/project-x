package lk.ac.mrt.projectx.buildex.trees;

import lk.ac.mrt.projectx.buildex.models.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chathura Widanage
 * @author Rukshan Perera
 */
public class AbstractTree extends Tree {
    private List<Pair<AbstractTree,Boolean>> conditionalTrees=new ArrayList<>();

    public List<Pair<AbstractTree, Boolean>> getConditionalTrees() {
        return conditionalTrees;
    }

    public void setConditionalTrees(List<Pair<AbstractTree, Boolean>> conditionalTrees) {
        this.conditionalTrees = conditionalTrees;
    }

    public List<AbstractNode> retrieveParameters(){
        List<AbstractNode> nodes=new ArrayList<>();
        traverseTree(
                this.getHead(),
                nodes,
                new NodeMutator() {
                    @Override
                    public Object mutate(Node node, Object value) {
                        AbstractNode abs_node = (AbstractNode) node;
                        if (abs_node.getType() == AbstractNode.AbstractNodeType.PARAMETER) {
                            List<AbstractNode> nodes = (List<AbstractNode>) value;
                            for (int i = 0; i < nodes.size(); i++) {
                                if (nodes.get(i).para_num
                                        == abs_node.para_num) {
                                    return null;
                                }
                            }
                            nodes.add(abs_node);
                        }
                        return null;
                    }
                },
                new NodeReturnMutator() {
                    @Override
                    public Object mutate(Object nodeValue, List<Object> traverseValue, Object value) {
                        return null;//empty
                    }
                }
        );

        return nodes;
    }

    @Override
    public void simplifyTree() {

    }
}
