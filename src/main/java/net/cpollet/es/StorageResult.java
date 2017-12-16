package net.cpollet.es;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class StorageResult {
    private final Event event;
    private final Exception exception;

    static StorageResult success(Event event) {
        return new StorageResult(event, null);
    }

    static StorageResult failure(Exception exception) {
        return new StorageResult(null, exception);
    }

    public boolean isSuccessful() {
        return event != null;
    }
}
