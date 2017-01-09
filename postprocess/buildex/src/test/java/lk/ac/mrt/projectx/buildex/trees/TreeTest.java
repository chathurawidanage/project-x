package lk.ac.mrt.projectx.buildex.trees;

import junit.framework.TestCase;
import lk.ac.mrt.projectx.buildex.X86Analysis;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by krv on 1/5/17.
 */
public class TreeTest extends TestCase {

    Tree tree;

    Node<Float> nde;
    Node<Float> nde2;
    Node<Float> nde3;

    public void setUp() throws Exception {
        super.setUp();
        tree = new Tree() {
            @Override
            public void simplifyTree() {

            }
        };
        nde = new AbstractNode<>();
        nde2 = new AbstractNode<>();
        nde3 = new AbstractNode<>();
        nde.srcs.add(nde2);
        nde2.srcs.add(nde3);
        tree.setHead(nde);
    }


    public void testCleanupVisit() throws Exception {
        nde.visited = true;
        nde3.visited = false;
        nde2.visited = true;
        nde2.operation = X86Analysis.Operation.op_add;
        assertEquals((Boolean) true, nde.visited);
        assertEquals((Boolean) true, nde2.visited);
        assertEquals((Boolean) false, nde3.visited);
        tree.cleanupVisit();
        assertEquals((Boolean) false, tree.getHead().visited);
        assertEquals((Boolean) false, nde2.visited);
        assertEquals((Boolean) false, nde3.visited);
    }

    public void testNumberTreeNodes() throws Exception {
        tree.setNumNodes(0);
        tree.numberTreeNodes();
        Node nde = tree.getHead();
        assertEquals((Integer) 1, nde.order_num);
        assertEquals((Integer) 2, nde2.order_num);
        assertEquals((Integer) 3, nde3.order_num);
    }

    public void testJavaPassByRefTest() throws Exception {
        Integer x = new Integer(55);
        Add(x);
        assertNotSame((Integer) 56, x);
        toyIntger toy = new toyIntger();
        Add(toy);
        assertEquals((Integer) 56, toy.value);

    }

    private void Add(Integer inta) {
        inta = inta + 1;
    }

    private void Add(toyIntger inta) {
        inta.value = inta.value + 1;
    }

    private class toyIntger {

        public Integer value = 55;
    }

    public void testArrayListReplace() throws Exception{
        Integer int1 = 1;
        Integer int2 = 2;
        Integer int3 = 3;
        Integer int4 = 4;

        List<Integer> intList  = new ArrayList<>();
        intList.add(int1);
        intList.add(int2);
        intList.add(int3);
        intList.add(int4);

        Integer int5 = (Integer)intList.get(2);
        assertEquals(int5, int3);

        intList.remove(2);
        Integer int6 = (Integer)intList.get(2);
        assertNotSame(int6, int3);

        Integer int7 = 3;
        intList.add(2,int7);
        Integer int8 = (Integer)intList.get(2);
        assertEquals(int8, int7);
        assertEquals(int3, int8);
        Integer int9 = (Integer)intList.get(3);
        assertEquals(int4, int9);
    }
}