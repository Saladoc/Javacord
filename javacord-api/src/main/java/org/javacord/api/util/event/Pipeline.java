package org.javacord.api.util.event;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A pipeline for fluent consumption of (mostly) events and elements obtained from them.
 *
 * @param <E> The element type of the pipeline.
 */
public interface Pipeline<E> {

    /**
     * Filter the pipeline.
     *
     * <p>Only events for which {@code condition} returns {@code true} will be passed onward.
     *
     * @param condition The condition to check against.
     * @return The filtered pipeline.
     */
    Pipeline<E> filter(Predicate<? super E> condition);

    /**
     * Filter the pipeline.
     *
     * <p>Only events for which {@code condition} returns {@code false} will be passed onward.
     *
     * @param condition The condition to check against.
     * @return The filtered pipeline.
     */
    default Pipeline<E> filterNot(Predicate<? super E> condition) {
        return filter(condition.negate());
    }

    /**
     * Filter the pipeline based on an attribute of the element.
     *
     * <p>For example when filtering {@code MessageCreateEvent}s, a way to filter for messages created by a user would
     * be {@code pipeline.filterAttributePresent(event -> event.getMessageAuthor.asUser())}.
     *
     * @param getter The function to obtain the attribute.
     * @return The filtered pipeline.
     */
    default Pipeline<E> filterAttributePresent(Function<? super E, Optional<?>> getter) {
        return filter(event -> getter.apply(event).isPresent());
    }

    /**
     * Filter the pipeline based on an attribute of the element.
     *
     * <p>For example when filtering {@code MessageCreateEvent}s, a way to filter for messages created by a server
     * admin would be
     * {@code `pipeline.filterAttribute(MessageCreateEvent::getMessageAuthor, MessageAuthor::isServerAdmin)}.
     *
     * @param getter The function to obtain the attribute.
     * @param condition The condition the attribute must satisfy
     * @param <T> The type of the attribute.
     * @return The filtered pipeline.
     */
    default <T> Pipeline<E> filterAttribute(Function<? super E, T> getter, Predicate<T> condition) {
        return filter(event -> condition.test(getter.apply(event)));
    }

    /**
     * Filter the pipeline based on an attribute of the element.
     *
     * <p>For example when filtering {@code MessageCreateEvent}s, a way to filter for messages not created by this user
     * would be {@code `pipeline.filterAttributeNot(MessageCreateEvent::getMessageAuthor, MessageAuthor::isYourself)}.
     *
     * @param getter The function to obtain the attribute.
     * @param condition The condition the attribute must satisfy
     * @param <T> The type of the attribute.
     * @return The filtered pipeline.
     */
    default <T> Pipeline<E> filterAttributeNot(Function<? super E, T> getter, Predicate<T> condition) {
        return filter(event -> condition.negate().test(getter.apply(event)));
    }

    /**
     * Map the element of the pipeline.
     *
     * @param function The mapping function.
     * @param <T> The type of the mapping target.
     * @return The mapped pipeline.
     */
    <T> Pipeline<T> map(Function<? super E, T> function);

    /**
     * Map the element of the pipeline and filter for present mappings.
     *
     * <p>The following expressions are equivalent: <br>
     * <code>
     *     pipeline.map(foo -&gt; foo.getOptionalBar())<br>
     *     &nbsp;&nbsp;&nbsp;&nbsp;.filter(Optional::isPresent)<br>
     *     &nbsp;&nbsp;&nbsp;&nbsp;.map(Optional::get)
     * </code> <br>
     *  and <br>
     * <code>pipeline.flatMap(foo -&gt; foo.getOptionalBar())</code>
     *
     * @param function The mapping function.
     * @param <T> The type of the mapping target.
     * @return The mapped and filtered pipeline.
     */
    <T> Pipeline<T> flatMap(Function<? super E, Optional<T>> function);


    /**
     * Consume the element.
     *
     * <p>This ends the pipeline as the element will be consumed.
     *
     * @param consumer The consumer for the events.
     */
    void consume(Consumer<? super E> consumer);
}
