package net.cpollet.es.stores;

import net.cpollet.es.AsyncEventStore;
import net.cpollet.es.Event;
import net.cpollet.es.EventStore;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadPoolExecutor;

public class DefaultAsyncEventStore implements AsyncEventStore {
    private final EventStore eventStore;
    private final ExecutorService executorService;

    public DefaultAsyncEventStore(EventStore eventStore, ExecutorService executorService) {
        this.eventStore = eventStore;
        this.executorService = executorService;
    }

    public DefaultAsyncEventStore(EventStore eventStore) {
        this(eventStore, ForkJoinPool.commonPool());
    }

    @Override
    public CompletableFuture<Event> storeAsync(String aggregateId, Object payload) {
        return storeAsync(aggregateId, payload, null);
    }

    @Override
    public CompletableFuture<Event> storeAsync(String aggregateId, Object payload, Map<String, String> metadata) {
        CompletableFuture<Event> completableFuture = new CompletableFuture<>();

        executorService.submit((Runnable & CompletableFuture.AsynchronousCompletionTask) () -> {
            try {
                completableFuture.complete(eventStore.store(aggregateId, payload, metadata));
            } catch (Throwable e) {
                completableFuture.completeExceptionally(e);
            }
        });

        return completableFuture;
    }
}
