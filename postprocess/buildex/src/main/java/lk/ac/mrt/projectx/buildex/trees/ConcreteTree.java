package lk.ac.mrt.projectx.buildex.trees;

import lk.ac.mrt.projectx.buildex.models.output.Operand;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by krv on 1/2/17.
 */
public class ConcreteTree extends Tree {


    //region private variables
    private static final int MAX_FRONTIERS = 1000;
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

    private Integer generateHash(Operand opnd){
        throw new NotImplementedException();
    }

    private void addToFrntier(Integer hash, Node node){
        throw new NotImplementedException();
    }

    private void removeRegistersFromFrontier(){
        throw new NotImplementedException();
    }

    private Boolean updateDependencyBackward(){
        throw new NotImplementedException();
    }

    //endregion public methods

    //region private methods
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
