package stonehorse.grit.vector;


import stonehorse.grit.PersistentVector;
import stonehorse.grit.Sets;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class VectorCollector<T>
        implements Collector<T, AtomicReference<PersistentVector<T>>, PersistentVector<T>> {

    private final PersistentVector<T> empty;

    private VectorCollector(PersistentVector<T> empty) {
        this.empty=empty;
    }

    public static <T> VectorCollector<T> collector(PersistentVector<T> empty){
        return new VectorCollector<T>(empty);
    }

    @Override
    public Supplier<AtomicReference<PersistentVector<T>>> supplier() {
        return ()->new AtomicReference<>(empty);
    }

    @Override
    public BiConsumer<AtomicReference<PersistentVector<T>>, T> accumulator() {
        return (holder, t) -> holder.updateAndGet(v->v.with(t));
    }

    @Override
    public BinaryOperator<AtomicReference<PersistentVector<T>>> combiner() {
        return (left, right) -> {
            left.updateAndGet(v->v.withAll(right.get()));
            return left;
        };
    }

    @Override
    public Function<AtomicReference<PersistentVector<T>>, PersistentVector<T>> finisher() {
        return AtomicReference::get;
    }

    @Override
    public Set<Characteristics> characteristics() {
       return Sets.set(Characteristics.CONCURRENT);
    }
}