package net.cpollet.es;

public interface ListenableEventStore extends EventStore {
    ListenableEventStore addListener(Listener listener);
}
