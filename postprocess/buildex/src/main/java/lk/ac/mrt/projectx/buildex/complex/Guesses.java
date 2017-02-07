package lk.ac.mrt.projectx.buildex.complex;

/**
 * @author Chathura Widanage
 */
public class Guesses {
    private double rcof;
    private double tcof;
    private double r2cof;
    private double t2cof;
    private double rtcof;
    private double ccof;
    private int votes;

    public double getR2cof() {
        return r2cof;
    }

    public void setR2cof(double r2cof) {
        this.r2cof = r2cof;
    }

    public double getT2cof() {
        return t2cof;
    }

    public void setT2cof(double t2cof) {
        this.t2cof = t2cof;
    }

    public double getRtcof() {
        return rtcof;
    }

    public void setRtcof(double rtcof) {
        this.rtcof = rtcof;
    }

    public double getCcof() {
        return ccof;
    }

    public void setCcof(double ccof) {
        this.ccof = ccof;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public void incrVote() {
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
                ", r2cof=" + r2cof +
                ", t2cof=" + t2cof +
                ", rtcof=" + rtcof +
                ", ccof=" + ccof +
                ", votes=" + votes +
                '}';
    }
}
