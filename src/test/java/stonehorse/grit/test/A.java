package stonehorse.grit.test;

import stonehorse.grit.tools.Util;

import java.io.Serializable;

public class A implements Serializable, Comparable<A>{

    private static final long serialVersionUID = 1L;
    final int num;
    final String text;
    final boolean isText;

    A(int num) {
        this.num = num;
        text = null;
        isText = false;
    }

    public static A a(){
        return new A("a");
    }
    public static A a(String text){
        return new A(text);
    }
    
    public static A a(int num){
        return new A(num);
    }
    
    A(String text) {
        this.text = text;
        num = 0;
        isText = true;
    }

    @Override
    public String toString() {
        if (isText)
            return "A:" + text;
        return "A:" + num;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof A))
            return false;
        A a = (A) obj;
        if (a.isText != isText)
            return false;
        if (isText) {
            return Util.equal(a.text, text);
        } else {
            return a.num == num;
        }
    }

    @Override
    public int hashCode() {
        if (isText) {
            return Util.hashCode(text);
        }
        else return num;

    }

	@Override public int compareTo(A o) {
		if(o.equals(this))
			return 0;
		return 1;
	}
}
