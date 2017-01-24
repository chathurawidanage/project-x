package lk.ac.mrt.projectx.buildex.trees;

import lk.ac.mrt.projectx.buildex.models.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chathura Widanage
 * @author Rukshan Perera
 */
public class AbstractTree extends Tree {
    private List<Pair<AbstractTree, Boolean>> conditionalTrees = new ArrayList<>();

    public List<Pair<AbstractTree, Boolean>> getConditionalTrees() {
        return conditionalTrees;
    }

    public void setConditionalTrees(List<Pair<AbstractTree, Boolean>> conditionalTrees) {
        this.conditionalTrees = conditionalTrees;
    }

    public List<AbstractNode> retrieveParameters() {
        List<AbstractNode> nodes = new ArrayList<>();
        traverseTree(
                this.getHead(),
                nodes,
                new NodeMutator() {
                    @Override
                    public Object mutate(Node node, Object value) {
                        AbstractNode absNode = (AbstractNode) node;
                        if (absNode.getType() == AbstractNode.AbstractNodeType.PARAMETER) {
                            List<AbstractNode> nodes = (List<AbstractNode>) value;
                            for (int i = 0; i < nodes.size(); i++) {
                                if (nodes.get(i).para_num
                                        == absNode.para_num) {
                                    return null;
                                }
                            }
                            nodes.add(absNode);
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

    public List<AbstractNode> getBufferRegionNodes() {
        List<AbstractNode> nodes = new ArrayList<>();

        traverseTree(this.getHead(), nodes, new NodeMutator() {
            @Override
            public Object mutate(Node node, Object value) {
                AbstractNode abstractNode = (AbstractNode) node;
                if (abstractNode.getType() == AbstractNode.AbstractNodeType.INPUT_NODE
                        || abstractNode.getType() == AbstractNode.AbstractNodeType.OUTPUT_NODE
                        || abstractNode.getType() == AbstractNode.AbstractNodeType.INTERMEDIATE_NODE) {
                    List<AbstractNode> nodes = (List<AbstractNode>) value;
                    for (int i = 1; i < nodes.size(); i++) { /* trick to get rid of the head node */
                        if(abstractNode.getAssociatedMem().equals(nodes.get(i).getAssociatedMem())){
                            return null;
                        }
                    }
                    nodes.add(abstractNode);
                }

                return null;
            }
        }, new NodeReturnMutator() {
            @Override
            public Object mutate(Object nodeValue, List<Object> traverseValue, Object value) {
                return null;//empty
            }
        });

        nodes.remove(0);/* remove head node */

        return nodes;
    }

    @Override
    public void simplifyTree() {

    }
}
