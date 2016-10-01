package stonehorse.grit.test;

public class LikeA implements Comparable<A>{
@Override public boolean equals(Object obj) {
if(obj instanceof A)
	return true;
	return false;

}
@Override public int hashCode() {
		return "a".hashCode();
	}
@Override public int compareTo(A o) {
	if(o instanceof A)
		return 0;
	return 1;
}
}
