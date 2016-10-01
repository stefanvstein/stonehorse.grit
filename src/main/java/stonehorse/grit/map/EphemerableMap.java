package stonehorse.grit.map;


import stonehorse.grit.PersistentMap;

public interface EphemerableMap<K, V> extends PersistentMap<K, V> {

	EphemeralMap<K, V> ephemeral();
}
