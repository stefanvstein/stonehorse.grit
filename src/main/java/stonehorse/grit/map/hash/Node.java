package stonehorse.grit.map.hash;




import java.io.Serializable;

//TODO En nod måste veta om den är tom, så att ägaren helt enkelt kan ta bort den. Result kanske ska innehålla isRemoveable?
public interface Node extends Serializable{

    Result with(int shift, int hash, Object key, Object val, boolean isAdding);


    Node without(int shift, int hash, Object key);

   
    Object find(int shift, int hash, Object key, Object notFound);


    Result withEphemerally(Object owner, int shift, int hash,
                           Object key, Object val, boolean isAdding);

    Result withoutEphemerally(Object owner, int shift, int hash,
                            Object key, boolean isRemoving);

    boolean isEmpty();

    //Returns value is if node is a single value, otherwise null
    //Map.Entry collapse();

int size();
	Object[] array();

	String toString(int ident);


}