package lk.ac.mrt.projectx.buildex.models;

/**
 * @author Chathura Widanage
 */
public class Pair<F, S> {
    public F first;//keeping all public to do integer++ things
    public S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Pair) {
            Pair p = (Pair) obj;
            return p.first.equals(first) && p.second.equals(second);
        }
        return false;
    }
}
