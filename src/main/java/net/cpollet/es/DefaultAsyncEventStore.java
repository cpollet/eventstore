package net.cpollet.es;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class DefaultAsyncEventStore implements AsyncEventStore {
    private final EventStore eventStore;
    private final ExecutorService executorService;

    public DefaultAsyncEventStore(EventStore eventStore, ExecutorService executorService) {
        this.eventStore = eventStore;
        this.executorService = executorService;
    }

    @Override
    public Future<StorageResult> store(String aggregateId, Object payload) {
        return store(aggregateId, payload, null);
    }

    @Override
    public Future<StorageResult> store(String aggregateId, Object payload, Map<String, String> metadata) {
        return executorService.submit(() -> {
            try {
                return StorageResult.success(eventStore.store(aggregateId, payload, metadata));
            } catch (Exception e) {
                return StorageResult.failure(e);
            }
        });
    }
}
