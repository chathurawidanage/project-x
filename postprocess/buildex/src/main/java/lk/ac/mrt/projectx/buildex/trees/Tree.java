package lk.ac.mrt.projectx.buildex.trees;

import lk.ac.mrt.projectx.buildex.MemoryRegion;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by krv on 1/2/17.
 */
public abstract class Tree implements Comparable {

    //region private variables

    private static Integer numParas;
    private boolean recursive;
    private boolean dummyTree;
    private Integer numNodes;
    private Integer treeNum;
    private Node head;

    //endregion private variables

    //region public constructors

    public Tree() {
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

    public static Integer getNumParas() {
        return numParas;
    }

//    public static void setNumParas(Integer numParas) {
//        Tree.numParas = numParas;
//    }

    public boolean isRecursive() {
        return recursive;
    }

    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }

    public boolean isDummyTree() {
        return dummyTree;
    }

    public void setDummyTree(boolean dummyTree) {
        this.dummyTree = dummyTree;
    }

    public Integer getNumNodes() {
        return numNodes;
    }

    public void setNumNodes(Integer numNodes) {
        this.numNodes = numNodes;
    }

    public Integer getTreeNum() {
        return treeNum;
    }

    public void setTreeNum(Integer treeNum) {
        this.treeNum = treeNum;
    }

    @Override
    public int compareTo(Object o) {
        Tree tree = (Tree) o;
        List<Node> nodes = new ArrayList<>();
        nodes.add(head);
        nodes.add(tree.getHead());
        return areTreeNodesSimilar(nodes);
    }

    public abstract void simplifyTree();

    public static boolean areTreesSimilar(List<Tree> trees) {
        List<Node> nodes = new ArrayList<>();
        for (Tree tree : trees) {
            nodes.add(tree.getHead());
        }
        return areTreeNodesSimilar(nodes) == 1 ? true : false;
    }

    public void cacocicalizeTree() {
        throw new NotImplementedException();
    }

    public void changeHeadNode() {
        throw new NotImplementedException();
    }

    public void numberTreeNodes() {
        throw new NotImplementedException();
    }

    public void printTree(FileOutputStream file) {
        throw new NotImplementedException();
    }

    public void printDot(FileOutputStream file, String name, int number) {
        throw new NotImplementedException();
    }

    public boolean isRecursive(Node node, List<MemoryRegion> regions) {
        throw new NotImplementedException();
    }

    public void cleanupVisit() {
        throw new NotImplementedException();
    }

    //region Tree Transformations

    public void removeAssignedNodes() {
        throw new NotImplementedException();
    }

    public List<MemoryRegion> identifyIntermediateBuffers(List<MemoryRegion> mem) {
        throw new NotImplementedException();
    }

    public void removeMultiplication() {
        throw new NotImplementedException();
    }

    public void simplifyImmediates() {
        throw new NotImplementedException();
    }

    public void remoevMinusNodes() {
        throw new NotImplementedException();
    }

    public void removeRedundantNodes() {
        throw new NotImplementedException();
    }

    public void convertSubNodes() {
        throw new NotImplementedException();
    }

    public void simplifyMinus() {
        throw new NotImplementedException();
    }

    public void verifyMinus() {
        throw new NotImplementedException();
    }

    public void removePoNodes() {
        throw new NotImplementedException();
    }

    public void removeOrMinus1() {
        throw new NotImplementedException();
    }

    public void markRecursive() {
        throw new NotImplementedException();
    }

    public void removeIdentities() {
        throw new NotImplementedException();
    }

    //endregion Tree Transformations

    //endregion public methods

    //region private methods

    /**
     * No idea what this do
     *
     * @param nodes
     * @return
     */
    private static int areTreeNodesSimilar(List<Node> nodes) {
        if (!Node.isNodesSimilar(nodes)) return 0;

        if (!nodes.isEmpty()) {
            for (int i = 0 ; i < nodes.get(0).srcs.size() ; i++) {
                List<Node> nodesList = new ArrayList<>();
                for (int j = 0 ; j < nodes.size() ; j++) {
                    //TODO : find the reason for needing to cast to Node
                    nodesList.add((Node) nodes.get(j).srcs.get(i));
                }
                if (areTreeNodesSimilar(nodesList) != 1) return 0;
            }
        }

        return 1;
    }
    //endregion private methods

}
