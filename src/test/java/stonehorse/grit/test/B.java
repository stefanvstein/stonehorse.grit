package stonehorse.grit.test;

public class B extends A {
    private static final long serialVersionUID = 1L;

    private B(int num) {
        super(num);
    }
    private B(String text) {
        super(text);
    }

    public static B b(){
        return new B("b");
    }
    public static B b(String text){
        return new B(text);
    }
    
    public static B b(int num){
        return new B(num);
    }
    
    @Override
    public String toString() {
        if (isText)
            return "B:" + text;
        return "B:" + num;
    }
}
