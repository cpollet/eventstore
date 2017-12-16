package net.cpollet.es;

import java.util.Map;

/**
 * https://stackoverflow.com/questions/12530310/structure-of-a-cqrs-event-store
 * https://cqrs.wordpress.com/documents/building-event-storage/
 * https://github.com/Ookami86/event-sourcing-in-practice
 */
public interface EventStore {
    Event store(String aggregateId, Object payload) throws EventNotStoredException;

    Event store(String aggregateId, Object payload, Map<String, String> metadata) throws EventNotStoredException;
}
