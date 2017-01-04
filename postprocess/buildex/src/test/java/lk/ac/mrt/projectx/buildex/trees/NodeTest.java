package lk.ac.mrt.projectx.buildex.trees;

import junit.framework.TestCase;

/**
 * Created by krv on 1/2/17.
 */
public class NodeTest extends TestCase {

    public void testIsNodeSimilar() throws Exception {
        AbstractNode<Float> node1 = new AbstractNode<>();
<<<<<<< HEAD
        AbstractNode<Float> node2 = new AbstractNode<>(node1);
        int ans = node1.compareTo(node2);
        assertEquals(1,ans);
=======
        AbstractNode<Float> node2 = new AbstractNode<>();

>>>>>>> cf85ef59cacdd4e1e92e69c254b0d53f19919468
    }
}