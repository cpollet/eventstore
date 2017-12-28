package net.cpollet.es.stores.fakes;

import net.cpollet.es.Event;
import net.cpollet.es.EventNotStoredException;
import net.cpollet.es.EventStore;

import java.util.Map;

public class SucceedingEventStore implements EventStore {
    private final Event event;

    public SucceedingEventStore(Event event) {
        this.event = event;
    }

    @Override
    public Event store(String aggregateId, Object payload) throws EventNotStoredException {
        return event;
    }

    @Override
    public Event store(String aggregateId, Object payload, Map<String, String> metadata) throws EventNotStoredException {
        return event;
    }
}
