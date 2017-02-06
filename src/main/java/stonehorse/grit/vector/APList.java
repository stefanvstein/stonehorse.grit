package stonehorse.grit.vector;

import stonehorse.grit.PersistentList;
import stonehorse.grit.tools.ImmutableList;
import stonehorse.grit.tools.Util;

import java.util.RandomAccess;

public abstract class APList<T> extends ImmutableList<T> implements PersistentList<T>, RandomAccess {
}
