package lk.ac.mrt.projectx.buildex.trees;

import lk.ac.mrt.projectx.buildex.models.common.CommonUtil;
import lk.ac.mrt.projectx.buildex.models.common.FuncInfo;
import lk.ac.mrt.projectx.buildex.models.common.StaticInfo;
import lk.ac.mrt.projectx.buildex.models.memoryinfo.MemoryRegion;
import lk.ac.mrt.projectx.buildex.models.output.MemoryType;
import lk.ac.mrt.projectx.buildex.models.output.Operand;
import lk.ac.mrt.projectx.buildex.models.output.Output;
import lk.ac.mrt.projectx.buildex.models.output.ReducedInstruction;
import lk.ac.mrt.projectx.buildex.x86.X86Analysis;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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

    public ConcreteTree() {
        dummyTree = false;
        funcInside = false;
        frontier = new ArrayList<>( MAX_FRONTIERS );
    }

    //endregion public constructors

    //region protected methods
    //endregion protected methods

    //region public methods
    @Override
    public void simplifyTree() {
    }

    private Node searchNode(Operand opnd) {
        throw new NotImplementedException();
    }


    private void addToFrntier(Integer hash, Node node) {
        throw new NotImplementedException();
    }

    private void removeRegistersFromFrontier() {
        throw new NotImplementedException();
    }

    public Boolean updateDependencyBackward(ReducedInstruction instr, Output cinstr, StaticInfo info,
                                            Integer line, List<MemoryRegion> regions, List<FuncInfo> funcInfos) {

        boolean INDIRECTION = true;
        boolean ASSIGN_OPT = true;
        boolean INTERMEDIATE_BUFFER_ANALYSIS = false;
        boolean SIMPLIFICATIONS = false;


        if (funcInside) {
            if (info.getPc() == funcInfos.get( this.funcIndex ).getStart() &&
                    info.getPc() <= funcInfos.get( this.funcIndex ).getEnd()) {
                return false;
            } else {
                this.funcInside = false;
            }
        }

        // TODO [Helium] : have precomputed nodes for immediate integers -> can we do it for floats as well
        // just need to point to them in future (space optimization)

        //TODO : Damn this is the head node in the super class
        Node head = this.getHead();

        if (head == null) {
            head = new ConcreteNode( instr.getDst(), regions );
            this.setHead( head );
            int hash = generateHash( instr.getDst() );
            assert hash != -1 : "Hash cannot be -1";

            if (hash != -1) {
                int amount = frontier.get( hash ).getAmount();
                frontier.get( hash ).getBucket().set( amount, head );
                frontier.get( hash ).setAmount( amount + 1 );
            }

            if (INDIRECTION) {
//                if ((info.getInstructionType().ordinal() & StaticInfo.InstructionType.INPUT_DEPENDENT_INDIRECT.ordinal())
//                        == StaticInfo.InstructionType.INPUT_DEPENDENT_INDIRECT.ordinal())
                if ((info.getInstructionType() == StaticInfo.InstructionType.INPUT_DEPENDENT_INDIRECT)) {
                    if (!instr.getDst().getAddress().isEmpty()) {
                        for (int buf = 0 ; buf < regions.size() ; buf++) {
                            if (CommonUtil.isOverlapped( regions.get( buf ).getStartMemory(),
                                    regions.get( buf ).getEndMemory(), instr.getDst().getValue().longValue(),
                                    instr.getDst().getValue().longValue() + instr.getDst().getWidth() )) {
                                addAddressDependency( head, instr.getDst().getAddress() );

                            }
                        }
                    }
                }

            }
        }

        throw new NotImplementedException();
    }

    private void addAddressDependency(Node head, List<Operand> address) {
        throw new NotImplementedException();
    }

    //endregion public methods

    //region private methods

    private Integer generateHash(Operand opnd) {
        if (opnd.getType() == MemoryType.REG_TYPE) {
            return ((Integer) opnd.getValue()) / X86Analysis.MAX_SIZE_OF_REG;
        } else if ((opnd.getType() == MemoryType.MEM_STACK_TYPE) || (opnd.getType() == MemoryType.MEM_HEAP_TYPE)) {
            int offset = ((Integer) opnd.getValue()) % MEM_REGION;
            return offset + MEM_OFFSET;
        }
        return -1;
    }

    //endregion private methods

    //region Inner Classes
    private class Frontier {

        private List<Node> bucket;
        private Integer amount;

        public Frontier() {
            bucket = new ArrayList<>();
            amount = new Integer( 0 );
        }

        public Integer getAmount() {
            return this.amount;
        }

        public void setAmount(Integer amount) {
            this.amount = amount;
        }

        public List<Node> getBucket() {
            return bucket;
        }

        public void setBucket(List<Node> bucket) {
            this.bucket = bucket;
        }
    }
    //endregion Inner Classes
}
