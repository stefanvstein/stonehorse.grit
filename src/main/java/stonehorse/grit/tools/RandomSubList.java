package stonehorse.grit.tools;

import java.util.AbstractList;
import java.util.List;
import java.util.RandomAccess;

/**
 * Created by stefan on 1/29/17.
 */
public class RandomSubList<T> extends AbstractList<T> implements RandomAccess {
    private final List<T> list;
    private final int from;
    private final int to;

    private RandomSubList(List<T> list, int from, int to){
        this.list=list;
        this.from=from;
        this.to=to;
    }

    public static <T> RandomSubList<T> create(List<T> list, int from, int to){
        int listsize=list.size();
        if(from>listsize || from<0 || to>listsize || to<0 || from>to)
            throw new IndexOutOfBoundsException();
        if (from>to)
            throw new IllegalArgumentException();
        if(list instanceof RandomSubList) {
            RandomSubList<T> sublist = (RandomSubList<T>) list;
            return new RandomSubList<T>(sublist.list, sublist.from + from, sublist.to + to);
        }
        return new RandomSubList<T>(list, from, to);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return super.subList(fromIndex, toIndex);
    }

    @Override
    public T get(int index) {
        if(index>=to|| index < 0)
            throw new IndexOutOfBoundsException();
        return list.get(index+from);
    }

    @Override
    public int size() {
        return to-from;
    }
}
