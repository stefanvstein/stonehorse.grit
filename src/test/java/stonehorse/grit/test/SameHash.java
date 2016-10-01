package stonehorse.grit.test;

public class SameHash {
    private int v;

    public SameHash(int v) {
        this.v = v;
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof SameHash))
            return false;
        return v == ((SameHash) obj).v;
    }

    @Override
    public String toString() {
        return "SameHash(" + v + ")";
    }
}