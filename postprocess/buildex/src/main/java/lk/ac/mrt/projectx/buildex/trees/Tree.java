package lk.ac.mrt.projectx.buildex.trees;

import lk.ac.mrt.projectx.buildex.MemoryRegion;
import lk.ac.mrt.projectx.buildex.X86Analysis;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static lk.ac.mrt.projectx.buildex.X86Analysis.Operation.op_mul;
import static lk.ac.mrt.projectx.buildex.trees.Operand.OperandType.IMM_INT_TYPE;

/**
 * Created by krv on 1/2/17.
 */
public abstract class Tree implements Comparable {

    final static Logger logger = LogManager.getLogger(Tree.class);

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

    //region protected methods

    public static Integer getNumParas() {
        return numParas;
    }

    //endregion protected methods

    //region public methods

    public static boolean areTreesSimilar(List<Tree> trees) {
        List<Node> nodes = new ArrayList<>();
        for (Tree tree : trees) {
            nodes.add(tree.getHead());
        }
        return areTreeNodesSimilar(nodes) == 1 ? true : false;
    }

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

    protected Object traverseTree(Object nde, Object value, NodeMutator nodeMutator, NodeReturnMutator nodeReturnMutator) {
        Node node = (Node) nde;
        Object nodeVal = nodeMutator.mutate(node, value);
        List<Object> traverseValue = new ArrayList<>();

        for (int i = 0 ; i < node.srcs.size() ; i++) {
            traverseValue.add(traverseTree(node.srcs.get(i), value, nodeMutator, nodeReturnMutator));
        }

        return nodeReturnMutator.mutate(nodeVal, traverseValue, value);
    }

//    public static void setNumParas(Integer  numParas) {
//        Tree.numParas = numParas;
//    }

    public Node getHead() {
        return head;
    }

    public void setHead(Node head) {
        this.head = head;
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

    public void cacocicalizeTree() {
        throw new NotImplementedException();
    }

    public void changeHeadNode() {
        throw new NotImplementedException();
    }

    public void numberTreeNodes() {
        logger.debug("Start tree numbering");
        traverseTree(head, this, new NodeMutator() {
            @Override
            public Object mutate(Node node, Object value) {
                Tree tr = (Tree) value;
                tr.numNodes = tr.numNodes + 1;
                if (node.order_num == -1) {
                    node.order_num = tr.numNodes;
                }
                return null;
            }
        }, new NodeReturnMutator() {
            @Override
            public Object mutate(Object nodeValue, List<Object> traverseValue, Object value) {
                return null;
            }
        });
        logger.debug("Number of trees %d", numNodes);
    }

    public void printTree(FileOutputStream file) {
        throw new NotImplementedException();
    }

    public void printDot(FileOutputStream file, String name, int number) {
        throw new NotImplementedException();
    }

    //region Tree Transformations

    public void cleanupVisit() {
        logger.debug("cleaning up all the visited states");
        traverseTree(head, numNodes, new NodeMutator() {
            @Override
            public Object mutate(Node node, Object value) {
                node.visited = false;
                return null;
            }
        }, new NodeReturnMutator() {
            @Override
            public Object mutate(Object nodeValue, List<Object> traverseValue, Object value) {
                return null;
            }
        });
    }

    public void removeAssignedNodes() {
        throw new NotImplementedException();
    }

    public List<MemoryRegion> identifyIntermediateBuffers(List<MemoryRegion> mem) {
        throw new NotImplementedException();
    }

    public void removeMultiplication() {
        cleanupVisit();
        removeMultiplication(head);
        cleanupVisit();
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

    //endregion Tree Transformations

    //endregion public methods

    //region private methods

    public void removeIdentities() {
        throw new NotImplementedException();
    }

    //TODO : move to util
    public boolean isRecursive(Node node, List<MemoryRegion> regions) {
        if (head != node) {
            if (node.symbol.type == Operand.OperandType.MEM_HEAP_TYPE ||
                    node.symbol.type == Operand.OperandType.MEM_STACK_TYPE) {
                if (MemoryRegionUtils.getMemRegion((Integer) head.symbol.value, regions) ==
                        MemoryRegionUtils.getMemRegion((Integer) node.symbol.value, regions)) {
                    return true;
                }
            }
        }

        boolean isRec = false;
        Iterator<Node> nodeIterator = head.srcs.listIterator();
        while (nodeIterator.hasNext()) {
            Node nde = nodeIterator.next();
            isRec = isRecursive(nde, regions);
            if (isRec) {
                break;
            }
        }

        return recursive;
    }

    private boolean removeMultiplication(Node node){
        boolean mul = false;
        if(node.visited) {
            return false;
        }else{
            mul = false;
            node.visited = true;
            if(node.operation ==  op_mul){
                Integer index = -1;
                Integer imm_value = 0;
                for (int i = 0 ; i < node.srcs.size() ; i++) {
                    if(((Node)node.srcs.get(i)).symbol.type == IMM_INT_TYPE){
                        imm_value = (Integer)((Node) node.srcs.get(i)).symbol.value;
                        index = i;
                        break;
                    }
                }

                if(index != -1 && imm_value >= 0){
                    mul = true;
                    for (int i = 0 ; i < node.prev.size() ; i++) {
                        Node nde = (Node)node.prev.get(i);
                        for (int j = 0 ; j < node.srcs.size() ; j++) {
                            if(index != j){
                                for (int k = 0 ; k < imm_value ; k++) {
                                    nde.addForwardRefrence((Node)node.srcs.get(j));
                                }
                            }
                        }
                        nde.removeForwardReference(node);
                    }

                    node.removeForwardReferenceAll();
                }
            }
        }

        for (int i = 0 ; i < node.srcs.size() ; i++) {
            Node src_node = (Node)node.srcs.get(i);
            if(removeMultiplication(src_node)){
                i = i - 1;
            }
        }

        return mul;
    }

    //endregion private methods

}
