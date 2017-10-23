package part1.example;

import org.openjdk.jmh.annotations.*;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.StreamSupport;

@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@State(Scope.Benchmark)
public class ArrayIntBenchmark {

    @Param({"10000000"})
    public int length;

    public int[] array;

    @Setup
    public void setup() {
        array = ThreadLocalRandom.current().ints(length).toArray();
    }


    @Benchmark
    public long baselineSequential() {
        return Arrays.stream(array)
                     .sequential()
                     .asLongStream()
                     .sum();
    }

    @Benchmark
    public long baiselineParallel() {
        return Arrays.stream(array)
                     .parallel()
                     .asLongStream()
                     .sum();
    }

    @Benchmark
    public long arrayIntSpliteratorSeq() {
        return StreamSupport.intStream(new IntArraySpliterator(array), false)
                            .asLongStream()
                            .sum();
    }

    @Benchmark
    public long arrayIntSpliteratorParallel() {
        return StreamSupport.intStream(new IntArraySpliterator(array), true)
                            .asLongStream()
                            .sum();
    }
}
