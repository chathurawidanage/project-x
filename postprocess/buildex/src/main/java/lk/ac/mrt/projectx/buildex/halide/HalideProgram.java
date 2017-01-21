package lk.ac.mrt.projectx.buildex.halide;

import lk.ac.mrt.projectx.buildex.models.memoryinfo.MemoryRegion;
import lk.ac.mrt.projectx.buildex.trees.AbstractNode;
import lk.ac.mrt.projectx.buildex.trees.AbstractTree;
import lk.ac.mrt.projectx.buildex.trees.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chathura Widanage
 */
public class HalideProgram {
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

    public String getFinalizedProgram() {
        halideProgramStr.append(
                "return 0;\n}"
        );
        return halideProgramStr.toString();
    }
}
