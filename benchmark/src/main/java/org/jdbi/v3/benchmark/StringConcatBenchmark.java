package org.jdbi.v3.benchmark;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Multiset;
import org.apache.commons.lang3.RandomStringUtils;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

@BenchmarkMode(Mode.Throughput)
@Measurement(time = 5)
@Warmup(time = 2)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Benchmark)
public class StringConcatBenchmark {

    private final Multiset<String> countifyVec = HashMultiset.create();
    private final ListMultimap<String, String> joinVec = ArrayListMultimap.create();

    @Setup
    public void createTestData() {
        final Random r = new Random();
        for (int i = 0; i < 10_000; i++) {
            countifyVec.add(RandomStringUtils.randomAlphanumeric(r.nextInt(5) + 2), r.nextInt(500) + 1);
            final String key = RandomStringUtils.randomAlphanumeric(10);
            for (int j = 0; j < r.nextInt(20); j++) {
                joinVec.put(key, RandomStringUtils.randomAlphanumeric(10));
            }
        }
    }

    @Benchmark
    public void formatCountify(Blackhole bh) {
        countifyVec.forEachEntry((e, c) -> {
            bh.consume(countify(c, e));
        });
    }

    @Benchmark
    public void stringBuilderCountify(Blackhole bh) {
        countifyVec.forEachEntry((e, c) -> {
            bh.consume(fastCountify(c, e));
        });
    }

    @Benchmark
    public void formatWordJoin(Blackhole bh) {
        Multimaps.asMap(joinVec).forEach((word, elems) -> bh.consume(wordJoin(word, elems)));
    }

    @Benchmark
    public void stringBuilderWordJoin(Blackhole bh) {
        Multimaps.asMap(joinVec).forEach((word, elems) -> bh.consume(fastWordJoin(word, elems)));
    }

    public static String defaultPluralize(final String word) {
        return word + "s";
    }

    public static String countify(final long count, final String word) {
        return countify(count, word, defaultPluralize(word));
    }

    public static String countify(final long count, final String word, final String pluralWord) {
        final String c = count == 0 ? "no" : Long.toString(count);
        return String.format("%s %s", c, count != 1 ? pluralWord : word);
    }

    public static String wordJoin(final String word, final List<String> elements) {
        if (elements.size() < 3) {
            return String.join(String.format(" %s ", word), elements);
        }
        return String.join(
                ", ",
                Stream
                        .concat(
                                elements.subList(0, elements.size() - 1).stream(),
                                Stream.of(String.format("%s %s", word, elements.get(elements.size() - 1)))
                        )
                        .collect(Collectors.toUnmodifiableList())
        );
    }

    public static String fastCountify(final long count, final String word) {
        return countify(count, word, defaultPluralize(word));
    }

    public static String fastCountify(final long count, final String word, final String pluralWord) {
        final String c = count == 0 ? "no" : Long.toString(count);
        return c + " " + (count != 1 ? pluralWord : word);
    }

    public static String fastWordJoin(final String word, final List<String> elements) {
        if (elements.size() < 3) {
            return String.join(" " + word + " ", elements);
        }
        final StringBuilder result = new StringBuilder();
        final int upperBound = elements.size() - 1;
        for (int i = 0; i < upperBound; i++) {
            result.append(elements.get(i));
            result.append(", ");
        }
        result.append(word);
        result.append(elements.get(upperBound));
        return result.toString();
    }
}
