package org.javacord.core.util.event;

import org.javacord.api.util.event.Pipeline;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class PipelineImpl<I, O> implements Pipeline<O>, Consumer<I> {

    /**
     * Create a starting point for a pipeline.
     *
     * <p>This creates a no-op chain element to start the chain, so
     * @param <I> The type the Pipeline accepts.
     * @return A pipeline to start a chain with.
     */
    public static <I> PipelineImpl<I,I> start() {
        return mapping(Function.identity());
    }

    /**
     * Creates a pipeline filtering for a condition.
     *
     * @param condition The condition to filter for.
     * @param <T> The type the pipeline accepts and outputs.
     * @return The filtering pipeline.
     */
    public static <T> PipelineImpl<T, T> filtering(Predicate<? super T> condition) {
        return new FilteringPipe<>(condition);
    }

    /**
     * Creates a pipeline mapping its input.
     *
     * @param function The mapping function.
     * @param <I> The input type.
     * @param <O> The output type.
     * @return The mapping pipeline.
     */
    public static <I, O> PipelineImpl<I, O> mapping(Function<? super I, O> function) {
        return new MappingPipe<>(function);
    }

    /**
     * Creates a pipeline flat mapping its input.
     *
     * <p>This is analogous to {@link Optional#flatMap(Function)}
     * @param function The mapping function.
     * @param <I> The input type.
     * @param <O> The output type.
     * @return The mapping pipeline.
     */
    public static <I, O> PipelineImpl<I, O> flatMapping(Function<? super I, Optional<O>> function) {
        return new FlatMappingPipe<>(function);
    }

    protected final List<Consumer<? super O>> sinks = new CopyOnWriteArrayList<>();

    @Override
    public Pipeline<O> filter(Predicate<? super O> condition) {
        PipelineImpl<O, O> sink = PipelineImpl.filtering(condition);
        sinks.add(sink);
        return sink;
    }

    @Override
    public <T> Pipeline<T> map(Function<? super O, T> function) {
        PipelineImpl<O,T> sink = PipelineImpl.mapping(function);
        sinks.add(sink);
        return sink;
    }


    @Override
    public <T> Pipeline<T> flatMap(Function<? super O, Optional<T>> function) {
        PipelineImpl<O,T> sink = PipelineImpl.flatMapping(function);
        sinks.add(sink);
        return sink;
    }


    @Override
    public void consume(Consumer<? super O> consumer) {
        sinks.add(consumer);
    }


    private static class FilteringPipe<T> extends PipelineImpl<T,T> {

        private final Predicate<? super T> condition;

        private FilteringPipe(Predicate<? super T> condition) {
            this.condition = condition;
        }

        @Override
        public void accept(T input) {
            if (condition.test(input)) {
                sinks.forEach(sink -> sink.accept(input));
            }
        }
    }

    private static class MappingPipe<I, O> extends PipelineImpl<I, O> {

        private final Function<? super I, O> function;

        private MappingPipe(Function<? super I, O> function) {
            this.function = function;
        }

        @Override
        public void accept(I input) {
            O output = function.apply(input);
            sinks.forEach(sink -> sink.accept(output));
        }
    }

    private static class FlatMappingPipe<I,O> extends PipelineImpl<I, O> {

        private final Function<? super I, Optional<O>> function;

        private FlatMappingPipe(Function<? super I, Optional<O>> function) {
            this.function = function;
        }

        @Override
        public void accept(I input) {
            Optional<O> potentialOutput = function.apply(input);
            if (potentialOutput.isPresent()) {
                O output = potentialOutput.get();
                sinks.forEach(sink -> sink.accept(output));
            }
        }
    }
}
