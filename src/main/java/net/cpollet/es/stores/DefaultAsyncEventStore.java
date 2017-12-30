package net.cpollet.es.stores;

import net.cpollet.es.AsyncEventStore;
import net.cpollet.es.EventStore;
import net.cpollet.es.StorageResult;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class DefaultAsyncEventStore implements AsyncEventStore {
    private final EventStore eventStore;
    private final ExecutorService executorService;

    public DefaultAsyncEventStore(EventStore eventStore, ExecutorService executorService) {
        this.eventStore = eventStore;
        this.executorService = executorService;
    }

    @Override
    public CompletableFuture<StorageResult> store(String aggregateId, Object payload) {
        return store(aggregateId, payload, null);
    }

    @Override
    public CompletableFuture<StorageResult> store(String aggregateId, Object payload, Map<String, String> metadata) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return StorageResult.success(eventStore.store(aggregateId, payload, metadata));
            } catch (Exception e) {
                return StorageResult.failure(e);
            }
        }, executorService);
    }
}
