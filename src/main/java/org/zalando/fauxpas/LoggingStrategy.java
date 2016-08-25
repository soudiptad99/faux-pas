package org.zalando.fauxpas;

import org.slf4j.Logger;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

// TODO document default values (null and false)
final class LoggingStrategy implements Strategy {

    private final Logger logger;

    LoggingStrategy(final Logger logger) {
        this.logger = logger;
    }

    @Override
    public <X extends Throwable> Runnable adapt(final ThrowingRunnable<X> runnable) {
        return () -> {
            try {
                runnable.tryRun();
            } catch (final Throwable e) {
                handle(runnable, e);
            }
        };
    }

    @Override
    public <T, X extends Throwable> Supplier<T> adapt(final ThrowingSupplier<T, X> supplier) {
        return () -> {
            try {
                return supplier.tryGet();
            } catch (final Throwable e) {
                handle(supplier, e);
                return null;
            }
        };
    }

    @Override
    public <T, X extends Throwable> Consumer<T> adapt(final ThrowingConsumer<T, X> consumer) {
        return t -> {
            try {
                consumer.tryAccept(t);
            } catch (final Throwable e) {
                handle(consumer, e);
            }
        };
    }

    @Override
    public <T, R, X extends Throwable> Function<T, R> adapt(final ThrowingFunction<T, R, X> function) {
        return t -> {
            try {
                return function.tryApply(t);
            } catch (final Throwable e) {
                handle(function, e);
                return null;
            }
        };
    }

    @Override
    public <T, X extends Throwable> Predicate<T> adapt(final ThrowingPredicate<T, X> predicate) {
        return t -> {
            try {
                return predicate.tryTest(t);
            } catch (final Throwable e) {
                handle(predicate, e);
                return false;
            }
        };
    }

    @Override
    public <T, U, X extends Throwable> BiConsumer<T, U> adapt(final ThrowingBiConsumer<T, U, X> consumer) {
        return (t, u) -> {
            try {
                consumer.tryAccept(t, u);
            } catch (final Throwable e) {
                handle(consumer, e);
            }
        };
    }

    @Override
    public <T, U, R, X extends Throwable> BiFunction<T, U, R> adapt(final ThrowingBiFunction<T, U, R, X> function) {
        return (t, u) -> {
            try {
                return function.tryApply(t, u);
            } catch (final Throwable e) {
                handle(function, e);
                return null;
            }
        };
    }

    @Override
    public <T, U, X extends Throwable> BiPredicate<T, U> adapt(final ThrowingBiPredicate<T, U, X> predicate) {
        return (t, u) -> {
            try {
                return predicate.tryTest(t, u);
            } catch (final Throwable e) {
                handle(predicate, e);
                return false;
            }
        };
    }

    private void handle(final Object source, final Throwable throwable) {
        logger.error("Exception in {}", source, throwable);
    }

}