package stonehorse.grit.vector;


import stonehorse.candy.Atomic;
import stonehorse.grit.PersistentList;
import stonehorse.grit.PersistentVector;
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

public class ListCollector<T>
        implements Collector<T, AtomicReference<PersistentVector<T>>, PersistentList<T>> {

    private final PersistentVector<T> empty;

    private ListCollector(PersistentVector<T> empty) {
        this.empty=empty;
    }

    public static <T> ListCollector<T> collector(PersistentVector<T> empty){
        return new ListCollector<>(empty);
    }

    @Override
    public Supplier<AtomicReference<PersistentVector<T>>> supplier() {
        return ()->atomic(empty);
    }

    @Override
    public BiConsumer<AtomicReference<PersistentVector<T>>, T> accumulator() {
        return (holder, t) -> swap(holder,v->v.with(t));
    }

    @Override
    public BinaryOperator<AtomicReference<PersistentVector<T>>> combiner() {
        return (left, right) -> {
            swap(left,v->v.withAll(right.get()));
            return left;
        };
    }

    @Override
    public Function<AtomicReference<PersistentVector<T>>, PersistentList<T>> finisher() {
        return a->PList.of(Atomic.value(a));
    }

    @Override
    public Set<Characteristics> characteristics() {
       return Sets.set(Characteristics.CONCURRENT);
    }
}