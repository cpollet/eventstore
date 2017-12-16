package net.cpollet.es;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
@Getter
public class Event {
    private final String id;

    private final String type;

    private final long timestamp;

    private final long version;

    private final String aggregateId;

    private final Map<String, String> metadata;

    private final Object payload;
}
