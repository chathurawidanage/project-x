package lk.ac.mrt.projectx.buildex.trees;

import lk.ac.mrt.projectx.buildex.models.common.StaticInfo;
import lk.ac.mrt.projectx.buildex.models.memoryinfo.MemoryRegion;
import lk.ac.mrt.projectx.buildex.models.output.MemoryType;
import lk.ac.mrt.projectx.buildex.models.output.Operand;
import lk.ac.mrt.projectx.buildex.models.output.Output;
import lk.ac.mrt.projectx.buildex.models.output.ReducedInstruction;
import lk.ac.mrt.projectx.buildex.x86.X86Analysis;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.servlet.jsp.tagext.FunctionInfo;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by krv on 1/2/17.
 */
public class ConcreteTree extends Tree {


    //region private variables
    private static final int MAX_FRONTIERS = 1000;
    private static final int MEM_OFFSET = 200;
    private static final int MEM_REGION = (MAX_FRONTIERS - MEM_OFFSET);

    private List<Conditional> conditionals;
    private List<Frontier> frontier; // this is actually a hash table keeping pointers to the nodes already allocated
    private List<Integer> memInFrontier;
    private Boolean funcInside;
    private Integer funcIndex;
    //endregion private variables

    //region public constructors

    public ConcreteTree(){
        dummyTree = false;
        funcInside = false;
        frontier = new ArrayList<>(MAX_FRONTIERS);
    }

    //endregion public constructors

    //region protected methods
    //endregion protected methods

    //region public methods
    @Override
    public void simplifyTree() {
    }

    private Node searchNode(Operand opnd){
        throw new NotImplementedException();
    }



    private void addToFrntier(Integer hash, Node node){
        throw new NotImplementedException();
    }

    private void removeRegistersFromFrontier(){
        throw new NotImplementedException();
    }

    public Boolean updateDependencyBackward(ReducedInstruction reducedInstruction, Output first, StaticInfo second, Integer curPos, List<MemoryRegion> regions, List<FunctionInfo> funcInfo){
        throw new NotImplementedException();
    }

    //endregion public methods

    //region private methods

    private Integer generateHash(Operand opnd){
        if(opnd.getType() == MemoryType.REG_TYPE) {
            return ((Integer) opnd.getValue()) / X86Analysis.MAX_SIZE_OF_REG;
        }else if( (opnd.getType() == MemoryType.MEM_STACK_TYPE) || (opnd.getType() == MemoryType.MEM_HEAP_TYPE)){
            int offset = ((Integer) opnd.getValue()) % MEM_REGION;
            return offset + MEM_OFFSET;
        }
        return -1;
    }

    //endregion private methods

    //region Inner Classes
    private class Frontier {

        List<Node> bucket;
        Integer amount;

        public Frontier() {
            bucket = new ArrayList<>();
            amount = new Integer(0);
        }
    }
    //endregion Inner Classes
}
