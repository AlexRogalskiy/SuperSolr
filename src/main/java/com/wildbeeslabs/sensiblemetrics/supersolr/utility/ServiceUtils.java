package com.wildbeeslabs.sensiblemetrics.supersolr.utility;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

/**
 * Service utilities implementation
 */
@Slf4j
@UtilityClass
public class ServiceUtils {

    private static final BiConsumer<? super Object, ? super Throwable> DEFAULT_COMPLETABLE_ACTION = (response, error) -> {
        try {
            if (Objects.isNull(error)) {
                log.debug("Received completable future response [from={}]", response);
            } else {
                log.debug("Canceled completable future request [response={}, error={}]", response, error);
            }
        } catch (RuntimeException | Error e) {
            log.error("ERROR: cannot process completable future request callback", e);
        }
    };

    public static <T> void getResultAsync(final Executor executor, @NonNull final CompletableFuture<T>... future) {
        CompletableFuture.allOf(future).whenCompleteAsync(DEFAULT_COMPLETABLE_ACTION, executor).join();
    }

    public static <T> T getResultAsync(final Executor executor, @NonNull final CompletableFuture<T> future) {
        return future.whenCompleteAsync(DEFAULT_COMPLETABLE_ACTION, executor).join();
    }

    public static <T> T getResultAsync(@NonNull final CompletableFuture<T> future) {
        return getResultAsync(Executors.newSingleThreadExecutor(), future);
    }
}
