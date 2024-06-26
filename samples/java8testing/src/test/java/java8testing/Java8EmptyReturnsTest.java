package java8testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Collections;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import mockit.Injectable;

import org.junit.jupiter.api.Test;

/**
 * The Class Java8EmptyReturnsTest.
 */
final class Java8EmptyReturnsTest {

    /**
     * Mock methods returning java 8 objects which can be empty.
     *
     * @param stream
     *            the stream
     * @param streamOfIntegers
     *            the stream of integers
     * @param streamOfLongs
     *            the stream of longs
     * @param streamOfDoubles
     *            the stream of doubles
     */
    @Test
    void mockMethodsReturningJava8ObjectsWhichCanBeEmpty(@Injectable Stream<?> stream,
            @Injectable Stream<Integer> streamOfIntegers, @Injectable Stream<Long> streamOfLongs,
            @Injectable Stream<Double> streamOfDoubles) {
        Optional<?> any = stream.findAny();
        assertSame(Optional.empty(), any);

        // Stream.empty() always creates a new stream, so it can't be used here.
        Stream<?> distinct = stream.distinct();
        assertFalse(distinct.iterator().hasNext());

        Spliterator<?> spliterator = stream.spliterator();
        assertSame(Spliterators.emptySpliterator(), spliterator);

        Spliterator<Integer> intSpliterator = streamOfIntegers.spliterator();
        assertEquals(0, intSpliterator.estimateSize());

        Spliterator<Long> longSpliterator = streamOfLongs.spliterator();
        assertEquals(0, longSpliterator.estimateSize());

        Spliterator<Double> doubleSpliterator = streamOfDoubles.spliterator();
        assertEquals(0, doubleSpliterator.estimateSize());

        assertSame(Collections.emptyIterator(), stream.iterator());
    }

    /**
     * Mock methods returning java 8 primitive specializations which can be empty.
     *
     * @param intStream
     *            the int stream
     * @param longStream
     *            the long stream
     * @param doubleStream
     *            the double stream
     */
    @Test
    void mockMethodsReturningJava8PrimitiveSpecializationsWhichCanBeEmpty(@Injectable IntStream intStream,
            @Injectable LongStream longStream, @Injectable DoubleStream doubleStream) {
        assertSame(OptionalInt.empty(), intStream.max());
        assertSame(OptionalLong.empty(), longStream.min());
        assertSame(OptionalDouble.empty(), doubleStream.findFirst());

        // noinspection RedundantStreamOptionalCall
        assertEquals(0, intStream.sorted().count());
        assertEquals(0, longStream.sequential().count());
        assertEquals(0, doubleStream.distinct().count());

        assertSame(Spliterators.emptyIntSpliterator(), intStream.spliterator());
        assertSame(Spliterators.emptyLongSpliterator(), longStream.spliterator());
        assertSame(Spliterators.emptyDoubleSpliterator(), doubleStream.spliterator());

        assertFalse(intStream.iterator().hasNext());
        assertFalse(longStream.iterator().hasNext());
        assertFalse(doubleStream.iterator().hasNext());
    }
}
