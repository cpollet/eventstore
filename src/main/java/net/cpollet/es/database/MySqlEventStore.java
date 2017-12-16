package net.cpollet.es.database;

import net.cpollet.es.Event;
import net.cpollet.es.EventStore;
import net.cpollet.es.data.Serializer;
import net.cpollet.es.utils.ClassUtils;
import net.cpollet.es.EventNotStoredException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;


public class MySqlEventStore implements EventStore {
    private final ConnectionPool connectionPool;
    private final Serializer serializer;

    public MySqlEventStore(ConnectionPool connectionPool, Serializer serializer) {
        this.connectionPool = connectionPool;
        this.serializer = serializer;
    }

    @Override
    public Event store(String aggregateId, Object payload) throws EventNotStoredException {
        return store(aggregateId, payload, null);
    }

    @Override
    public Event store(String aggregateId, Object payload, Map<String, String> metadata) throws EventNotStoredException {
        try (Connection c = connectionPool.getConnection()) {
            return new Transactional<Event>(c, Connection.TRANSACTION_SERIALIZABLE) {
                @Override
                public Event execute() throws Exception {
                    String eventId = persist(c, aggregateId, payload, metadata);
                    return fetch(c, eventId);
                }
            }.executeInTransaction();
        } catch (EventNotStoredException e) {
            throw e;
        } catch (Exception e) {
            throw new EventNotStoredException(e);
        }
    }

    private String persist(Connection c, String aggregateId, Object payload, Map<String, String> metadata) throws EventNotStoredException {
        String eventId = UUID.randomUUID().toString();
        String eventType = ClassUtils.getCanonicalBinaryName(payload.getClass());

        try (PreparedStatement stmt = c.prepareStatement(
                "insert into events (id, type, aggregate_id, metadata, payload) values (?, ?, ?, ?, ?)")) {

            stmt.setString(1, eventId);
            stmt.setString(2, eventType);
            stmt.setString(3, aggregateId);
            stmt.setString(4, serializer.serialize(metadata));
            stmt.setString(5, serializer.serialize(payload));

            if (stmt.executeUpdate() != 1) {
                throw new EventNotStoredException();
            }

            return eventId;
        } catch (SQLException e) {
            throw new EventNotStoredException(e);
        }
    }


    private Event fetch(Connection c, String eventId) throws EventNotStoredException {
        try (PreparedStatement stmt = c.prepareStatement(
                "select id, type, timestamp, version, aggregate_id, metadata, payload from events where id = ?")) {

            stmt.setString(1, eventId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new EventNotStoredException();
                }

                String id = rs.getString(1);
                String type = rs.getString(2);
                long timestamp = rs.getTimestamp(3).getTime();
                long version = rs.getLong(4);
                String aggregateId = rs.getString(5);
                Map<String, String> metadata = serializer.deserialize(rs.getString(6), "java.util.HashMap");
                Object payload = serializer.deserialize(rs.getString(7), type);

                return new Event(id, type, timestamp, version, aggregateId, metadata, payload);
            } catch (ClassNotFoundException e) {
                throw new EventNotStoredException(e);
            }
        } catch (SQLException e) {
            throw new EventNotStoredException(e);
        }
    }
}
