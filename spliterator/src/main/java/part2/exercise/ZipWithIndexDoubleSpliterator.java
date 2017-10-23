package part2.exercise;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;

public class ZipWithIndexDoubleSpliterator extends Spliterators.AbstractSpliterator<IndexedDoublePair> {


    private final OfDouble inner;
    private int currentIndex;

    public ZipWithIndexDoubleSpliterator(OfDouble inner) {
        this(0, inner);
    }

    private ZipWithIndexDoubleSpliterator(int firstIndex, OfDouble inner) {
        super(inner.estimateSize(), inner.characteristics());
        currentIndex = firstIndex;
        this.inner = inner;
    }

    @Override
    public int characteristics() {
        // TODO
        return inner.characteristics();
    }

    @Override
    public boolean tryAdvance(Consumer<? super IndexedDoublePair> action) {
        // TODO
        return inner.tryAdvance((double value) -> action.accept(new IndexedDoublePair(currentIndex++,value)));
    }

    @Override
    public void forEachRemaining(Consumer<? super IndexedDoublePair> action) {
        // TODO
        inner.forEachRemaining(((double value) -> action.accept(new IndexedDoublePair(currentIndex++, (double) value))));
    }

    @Override
    public Spliterator<IndexedDoublePair> trySplit() {
        // TODO
        OfDouble newSplit;
        ZipWithIndexDoubleSpliterator result;
        if (inner.hasCharacteristics(Spliterator.SIZED| Spliterator.SUBSIZED)) {
            newSplit = inner.trySplit();
            result = new ZipWithIndexDoubleSpliterator(currentIndex,newSplit);
            currentIndex+=newSplit.estimateSize();
            return result;
        } else {
            return super.trySplit();
        }
    }

    @Override
    public long estimateSize() {
        // TODO
        return inner.estimateSize();
    }
}
