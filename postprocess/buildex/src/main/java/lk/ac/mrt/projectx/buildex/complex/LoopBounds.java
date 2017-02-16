package lk.ac.mrt.projectx.buildex.complex;

/**
 * @author Chathura Widanage
 */
public class LoopBounds {
    public Bound r = new Bound();
    public Bound r2 = new Bound();
    public Bound t = new Bound();
    public Bound t2 = new Bound();
    public Bound rt = new Bound();
    public Bound c = new Bound();

    public long getIterations() {
        return r.getIterations() * r2.getIterations() * t.getIterations()
                * t2.getIterations() * rt.getIterations() * c.getIterations();
    }

    @Override
    public String toString() {
        return "LoopBounds{" +
                "r=" + r +
                ", r2=" + r2 +
                ", t=" + t +
                ", t2=" + t2 +
                ", rt=" + rt +
                ", c=" + c +
                '}';
    }

    public class Bound {
        public int low;
        public int high;

        private int getIterations() {
            return high - low + 1;
        }

        @Override
        public String toString() {
            return "Bound{" +
                    "low=" + low +
                    ", high=" + high +
                    '}';
        }
    }
}
