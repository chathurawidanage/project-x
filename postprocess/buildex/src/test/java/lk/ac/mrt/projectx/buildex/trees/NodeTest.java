package lk.ac.mrt.projectx.buildex.trees;

import junit.framework.TestCase;

/**
 * Created by krv on 1/2/17.
 */
public class NodeTest extends TestCase {

    public void testIsNodeSimilar() throws Exception {
        AbstractNode node1 = new AbstractNode();
        AbstractNode node2 = new AbstractNode(node1);
        int ans = node1.compareTo(node2);
        assertEquals(1,ans);
    }
}