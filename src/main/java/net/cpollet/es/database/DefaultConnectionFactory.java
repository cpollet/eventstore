package net.cpollet.es.database;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DefaultConnectionFactory implements ConnectionFactory {
    private final DataSource dataSource;

    public DefaultConnectionFactory(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
