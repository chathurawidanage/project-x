package lk.ac.mrt.projectx.buildex.halide;

import lk.ac.mrt.projectx.buildex.GeneralUtils;
import lk.ac.mrt.projectx.buildex.models.Pair;
import lk.ac.mrt.projectx.buildex.models.memoryinfo.MemoryRegion;
import lk.ac.mrt.projectx.buildex.trees.AbstractNode;
import lk.ac.mrt.projectx.buildex.trees.AbstractTree;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static lk.ac.mrt.projectx.buildex.x86.X86Analysis.Operation.op_assign;

/**
 * @author Chathura Widanage
 */
public class HalideProgram {
    private final Logger logger = LogManager.getLogger(HalideProgram.class);

    private StringBuilder halideProgramStr = new StringBuilder();
    private AbstractTree abstractTree;

    private List<Function> funcs;

    public HalideProgram(AbstractTree abstractTree) {
        this.funcs = new ArrayList<>();

        this.abstractTree = abstractTree;
        halideProgramStr.append(
                "#include <Halide.h>\n" +
                        "#include <vector>\n " +
                        "using namespace std;\n" +
                        "using namespace Halide;\n " +
                        "int main(){ \n"
        );
    }

    private Function checkFunction(MemoryRegion memoryRegion) {
        for (int i = 0; i < funcs.size(); i++) {
            Function function = funcs.get(i);
            if (!function.getPureTrees().isEmpty()) {
                AbstractNode abstractNode = (AbstractNode) function.getPureTrees().get(0).getHead();
                if (abstractNode.getAssociatedMem().equals(memoryRegion)) {
                    return function;
                }
            }

            for (int j = 0; j < function.getReductionTrees().size(); j++) {
                List<AbstractTree> trees = function.getReductionTrees().get(j).second;
                for (int k = 0; k < trees.size(); k++) {
                    AbstractNode node = (AbstractNode) trees.get(k).getHead();
                    if (node.getAssociatedMem().equals(memoryRegion)) {
                        return function;
                    }
                }
            }
        }
        return null;
    }

    public void pupulatePureFunction(AbstractTree abstractTree) {
        AbstractNode abstractNode = (AbstractNode) abstractTree.getHead();

        MemoryRegion associatedMemRegion = abstractNode.getAssociatedMem();
        Function function = this.checkFunction(associatedMemRegion);
        if (function == null) {
            function = new Function();
            funcs.add(function);
        }
        function.getPureTrees().add(abstractTree);
    }

    private long getRdomLocation(Function function, RDom rDom) {
        for (int i = 0; i < function.getReductionTrees().size(); i++) {
            RDom currentRDom = function.getReductionTrees().get(i).first;
            if (rDom.getrDomType() == currentRDom.getrDomType()) {
                if (rDom.getrDomType() == RDomType.INDIRECT_REF) {
                    if (rDom.getRedNode().getAssociatedMem().
                            equals(currentRDom.getRedNode().getAssociatedMem())) {
                        return i;
                    }
                } else {
                    List<Pair<Long, Long>> first = currentRDom.getExtents();
                    List<Pair<Long, Long>> second = rDom.getExtents();

                    GeneralUtils.assertAndFail(first.size() == second.size(),
                            "reduction domain dimensions for the same buffer should be the same");


                    boolean similar = true;
                    for (int j = 0; j < first.size(); j++) {
                        if (first.get(i).first != second.get(i).first
                                || first.get(i).second != second.get(i).second) {
                            similar = false;
                            break;
                        }
                    }

                    if (similar) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    public void populateRedFunctions(AbstractTree tree, List<Pair<Long, Long>> boundaries, AbstractNode node) {
        RDom rdom = new RDom();
        if (node != null) {
            rdom.setRedNode(node);
            rdom.setrDomType(RDomType.INDIRECT_REF);
            logger.debug("Indirect reference populated");
        } else {
            rdom.setExtents(boundaries);
            rdom.setrDomType(RDomType.EXTENTS);
            logger.debug("Extents populated");
        }

        AbstractNode head = (AbstractNode) tree.getHead();
        Function func = checkFunction(head.getAssociatedMem());
        if (func == null) {
            func = new Function();
            funcs.add(func);
        }

        long loc = getRdomLocation(func, rdom);
        if (loc != -1) {
            func.getReductionTrees().get((int) loc).second.add(tree);
        } else {
            List<AbstractTree> abstractTreeList = new ArrayList<>();
            abstractTreeList.add(tree);
            func.getReductionTrees().add(new Pair<>(rdom, abstractTreeList));
        }
    }

    public void resolveConditionals() {
        /* if there is no else; if assume it is coming from outside */

	/* todo need to handle all cases */

        for (int i = 0; i < funcs.size(); i++) {

		/* check whether there is no statement with no conditionals */
            if (funcs.get(i).getPureTrees().size() == 1) {
                if (!funcs.get(i).getPureTrees().get(0).getConditionalTrees().isEmpty()) {
                /* add a new tree output = output values coming from outside */
                    AbstractTree abstractTree = funcs.get(i).getPureTrees().get(0);
                    AbstractTree newTree = new AbstractTree();

                    AbstractNode newHead = (AbstractNode) GeneralUtils.deepCopy(abstractTree.getHead());
                    newHead.operation = op_assign;
                    newHead.minus = false;

                    AbstractNode assignNode = (AbstractNode) GeneralUtils.deepCopy(abstractTree.getHead());
                    for (int j = 0; j < assignNode.getDimensions(); j++) {
                        for (int k = 0; k < assignNode.getHeadDiemensions() + 1; k++) {
                            if (j == k) assignNode.getIndexes().get(i).set(k, 1);// mem_info.indexes[j][k] = 1;
                            else assignNode.getIndexes().get(i).set(k, 0);
                        }
                    }
                    assignNode.minus=false;
                    newTree.setHead(newHead);
                    newHead.addForwardReference(assignNode);
                    funcs.get(i).getPureTrees().add(newTree);
                }
            }
        }
    }

    public String getFinalizedProgram() {
        halideProgramStr.append(
                "return 0;\n}"
        );
        return halideProgramStr.toString();
    }
}
