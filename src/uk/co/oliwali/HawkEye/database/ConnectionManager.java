package uk.co.oliwali.HawkEye.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.Util;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Controls MySQL connection pool using Hikari
 *
 * @author bob7l
 */
public class ConnectionManager implements AutoCloseable {

    private HikariDataSource connectionPool;

    public ConnectionManager() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");

        Util.debug("Attempting to connecting to database...");

        HikariConfig config = new HikariConfig();

        config.setMaximumPoolSize(Config.PoolSize);

        config.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");

        config.setUsername(Config.DbUser);
        config.setPassword(Config.DbPassword);

        config.addDataSourceProperty("databaseName", Config.DbDatabase);
        config.addDataSourceProperty("port", Config.DbPort);

        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "275");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("cachePrepStmts", "true");

        config.setAutoCommit(false);

        connectionPool = new HikariDataSource(config);
    }

    @Override
    public void close() throws Exception {
        if (connectionPool != null) {
            connectionPool.close();
        }
    }

    public HikariDataSource getConnectionPool() {
        return connectionPool;
    }

    public Connection getConnection() throws SQLException {
        return connectionPool.getConnection();
    }

}
