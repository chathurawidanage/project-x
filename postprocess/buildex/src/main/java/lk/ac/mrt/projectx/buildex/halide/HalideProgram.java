package lk.ac.mrt.projectx.buildex.halide;

import lk.ac.mrt.projectx.buildex.GeneralUtils;
import lk.ac.mrt.projectx.buildex.models.Pair;
import lk.ac.mrt.projectx.buildex.models.memoryinfo.MemoryRegion;
import lk.ac.mrt.projectx.buildex.trees.AbstractNode;
import lk.ac.mrt.projectx.buildex.trees.AbstractTree;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

import static lk.ac.mrt.projectx.buildex.models.memoryinfo.MemDirection.MEM_INPUT;
import static lk.ac.mrt.projectx.buildex.models.memoryinfo.MemDirection.MEM_OUTPUT;
import static lk.ac.mrt.projectx.buildex.x86.X86Analysis.Operation.op_assign;

/**
 * @author Chathura Widanage
 */
public class HalideProgram {
    private final Logger logger = LogManager.getLogger(HalideProgram.class);

    private StringBuilder halideProgramStr = new StringBuilder();
    private AbstractTree abstractTree;

    private List<Function> funcs;
    private List<String> vars;
    private Map<Integer, Integer> parameterMatch;
    private List<AbstractNode> params;

    private List<AbstractNode> output;/* this is used to populate the arguments string */
    private List<AbstractNode> inputs;

    public HalideProgram(AbstractTree abstractTree) {
        this.funcs = new ArrayList<>();
        this.vars = new ArrayList<>();
        this.parameterMatch = new HashMap<>();
        this.params = new ArrayList<>();//not necessary, just in case
        this.output = new ArrayList<>();
        this.inputs = new ArrayList<>();

        this.abstractTree = abstractTree;
    }

    /*APPENDERS*/
    private void appendNewLine(String line, boolean semicolon) {
        halideProgramStr.append(line + (semicolon ? ";" : ""));
        halideProgramStr.append("\n");
    }

    private void appendNewLine(String line) {
        appendNewLine(line, true);
    }

    private void appendHalideHeader() {
        appendNewLine("#include <Halide.h>", false);
        appendNewLine("#include <vector>", false);
        appendNewLine("using namespace std");
        appendNewLine("using namespace Halide");
        appendNewLine("int main(){", false);
    }

    private void appendHalideVariableDeclarations() {
        for (String var : vars) {
            appendNewLine("Var " + var);
        }
    }

    private String getHalideDataType(int width, boolean sign, boolean isFloat) {
        StringBuilder signString = new StringBuilder(sign ? "" : "U");

        if (!isFloat)
            return signString.append("Int(" + (width * 8) + ")").toString();
        else
            return "Float";
    }

    private void appendHalideInputDeclarations() {
        for (AbstractNode node : inputs) {
            if (node.getAssociatedMem().getMemDirection() != MEM_INPUT) {
                continue;
            }

            String inString = node.getAssociatedMem().getMemDirection() == MEM_OUTPUT ? "_buf_in" : "";//todo check, never true

            appendNewLine(String.format("ImageParam %s(%s,%s)",
                    node.getAssociatedMem().getName() + inString,
                    getHalideDataType(node.symbol.getWidth(), node.sign, node.is_double),
                    node.getDimensions().toString()
            ));
        }
    }

    private void appendHalideParamaterDeclarations() {
        for (AbstractNode param : params) {
            GeneralUtils.assertAndFail(param.getType() == AbstractNode.AbstractNodeType.PARAMETER, "ERROR: the node is not a parameter");

            StringBuilder ret = new StringBuilder("Param<");

            if (param.is_double) {
                ret.append("double");
            } else {
                if (!param.sign) ret.append("u");
                ret.append("int" + (param.symbol.getWidth() * 8) + "_t");
            }
            ret.append("> ");
            ret.append(String.format("p_%d(\"p_%d\")", param.para_num, param.para_num));
            appendNewLine(ret.toString());
        }
    }
    /*END OF APPENDERS*/

    /*Function Sorters*/
    private void sortFunctions(List<AbstractTree> abstractTrees) {
        Collections.sort(abstractTrees, new Comparator<AbstractTree>() {
            @Override
            public int compare(AbstractTree o1, AbstractTree o2) {
                return o1.getConditionalTrees().size() - o2.getConditionalTrees().size();
            }
        });
    }

    private void sortFunctions() {
        for (int i = 0; i < funcs.size(); i++) {
            sortFunctions(funcs.get(i).getPureTrees());
            for (int j = 0; j < funcs.get(i).getReductionTrees().size(); j++) {
                sortFunctions(funcs.get(i).getReductionTrees().get(j).second);
            }
        }
    }
    /*End of function sorters*/

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

    public void populateReductionFunctions(AbstractTree tree, List<Pair<Long, Long>> boundaries, AbstractNode node) {
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
                    assignNode.minus = false;
                    newTree.setHead(newHead);
                    newHead.addForwardReference(assignNode);
                    funcs.get(i).getPureTrees().add(newTree);
                }
            }
        }
    }

    public void populateVars(int dim) {
        String x = "x";
        for (int i = 0; i < dim; i++) {
            vars.add(x + "_" + i);
        }
    }

    public void populateInputParams(boolean parameters) {
        List<AbstractTree> trees = new ArrayList<>();
        for (int i = 0; i < funcs.size(); i++) {
            trees.addAll(funcs.get(i).getPureTrees());
            for (int j = 0; j < funcs.get(i).getReductionTrees().size(); j++) {
                trees.addAll(funcs.get(i).getReductionTrees().get(j).second);
            }
        }


	/*get the conditional trees as well*/
        int size = trees.size();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < trees.get(i).getConditionalTrees().size(); j++) {
                trees.add(trees.get(i).getConditionalTrees().get(j).first);
            }
        }

        List<AbstractNode> total = new ArrayList<>();

        for (int i = 0; i < trees.size(); i++) {
            List<AbstractNode> temp;
            temp = parameters ? trees.get(i).retrieveParameters() : trees.get(i).getBufferRegionNodes();
            total.addAll(temp);
        }

        if (parameters) {
        /* paras we can have duplicates */
            for (int i = 0; i < total.size(); i++) {
                boolean found = false;
                AbstractNode abstractNodei = total.get(i);
                for (int j = 0; j < i; j++) {
                    AbstractNode abstractNodej = total.get(j);
                    if (abstractNodei.symbol.getType() == abstractNodej.symbol.getType()
                            && abstractNodei.symbol.getValue().equals(abstractNodej.symbol.getValue())
                            && abstractNodei.symbol.getWidth() == abstractNodej.symbol.getWidth()
                            ) {
                        this.parameterMatch.put(abstractNodei.para_num, abstractNodej.para_num);

                        //todo check if remove problems occur
                        total.remove(i--);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    parameterMatch.put(total.get(i).para_num, total.get(i).para_num);
                }
            }
            params = total;
        } else {
            for (int i = 0; i < total.size(); i++) {
                for (int j = 0; j < total.size(); j++) {
                    if (i == j) continue;
                    if (total.get(i).getAssociatedMem().equals(total.get(j).getAssociatedMem())) {
                        total.remove(i--);
                        break;
                    }
                }
            }

		/* now first populate the outputs */
            for (int i = 0; i < funcs.size(); i++) {
                AbstractNode outputNode = (AbstractNode) funcs.get(i).getPureTrees().get(0).getHead();
                output.add(outputNode);
                MemoryRegion head_region = outputNode.getAssociatedMem();
                long direction = head_region.getTreeDirections();
                direction |= MEM_OUTPUT.getValue();
                head_region.setTreeDirections(direction);
            }

            for (int i = 0; i < total.size(); i++) {
                AbstractNode abstractNodei = total.get(i);
                long direction = abstractNodei.getAssociatedMem().getTreeDirections();
                direction |= MEM_INPUT.getValue();
                abstractNodei.getAssociatedMem().setTreeDirections(direction);
                for (int j = 0; j < output.size(); j++) {
                    if (abstractNodei.getAssociatedMem() == output.get(j).getAssociatedMem()) {
                        if (abstractNodei.isIndirect() != -1) {
                            direction &= ~(MEM_INPUT.getValue());
                            abstractNodei.getAssociatedMem().setTreeDirections(direction);
                        }
                        break;
                    }
                }
            }
            inputs = total;
        }
    }

    public String getFinalizedProgram(List<String> reductionVariables) {
        logger.debug("Finalizing halide program");

        Iterator<Integer> paramMatchKeysIterator = parameterMatch.keySet().iterator();

        while (paramMatchKeysIterator.hasNext()) {
            Integer next = paramMatchKeysIterator.next();
            halideProgramStr.append(next + " " + parameterMatch.get(next));
        }

        appendHalideHeader();

        /****************** print declarations **********************/
    /* print Vars */
        appendHalideVariableDeclarations();

	/* print InputParams */
        appendHalideInputDeclarations();

	/* print Params */
        appendHalideParamaterDeclarations();

        sortFunctions();


        halideProgramStr.append(
                "return 0;\n}"
        );
        return halideProgramStr.toString();
    }
}
