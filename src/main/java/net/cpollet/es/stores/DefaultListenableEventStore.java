package net.cpollet.es.stores;

import net.cpollet.es.Event;
import net.cpollet.es.EventNotStoredException;
import net.cpollet.es.EventStore;
import net.cpollet.es.ListenableEventStore;
import net.cpollet.es.Listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DefaultListenableEventStore implements ListenableEventStore {
    private final EventStore eventStore;
    private final List<Listener> listeners;

    public DefaultListenableEventStore(EventStore eventStore, Listener... listeners) {
        this(eventStore);
        this.listeners.addAll(Arrays.asList(listeners));
    }

    public DefaultListenableEventStore(EventStore eventStore) {
        this.eventStore = eventStore;
        listeners = new ArrayList<>();
    }

    @Override
    public ListenableEventStore addListener(Listener listener) {
        listeners.add(listener);
        return this;
    }

    @Override
    public Event store(String aggregateId, Object payload) throws EventNotStoredException {
        return store(aggregateId, payload, null);
    }

    @Override
    public Event store(String aggregateId, Object payload, Map<String, String> metadata) throws EventNotStoredException {
        Event event = eventStore.store(aggregateId, payload, metadata);

        listeners.forEach(l -> l.onEventPersisted(event));

        return event;
    }
}
