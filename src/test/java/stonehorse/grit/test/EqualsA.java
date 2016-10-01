package stonehorse.grit.test;

public class EqualsA {
    public static EqualsA likeA(){
        return new EqualsA();
    }
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof A);
    }
}
