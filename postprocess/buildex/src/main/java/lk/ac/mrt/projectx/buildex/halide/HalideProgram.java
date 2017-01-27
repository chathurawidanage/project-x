package lk.ac.mrt.projectx.buildex.halide;

import lk.ac.mrt.projectx.buildex.GeneralUtils;
import lk.ac.mrt.projectx.buildex.models.Pair;
import lk.ac.mrt.projectx.buildex.models.memoryinfo.MemoryRegion;
import lk.ac.mrt.projectx.buildex.trees.AbstractNode;
import lk.ac.mrt.projectx.buildex.trees.AbstractTree;
import lk.ac.mrt.projectx.buildex.trees.AbstractTreeCharacteristic;
import lk.ac.mrt.projectx.buildex.trees.Node;
import lk.ac.mrt.projectx.buildex.x86.X86Analysis;
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

    private List<Function> funcs;
    private List<String> vars;
    private List<String> rvars;//reduction variables
    private Map<Integer, Integer> parameterMatch;
    private List<AbstractNode> params;

    private List<AbstractNode> output;/* this is used to populate the arguments string */
    private List<AbstractNode> inputs;

    public HalideProgram() {
        this.funcs = new ArrayList<>();
        this.vars = new ArrayList<>();
        this.rvars = new ArrayList<>();
        this.parameterMatch = new HashMap<>();
        this.params = new ArrayList<>();//not necessary, just in case
        this.output = new ArrayList<>();
        this.inputs = new ArrayList<>();
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

    private void appendHalideParameterDeclarations() {
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

    private void appendHalideFunctionDeclarations() {
        for (Function function : funcs) {
            AbstractNode abstractNode = (AbstractNode) function.getPureTrees().get(0).getHead();
            appendNewLine(String.format("Func %s", abstractNode.getAssociatedMem().getName()));
        }
    }

    private void appendHalideArguments(String vectorName) {
        StringBuilder arg = new StringBuilder(vectorName);
        appendNewLine(String.format("vector<Argument> %s", arg));
        for (AbstractNode param : params) {
            appendNewLine(String.format("%s.push_back(p_%s)", arg, param.para_num));
        }

        for (AbstractNode input : inputs) {
            appendNewLine(String.format("%s.push_back(p_%s)", input.getAssociatedMem().getName()));
        }
    }

    private void appendHalideOutputToFile(String argsVectorName) {
        int outIndex = 0;
        for (AbstractNode out : output) {
            appendNewLine(String.format("%s.compile_to_file(\"%s_%d\",%s)",
                    out.getAssociatedMem().getName(), "halide_out", outIndex++, argsVectorName));
        }
    }

    private String getExpressionName(String expressionName, int condiotional) {
        return expressionName + "_" + condiotional;
    }

    private String getFullOverlapNode(AbstractNode node, Node head, List<String> vars) {
        /* here, we will some times we need to use shifting and anding */
        AbstractNode overlap = (AbstractNode) node.getSrcs().get(0);

        long overlapEnd = overlap.getSymbol().getValue().longValue() +
                overlap.getSymbol().getWidth();
        long nodeEnd = node.getSymbol().getValue().longValue() + node.getSymbol().getWidth();

	/* BUG - overlapEnd == nodeEnd ? this is not always true if mem and reg values are*/


        StringBuilder ret = new StringBuilder("");

        if (node.getSymbol().getWidth() == overlap.getSymbol().getWidth()) { /* where the nodes are of reg and memory etc.*/
            ret.append(getAbstractTree(overlap, head, vars));
            return ret.toString();
        }

        long mask = ~0 >> (32 - node.getSymbol().getWidth() * 8);
        mask = mask > 65535 ? 65535 : mask;

        ret.append(" ( ");
        ret.append(getAbstractTree(overlap, head, vars));
        ret.append(" ) & " + mask);

        return ret.toString();
    }

    private String getPartialOverlapNode(AbstractNode node, Node head, List<String> vars) {
        return "";//todo
    }

    private String getIndirectString(AbstractNode node, Node head, List<String> vars) {
        return getAbstractTree(node, head, vars);
    }


    private String getAbstractTree(Node nnode, Node head, List<String> vars) {
        AbstractNode node = (AbstractNode) nnode;

        StringBuilder ret = new StringBuilder();

        if (node.minus) {
            ret.append("- (");
        }

        if (node.getType() == AbstractNode.AbstractNodeType.OPERATION_ONLY) {
            if (node.getOperation() == X86Analysis.Operation.op_full_overlap) {
                ret.append(" ( ");
                ret.append(getFullOverlapNode(node, head, vars));
                ret.append(" ) ");
            } else if (node.getOperation() == X86Analysis.Operation.op_partial_overlap) {
                ret.append(" ( ");
                ret.append(getPartialOverlapNode(node, head, vars));
                ret.append(" ) ");
            } else if (node.getOperation() == X86Analysis.Operation.op_split_h) {
                ret.append(" ( ");
                ret.append(getAbstractTree(node.getSrcs().get(0), head, vars));
                ret.append(" ) >> ( " + (node.getSrcs().get(0).getSymbol().getWidth() * 8 / 2) + ")");
            } else if (node.getOperation() == X86Analysis.Operation.op_split_l) {
                ret.append(" ( ");
                ret.append(getAbstractTree(node.srcs.get(0), head, vars));
                ret.append(" ) & " + ((node.getSrcs().get(0).getSymbol().getWidth() / 2) * 8));
            } else if (node.getOperation() == X86Analysis.Operation.op_indirect) {
                ret.append("(");
                ret.append(getIndirectString(node, head, vars));
                ret.append(")");
            } else if (node.getOperation() == X86Analysis.Operation.op_call) {
                ret.append("(");
                ret.append(node.functionName + "(");
                for (int k = 0; k < node.getSrcs().size(); k++) {
                    AbstractNode abstractNode = (AbstractNode) node.getSrcs().get(k);
                    ret.append(getAbstractTree(abstractNode, head, vars));
                    if (k != node.getSrcs().size() - 1) {
                        ret.append(",");
                    }
                }
                ret.append(")");
            } else if (node.getSrcs().size() == 1) {
                ret.append(" " + node.getSymbolicString(vars) + " ");
                ret.append(getAbstractTree(node.getSrcs().get(0), head, vars));
            } else {
                ret.append("(");
                for (int i = 0; i < node.getSrcs().size(); i++) {
                    if (node.getSrcs().get(i).getSymbol().getWidth() != node.getSymbol().getWidth()) {
                        ret.append(getCastString(node, node.getSrcs().get(0).minus) + "(");
                    }
                    ret.append(getAbstractTree(node.getSrcs().get(i), head, vars));
                    if (node.getSrcs().get(i).getSymbol().getWidth() != node.getSymbol().getWidth()) {
                        ret.append(")");
                    }
                    if (i != node.getSrcs().size() - 1) {
                        ret.append(" " + node.getSymbolicString(vars) + " ");
                    }
                }
                ret.append(")");
            }
        } else if (node.getType() == AbstractNode.AbstractNodeType.SUBTREE_BOUNDARY) {

        } else {
            int pos = node.isIndirect();
            boolean indirect = (pos != -1);

            if (node != head) {
                if (indirect) {
                    ret.append(node.getAssociatedMem().getName());
                    ret.append(getAbstractTree(node.getSrcs().get(pos), head, vars)); /* assumes that these nodes are at the leaves */
                } else {
                    if (node.getType() == AbstractNode.AbstractNodeType.PARAMETER) {
                        ret.append("p_" + parameterMatch.get(node.para_num) + " ");
                    } else {
                        ret.append(node.getSymbolicString(vars) + " ");
                    }
                }
            } else {
                Node indirectNode = null;
                if (indirect) {
                    indirectNode = node.getSrcs().get(pos);
                    node.getSrcs().remove(pos);
                }

                if (node.getOperation() != op_assign) {  /* the node contains some other operation */
                    AbstractNode.AbstractNodeType originalType = node.getType();
                    node.setType(AbstractNode.AbstractNodeType.OPERATION_ONLY);
                    ret.append(getAbstractTree(node, head, vars));
                    node.setType(originalType);
                } else {
                    ret.append(getAbstractTree(node.getSrcs().get(0), head, vars));
                }

                if (indirect) {
                    node.getSrcs().add(pos, indirectNode);
                }
            }
        }

        if (node.minus) {
            ret.append(")");
        }

        return ret.toString();
    }

    private String getConditionalTrees(List<Pair<AbstractTree, Boolean>> conditions, List<String> vars) {
        StringBuilder ret = new StringBuilder();

        for (int i = 0; i < conditions.size(); i++) {
            AbstractNode node = (AbstractNode) conditions.get(i).first.getHead();
        /* because the head node is just the output node - verify this fact */
            GeneralUtils.assertAndFail(node.srcs.size() == 1, "ERROR: expected single source");

            boolean taken = conditions.get(i).second;

            if (!taken) {
                ret.append("! (");
            }

            ret.append(getAbstractTree(node.srcs.get(0), node, vars));
            if (i != conditions.size() - 1) {
                ret.append(" && ");
            }

            if (!taken) {
                ret.append(")");
            }
        }

        if (ret.toString().isEmpty()) {
            ret.append("true");
        }
        return ret.toString();
    }

    private void appendSelectStatement(Expression current, Expression next) {
        if (next != null) { /* we have a false value*/
            appendNewLine("Expr " + current.getName() + " = select(" + current.getCondition() + "," + current.getTruthValue() + "," + next.getName() + ")");
        } else {
            appendNewLine("Expr " + current.getName() + " = " + current.getTruthValue());
        }
    }

    private AbstractNode getIndirectNode(AbstractNode abstractNode) {
        return (AbstractNode) abstractNode.getSrcs().get(0);
    }

    private String getOutputFunctionDefinition(AbstractNode head) {
        MemoryRegion mem = head.getAssociatedMem();

        int pos = head.isIndirect();
        boolean indirect = (pos != -1);

        StringBuilder ret = new StringBuilder(mem.getName() + "(");

	/* assume only one level of indirection */
        if (indirect) {
            AbstractNode indirectNode = getIndirectNode((AbstractNode) head.getSrcs().get(pos));
            ret.append(indirectNode.getAssociatedMem().getName());
            ret.append("(");
            head = indirectNode;
        }

        for (int i = 0; i < head.getDimensions(); i++) {
            ret.append(vars.get(i));
            if (i == head.getDimensions() - 1) {
                ret.append(")");
            } else {
                ret.append(",");
            }
        }

        if (indirect) {
            ret.append(")");
        }

        return ret.toString();
    }

    private String getCastString(AbstractNode abstractNode, boolean sign) {
        StringBuilder ret = new StringBuilder();
        ret.append("cast<");

        if (abstractNode.is_double) {
            ret.append("double>");
        } else {
            if (!sign) ret.append("u");
            ret.append("int" + abstractNode.symbol.getWidth() * 8 + "_t>");
        }

        return ret.toString();
    }

    private void appendPredictedTree(List<AbstractTree> trees, String exprTag, List<String> vars) {
        //appending predicted tree

        List<Expression> exprs = new ArrayList<>();
        /* populate the expressions */
        for (int i = 0; i < trees.size(); i++) {
            AbstractNode head = (AbstractNode) trees.get(i).getHead();
            Expression expr = new Expression();
            expr.setName(getExpressionName(head.getAssociatedMem().getName() + exprTag, i));
            expr.setCondition(getConditionalTrees(trees.get(i).getConditionalTrees(), vars));
            expr.setTruthValue(getAbstractTree(trees.get(i).getHead(), trees.get(i).getHead(), vars));
            exprs.add(expr);
        }

	/* final print statements */
        for (int i = 0; i < exprs.size() - 1; i++) {
            appendSelectStatement(exprs.get(i), exprs.get(i + 1));
        }

        appendSelectStatement(exprs.get(exprs.size() - 1), null);


        StringBuilder output = new StringBuilder();

	/* finally update the final output location */
        output.append(getOutputFunctionDefinition((AbstractNode) trees.get(0).getHead()));

        AbstractNode head_node = (AbstractNode) trees.get(0).getHead();

        long clamp_max = Math.max(32 - head_node.symbol.getWidth() * 8, 65535);
        long clamp_min = 0;

	/* BUG - what to do with the sign?? */
        output.append(" = ");
        output.append(getCastString(head_node, false));
        output.append("( clamp(");
        output.append(exprs.get(0).getName());
        output.append(clamp_min + "," + clamp_max);
        output.append(") ))");

        appendNewLine(output.toString());
    }

    private void appendPureTrees(Function function) {
        appendPredictedTree(function.getPureTrees(), "_p_", vars);
    }

    private void appendRDom(RDom rDom, List<String> variables) {
        String name = rvars.get(rvars.size() - 1);
        StringBuilder ret = new StringBuilder("RDom " + name + "(");
        if (rDom.getrDomType() == RDomType.INDIRECT_REF) {
            ret.append(rDom.getRedNode().getAssociatedMem().getName());
        } else {
            for (int i = 0; i < rDom.getAbstractIndexes().size(); i++) {
                for (int j = 0; j < rDom.getAbstractIndexes().get(i).size(); j++) {
                    ret.append(rDom.getAbstractIndexes().get(i).get(j) + " * " + variables.get(j));
                    if (j != rDom.getAbstractIndexes().get(i).size() - 1) {
                        ret.append(" + ");
                    }
                }
                if (i != rDom.getAbstractIndexes().size()) {
                    ret.append(" , ");
                }
            }
        }
        ret.append(" )");
        appendNewLine(ret.toString());
    }

    private List<String> getReductionIndexVariables(String rvar) {
        List<String> rvars = new ArrayList<>();
        for (String suff : new String[]{"x", "y", "z", "w"}) {
            rvars.add(rvar + "." + suff);
        }
        return rvars;
    }

    private void appendReductionTrees(Function function, List<String> reductionVariables) {

	/* Assumption - If the RDom is the same, then the trees are different due to conditionals.
    If the RDom's are not the same, then those trees are computed without overlap */
        GeneralUtils.assertAndFail(function.getPureTrees().size() > 0, "Reduction updates should have initial pure definitions");

        if (function.getReductionTrees().isEmpty()) return;


        for (int i = 0; i < function.getReductionTrees().size(); i++) {
            String name = "r_" + rvars.size();
            rvars.add(name);
            appendRDom(function.getReductionTrees().get(i).first, reductionVariables);
            appendPredictedTree(function.getReductionTrees().get(i).second, "_r" + i + "_", getReductionIndexVariables(name));
        }
    }


    private void appendFunctions(List<String> reductionVariables) {
        for (Function function : funcs) {
            appendPureTrees(function);
            appendReductionTrees(function, reductionVariables);
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

    private void populatePureFunction(AbstractTree abstractTree) {
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

    private void populateReductionFunctions(AbstractTree tree, List<Pair<Long, Long>> boundaries, AbstractNode node) {
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

    private void resolveConditionals() {
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

    private void populateVars(int dim) {
        String x = "x";
        for (int i = 0; i < dim; i++) {
            vars.add(x + "_" + i);
        }
    }

    private void populateInputParams(boolean parameters) {
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

    private String getFinalizedProgram(List<String> reductionVariables) {
        logger.debug("Finalizing halide program");

        Iterator<Integer> paramMatchKeysIterator = parameterMatch.keySet().iterator();

        while (paramMatchKeysIterator.hasNext()) {
            Integer next = paramMatchKeysIterator.next();
            halideProgramStr.append(next + " " + parameterMatch.get(next));
        }

        appendHalideHeader();

        /****************** print declarations **********************/

        appendHalideVariableDeclarations();

        appendHalideInputDeclarations();

        appendHalideParameterDeclarations();

        sortFunctions();
        appendHalideFunctionDeclarations();

        /***************** print the functions ************************/

        appendFunctions(reductionVariables);

        /***************finalizing - instructions for code generation ******/

	/* print argument population - params and input params */
        String argumentsVector = "arguments";
        appendHalideArguments(argumentsVector);

        appendHalideOutputToFile(argumentsVector);

        appendNewLine("return 0");
        appendNewLine("}", false);
        return halideProgramStr.toString();
    }

    public String generateHalide(AbstractTree finalAbstractTree, List<AbstractTreeCharacteristic> absTrees, List<String> reductionVariables) {
        if (absTrees.isEmpty()) {
            this.populatePureFunction(finalAbstractTree);
        } else {
            for (int i = 0; i < absTrees.size(); i++) {
                if (absTrees.get(i).isRecusrsive()) {
                    logger.debug("reduction func populated");
                    this.populateReductionFunctions(absTrees.get(i).getAbstractTree(),
                            absTrees.get(i).getExtents(), absTrees.get(i).getRedNode());
                } else {
                    logger.debug("pure func populated");
                    this.populatePureFunction(absTrees.get(i).getAbstractTree());
                }
            }
        }
        this.resolveConditionals();
        this.populateVars(4);
        this.populateInputParams(false);
        this.populateInputParams(true);
        return this.getFinalizedProgram(reductionVariables);
    }
}
