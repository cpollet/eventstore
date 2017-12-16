package net.cpollet.es.database;

import java.sql.Connection;

public abstract class Transactional<T> {
    private final Connection connection;
    private final int isolationLevel;

    Transactional(Connection connection, int isolationLevel) {
        this.connection = connection;
        this.isolationLevel = isolationLevel;
    }

    T executeInTransaction() throws Exception {
        try {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(isolationLevel);

            T result = execute();

            connection.commit();

            return result;
        } catch (Exception e) {
            connection.rollback();
            throw e;
        }
    }

    public abstract T execute() throws Exception;
}
