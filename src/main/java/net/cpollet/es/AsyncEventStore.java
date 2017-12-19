package net.cpollet.es;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public interface AsyncEventStore {
    CompletableFuture<StorageResult> store(String aggregateId, Object payload);

    CompletableFuture<StorageResult> store(String aggregateId, Object payload, Map<String, String> metadata);
}
