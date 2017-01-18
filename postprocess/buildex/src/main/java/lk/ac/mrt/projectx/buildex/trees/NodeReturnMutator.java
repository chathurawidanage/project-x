package lk.ac.mrt.projectx.buildex.trees;

import java.util.List;

/**
 * Created by krv on 1/4/17.
 */
public interface NodeReturnMutator {

    public Object mutate(Object nodeValue, List<Object> traverseValue, Object value);
}
