package net.cpollet.es;

public class EventNotStoredException extends Exception {
    public EventNotStoredException() {
        super();
    }

    public EventNotStoredException(Exception e) {
        super(e);
    }
}
