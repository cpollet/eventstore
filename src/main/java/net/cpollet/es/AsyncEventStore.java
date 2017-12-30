package net.cpollet.es;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface AsyncEventStore {
    CompletableFuture<Event> storeAsync(String aggregateId, Object payload);

    CompletableFuture<Event> storeAsync(String aggregateId, Object payload, Map<String, String> metadata);
}
