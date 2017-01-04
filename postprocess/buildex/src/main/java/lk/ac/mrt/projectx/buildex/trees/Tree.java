package lk.ac.mrt.projectx.buildex.trees;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by krv on 1/2/17.
 */
public abstract class Tree implements Comparable{

    //region private variables

    private static Integer numParas;
    private boolean recursive;
    private boolean dummyTree;
    private Integer numNodes;
    private Integer treeNum;
    private Node head;

    //endregion private variables

    //region public constructors

    public Tree(){
        head = null;
        numNodes = 0;
        treeNum = -1;
        recursive = false;
    }

    //endregion public constructors

    //region public methods

    public Node getHead() {
        return head;
    }

    public void setHead(Node head) {
        this.head = head;
    }

    @Override
    public int compareTo(Object o) {
        Tree tree = (Tree) o;
        return 0;
    }

    public abstract void simplifyTree();

    //endregion public methods

    //region private methods

    private boolean areTreesSimilar(List<Tree> trees){
        List<Node> nodes = new ArrayList<>();
        for (Tree tree:trees) {
            nodes.add(tree.getHead());
        }
        return areTreeNodesSimilar(nodes);
    }

    /**
     *  No idea what this do
     * @param nodes
     * @return
     */
    private boolean areTreeNodesSimilar(List<Node> nodes){
        if(!Node.isNodesSimilar(nodes)) return false;

        if(!nodes.isEmpty()){
            for (int i = 0 ; i < nodes.get(0).srcs.size() ; i++) {
                List<Node> nodesList = new ArrayList<>();
                for (int j = 0 ; j < nodes.size() ; j++) {
                    //TODO : find the reason for needing to cast to Node
                    nodesList.add((Node)nodes.get(j).srcs.get(i));
                }
                if(!areTreeNodesSimilar(nodesList)) return false;
            }
        }

        return true;
    }
    //endregion private methods

}
