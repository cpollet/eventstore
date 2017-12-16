package net.cpollet.es.database;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class TomcatConnectionPool implements ConnectionPool {
    private final DataSource dataSource;

    public TomcatConnectionPool(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
