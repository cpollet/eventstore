package net.cpollet.es;

import java.util.Map;
import java.util.concurrent.Future;

public interface AsyncEventStore {
    Future<StorageResult> store(String aggregateId, Object payload);

    Future<StorageResult> store(String aggregateId, Object payload, Map<String, String> metadata);
}
