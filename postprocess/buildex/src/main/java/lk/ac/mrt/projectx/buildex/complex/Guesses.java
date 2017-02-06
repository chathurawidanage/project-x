package lk.ac.mrt.projectx.buildex.complex;

/**
 * @author Chathura Widanage
 */
public class Guesses {
    private double rcof;
    private double tcof;
    private int votes;

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public void incrVote(){
        this.votes++;
    }

    public int getVotes() {
        return votes;
    }

    public double getRcof() {
        return rcof;
    }

    public void setRcof(double rcof) {
        this.rcof = rcof;
    }

    public double getTcof() {
        return tcof;
    }

    public void setTcof(double tcof) {
        this.tcof = tcof;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Guesses)) return false;

        Guesses guesses = (Guesses) o;

        if (Double.compare(guesses.getRcof(), getRcof()) != 0) return false;
        return Double.compare(guesses.getTcof(), getTcof()) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(getRcof());
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getTcof());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Guesses{" +
                "rcof=" + rcof +
                ", tcof=" + tcof +
                ", votes=" + votes +
                '}';
    }
}
