package lk.ac.mrt.projectx.buildex.trees;

import lk.ac.mrt.projectx.buildex.DefinesDotH;
import lk.ac.mrt.projectx.buildex.models.Pair;
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

import static lk.ac.mrt.projectx.buildex.DefinesDotH.DR_REG.DR_REG_RBP;
import static lk.ac.mrt.projectx.buildex.DefinesDotH.DR_REG.DR_REG_RSP;
import static lk.ac.mrt.projectx.buildex.models.output.MemoryType.*;
import static lk.ac.mrt.projectx.buildex.x86.X86Analysis.Operation.op_add;

/**
 * Created by krv on 1/2/17.
 */
public class ConcreteTree extends Tree {


    //region private variables
    private static final int SIZE_PER_FRONTIER = 100;
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
                                break;
                            }
                        }
                    }
                }

            }

            List<Node> fullOverlapNodes = new ArrayList<>();

            // first get the partial overlap nodes - if dest is part of the frontier of a wide region it will be part
            // of the nodes returned
            List<Pair<Node, List<Node>>> partialOverlapNodes = getPartialOverlapNodes( instr.getDst() );

        }

        throw new NotImplementedException();
    }

    private List<Pair<Node, List<Node>>> getPartialOverlapNodes(Operand opnd) {
        logger.debug( "Checking for partial overlap nodes" );
        List<Pair<Node, List<Node>>> ret = null;
        if (opnd.getType() == REG_TYPE) {
            Integer hash = generateHash( opnd );
            ret = splitPartialOverlap( opnd, hash );
        } else if ((opnd.getType() == MEM_STACK_TYPE) || (opnd.getType() == MEM_HEAP_TYPE)) {
            for (int i = 0 ; i < memInFrontier.size() ; i++) {
                Integer index = memInFrontier.get( i );
                ret = splitPartialOverlap( opnd, index );
            }
        }

        return ret;
    }

    private List<Pair<Node, List<Node>>> splitPartialOverlap(Operand opnd, Integer hash) {
        List<Pair<Node, List<Node>>> nodes = new ArrayList<>();

        for (int i = 0 ; i < frontier.get( hash ).getAmount() ; i++) {
            Node splitNode = frontier.get( hash ).getBucket().get( i );
            Long start = frontier.get( hash ).getBucket().get( i ).getSymbol().getValue().longValue();
            Integer width = frontier.get( hash ).getBucket().get( i ).getSymbol().getWidth();

            List<Node> splits = new ArrayList<>();
            if (opnd.getType() == splitNode.getSymbol().getType()) {

                if ((start >= opnd.getValue().longValue()) && (start <= opnd.getValue().longValue() - opnd.getWidth()
                        - 1) //start within
                        && (start + width > opnd.getValue().longValue() + opnd.getWidth())) // end strictly after
                {

                    Operand first = new Operand( splitNode.getSymbol().getType(), opnd.getValue().intValue() + opnd
                            .getWidth() - start.intValue(), start );
                    Operand second = new Operand( splitNode.getSymbol().getType(), width - first.getWidth(), opnd
                            .getValue().intValue() + opnd.getWidth() );
                    splits.add( createOrGetNode( first ) );
                    splits.add( createOrGetNode( second ) );

                    nodes.add( new Pair<Node, List<Node>>( splitNode, splits ) );

                    logger.debug( "partial oveerlap %s %s", first.toString(), second.toString() );

                } else if (start <= opnd.getValue().longValue() /* start strictly before */ && (start + width - 1 >= opnd
                        .getValue().longValue()) && (start + width - 1 <= opnd.getValue().longValue() + opnd.getWidth() - 1))// end within
                {

                    Operand first = new Operand( splitNode.getSymbol().getType(), opnd.getValue().intValue() -
                            start.intValue(), start );
                    Operand second = new Operand( splitNode.getSymbol().getType(), width - first.getWidth(),
                            opnd.getValue() );

                    splits.add( createOrGetNode( first ) );
                    splits.add( createOrGetNode( second ) );

                    nodes.add( new Pair<Node, List<Node>>( splitNode, splits ) );

                    logger.debug( "partial oveerlap %s %s", first.toString(), second.toString() );

                } else if ((start < opnd.getValue().longValue()) && (start + width > opnd.getValue().longValue() +
                        opnd.getWidth())) {

                    Operand first = new Operand( splitNode.getSymbol().getType(), opnd.getValue().intValue() -
                            start.intValue(), start );
                    Operand second = new Operand( splitNode.getSymbol().getType(), width - first.getWidth() - opnd.getWidth(),
                            opnd.getValue().longValue() + opnd.getWidth() );

                    splits.add( createOrGetNode( first ) );
                    splits.add( createOrGetNode( opnd ) );
                    splits.add( createOrGetNode( second ) );

                    nodes.add( new Pair<Node, List<Node>>( splitNode, splits ) );

                    logger.debug( "partial oveerlap %s %s", first.toString(), second.toString() );

                }


            }
        }

        throw new NotImplementedException();
//        return nodes;
    }

    private Node createOrGetNode(Operand first) {
        Node node = searchNode( first );
        if (node == null) {
            node = new ConcreteNode( first );
        }
        return node;

    }

    private Integer generateHash(Operand opnd) {
        if (opnd.getType() == REG_TYPE) {
            return ((Integer) opnd.getValue()) / X86Analysis.MAX_SIZE_OF_REG;
        } else if ((opnd.getType() == MemoryType.MEM_STACK_TYPE) || (opnd.getType() == MemoryType.MEM_HEAP_TYPE)) {
            int offset = ((Integer) opnd.getValue()) % MEM_REGION;
            return offset + MEM_OFFSET;
        }
        return -1;
    }

    //endregion public methods

    //region private methods

    /**
     * rbp - register base pointer (start of stack)
     * rsp - register stack pointer (current location in stack, growing downwards)
     *
     * @param head
     * @param opnds
     */
    private void addAddressDependency(Node node, List<Operand> opnds) {
        // four operand here for [base + index + scale + disp]

        //make sure that this is a base-disp address
        if ((opnds.get( 0 ).getValue() == 0) && (opnds.get( 2 ).getValue() == 0)) {
            return;
        }
        Operand operand0 = opnds.get( 0 );
        Operand operand1 = opnds.get( 1 );

        // should have home index
        DefinesDotH.DR_REG reg1 = operand0.memRangeToRegister();
        DefinesDotH.DR_REG reg2 = operand1.memRangeToRegister();

        // absoulute addr and rsp, rbp combination filtering
        // rbp - register base pointer (start of stack)
        // rsp - register stack pointer (current location in stack, growing downwards)
        // TODO : this condition should be moved to operand class
        if ((reg1 == DR_REG_RSP || reg1 == DR_REG_RBP || operand0.getValue() == 0) &&
                (reg2 == DR_REG_RSP || reg2 == DR_REG_RBP || operand1.getValue() == 0)) {
            return;
        }

        // reg type used but doesnt matter coz used as an operation only node
        ConcreteNode indirectNode = new ConcreteNode( REG_TYPE, 0L, 0L, 0.0f );
        indirectNode.setOperation( X86Analysis.Operation.op_indirect );
        node.addForwardReference( indirectNode );

        ConcreteNode currentNode = indirectNode;

        /*ok now with cases*/
        boolean reg1_rsp = (reg1 == DR_REG_RSP || reg1 == DR_REG_RBP);
        boolean reg2_rsp = (reg2 == DR_REG_RSP || reg2 == DR_REG_RBP);

        // ok if one of the regs is a RSP or a RBP then, omit the displacement
        if (reg1_rsp && !reg2_rsp) {
            Node addr_node = searchNode( opnds.get( 1 ) );
            if (addr_node == null) {
                addr_node = new ConcreteNode( opnds.get( 1 ) );
                addToFrontier( generateHash( opnds.get( 1 ) ), addr_node );
            }
            currentNode.addForwardReference( addr_node );
        } else if (!reg1_rsp && reg2_rsp) {
            Node addr_node = searchNode( opnds.get( 0 ) );
            if (addr_node == null) {
                addr_node = new ConcreteNode( opnds.get( 0 ) );
                addToFrontier( generateHash( opnds.get( 0 ) ), addr_node );
            }
            currentNode.addForwardReference( addr_node );
        } else if (!reg1_rsp && !reg2_rsp) { // [edx + 2] like addresses
            Node addr_node = null;
            if (opnds.get( 0 ).getValue() == 0) {
                addr_node = searchNode( opnds.get( 1 ) );
                if (addr_node == null) {
                    addr_node = new ConcreteNode( opnds.get( 1 ) );
                    addToFrontier( generateHash( opnds.get( 1 ) ), addr_node );
                }
            } else if (opnds.get( 1 ).getValue() == 0) {
                addr_node = searchNode( opnds.get( 0 ) );
                if (addr_node == null) {
                    addr_node = new ConcreteNode( opnds.get( 0 ) );
                    addToFrontier( generateHash( opnds.get( 0 ) ), addr_node );
                }
            } else {
                assert false : "ERROR: not handled";
            }

            ConcreteNode add_node = new ConcreteNode( REG_TYPE, 0L, 4L, 0.0f ); //reg_type is used here; it doesn't
            // really matter as this is an operation only node
            add_node.setOperation( op_add );

            currentNode.addForwardReference( add_node );
            add_node.addForwardReference( addr_node );
            ConcreteNode imm = new ConcreteNode( opnds.get( 3 ) );
            add_node.addForwardReference( imm );
        } else {
            assert false : "ERROR: should not reach here";
        }
    }

    private void addToFrontier(Integer hash, Node node) {
        assert (node.getSymbol().getType() != MemoryType.IMM_INT_TYPE) && (node.getSymbol().getType() != MemoryType
                .IMM_FLOAT_TYPE) : "Immediate types cannot be in the frontier";
        assert frontier.get( hash ).getAmount() < SIZE_PER_FRONTIER : "Bucket size is full";
        frontier.get( hash ).getBucket().set( frontier.get( hash ).getAmount(), node );
        frontier.get( hash ).setAmount( frontier.get( hash ).getAmount() + 1 );

        // if this a memory operand we should memorize it
        if (node.getSymbol().getType() == REG_TYPE) {
            if (!memInFrontier.contains( hash )) {
                memInFrontier.add( hash );
            }
        }
    }

    private Node searchNode(Operand opnd) {
        Long hash = generateHash( opnd ).longValue();
        Number value = opnd.getValue();
        Integer width = opnd.getWidth();

        for (int i = 0 ; i < frontier.get( hash.intValue() ).getAmount() ; i++) {
            // we don't need to check for types as we seperate them out in hashing
            // could furthur optimize this search by having a type specific search algo
            Node frntNode = frontier.get( hash.intValue() ).getBucket().get( i );
            if (frntNode.symbol.getValue() == value && frntNode.symbol.getWidth() == width) {
                return frntNode;
            }
        }
        return null;
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
