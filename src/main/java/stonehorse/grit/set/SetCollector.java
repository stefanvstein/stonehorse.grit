package stonehorse.grit.set;


import stonehorse.candy.Atomic;
import stonehorse.grit.PersistentSet;
import stonehorse.grit.Sets;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static stonehorse.candy.Atomic.atomic;
import static stonehorse.candy.Atomic.swap;

public class SetCollector<T> implements Collector<T, AtomicReference<PersistentSet<T>>, PersistentSet<T>> {

    private final PersistentSet<T> empty;

    private SetCollector(PersistentSet<T> empty) {
        this.empty=empty;
    }

    public static <T> SetCollector<T> collector(PersistentSet<T> empty){
        return new SetCollector<>(empty);
    }

    @Override
    public Supplier<AtomicReference<PersistentSet<T>>> supplier() {
        return ()->atomic(empty);
    }

    @Override
    public BiConsumer<AtomicReference<PersistentSet<T>>, T> accumulator() {
        return (holder, t) -> swap(holder,s->s.with(t));
    }

    @Override
    public BinaryOperator<AtomicReference<PersistentSet<T>>> combiner() {
        return (left, right) -> {
            swap(left,l->l.union(right.get()));
            return left;
        };
    }

    @Override
    public Function<AtomicReference<PersistentSet<T>>, PersistentSet<T>> finisher() {
        return Atomic::value;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Sets.set(Characteristics.UNORDERED, Characteristics.CONCURRENT);
    }
}
