package net.cpollet.es.stores.fakes;

import net.cpollet.es.Event;
import net.cpollet.es.EventNotStoredException;
import net.cpollet.es.EventStore;

import java.util.Map;

public class FailingEventStore implements EventStore {
    private final EventNotStoredException exception;

    public FailingEventStore(EventNotStoredException exception) {
        this.exception = exception;
    }

    @Override
    public Event store(String aggregateId, Object payload) throws EventNotStoredException {
        throw exception;
    }

    @Override
    public Event store(String aggregateId, Object payload, Map<String, String> metadata) throws EventNotStoredException {
        throw exception;
    }
}
