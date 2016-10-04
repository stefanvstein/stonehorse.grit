package stonehorse.grit.map;

import stonehorse.candy.Atomic;
import stonehorse.grit.PersistentMap;
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
import static stonehorse.candy.Choices.ifelse;

public class ToMapCollector<T, K, U> implements Collector<T, AtomicReference<PersistentMap<K, U>>, PersistentMap<K, U>> {
    private final Function<? super T, ? extends K> keyMapper;
    private final Function<? super T, ? extends U> valueMapper;
    private final BinaryOperator<U> valueMerger;
    private final PersistentMap<K, U> empty;

    public ToMapCollector(Function<? super T, ? extends K> keyMapper,
                          Function<? super T, ? extends U> valueMapper,
                          BinaryOperator<U> valueMerger,
                          PersistentMap<K,U> empty) {
        this.keyMapper = keyMapper;
        this.valueMapper = valueMapper;
        this.valueMerger = valueMerger;
        this.empty=empty;
    }

    @Override
    public Supplier<AtomicReference<PersistentMap<K, U>>> supplier() {
        return ()-> atomic(empty);
    }

    @Override
    public BiConsumer<AtomicReference<PersistentMap<K, U>>, T> accumulator() {
        return (holder, t) -> {
            K k = keyMapper.apply(t);
            swap(holder,m->
                ifelse(m.has(k),
                        ()->m.with(k, valueMerger.apply(m.get(k), valueMapper.apply(t))),
                        ()->m.with(k, valueMapper.apply(t))));
        };
    }

    @Override
    public BinaryOperator<AtomicReference<PersistentMap<K, U>>> combiner() {
        return (left, right) -> {
            swap(left,l-> l.withAll(right.get()));
            return left;
        };
    }

    @Override
    public Function<AtomicReference<PersistentMap<K, U>>, PersistentMap<K, U>> finisher() {
        return Atomic::value;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Sets.set(Characteristics.UNORDERED, Characteristics.CONCURRENT);
    }

    public static <T, K, U> Collector<T, ?, PersistentMap<K, U>>
    collector(Function<? super T, ? extends K> keyMapper,
              Function<? super T, ? extends U> valueMapper,
              BinaryOperator<U> valueMerger,
              PersistentMap<K, U> empty) {
        return new ToMapCollector<>(keyMapper, valueMapper, valueMerger, empty);
    }

}
