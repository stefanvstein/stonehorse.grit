package stonehorse.grit.set;


import stonehorse.grit.PersistentSet;
import stonehorse.grit.Sets;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class SetCollector<T> implements Collector<T, AtomicReference<PersistentSet<T>>, PersistentSet<T>> {

    private final PersistentSet<T> empty;

    private SetCollector(PersistentSet<T> empty) {
        this.empty=empty;
    }

    public static <T> SetCollector<T> collector(PersistentSet<T> empty){
        return new SetCollector<T>(empty);
    }

    @Override
    public Supplier<AtomicReference<PersistentSet<T>>> supplier() {
        return ()->new AtomicReference<>(empty);
    }

    @Override
    public BiConsumer<AtomicReference<PersistentSet<T>>, T> accumulator() {
        return (holder, t) -> holder.updateAndGet(s->s.with(t));
    }

    @Override
    public BinaryOperator<AtomicReference<PersistentSet<T>>> combiner() {
        return (left, right) -> {
            left.updateAndGet(l->l.union(right.get()));
            return left;
        };
    }

    @Override
    public Function<AtomicReference<PersistentSet<T>>, PersistentSet<T>> finisher() {
        return AtomicReference::get;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Sets.set(Characteristics.UNORDERED, Characteristics.CONCURRENT);
    }
}
