package lk.ac.mrt.projectx.buildex.trees;

import junit.framework.TestCase;
import lk.ac.mrt.projectx.buildex.X86Analysis;

/**
 * Created by krv on 1/5/17.
 */
public class TreeTest extends TestCase {

    public void testCleanupVisit() throws Exception {
        Tree tree = new Tree() {
            @Override
            public void simplifyTree() {

            }
        };

        Node<Float> nde = new AbstractNode<>();
        Node<Float> nde2 = new AbstractNode<>();
        Node<Float> nde3 = new AbstractNode<>();
        nde.visited = true;
        nde3.visited = false;
        nde2.visited = true;
        nde2.operation = X86Analysis.Operation.op_add;
        nde.srcs.add(nde2);
        nde2.srcs.add(nde3);
        tree.setHead(nde);
        assertEquals((Boolean) true, nde.visited);
        assertEquals((Boolean) true, nde2.visited);
        assertEquals((Boolean) false, nde3.visited);
        tree.cleanupVisit();
        assertEquals((Boolean) false, tree.getHead().visited);
        assertEquals((Boolean) false, nde2.visited);
        assertEquals((Boolean) false, nde3.visited);
    }

}