package uk.co.oliwali.HawkEye.database;

import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.Util;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Manages the MySQL connection pool.
 * By default 10 connections are maintained at a time
 *
 * @author oliverw92
 */
public class ConnectionManager implements Closeable {

    private static final long TTL = 300000;

    private static int poolsize = 10;

    private static List<JDCConnection> connections;

    private final String url;
    private final Properties prop;

    /**
     * Creates the connection manager and starts the reaper
     *
     * @param url      url of the database
     * @param user     username to use
     * @param password password for the database
     * @throws ClassNotFoundException
     */
    public ConnectionManager(String url, String user, String password) throws ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        Util.debug("Attempting to connecting to database at: " + url);
        this.url = url;

        this.prop = new Properties();

        prop.put("user", user);
        prop.put("password", password);
        prop.put("rewriteBatchedStatements", "true");
        prop.put("prepStmtCacheSize", "275");
        prop.put("prepStmtCacheSqlLimit", "2048");
        prop.put("cachePrepStmts", "true");

        poolsize = Config.PoolSize;

        connections = Collections.synchronizedList(new ArrayList<JDCConnection>(poolsize));

        new ConnectionReaper().start();
    }

    /**
     * Closes all connections
     */
    @Override
    public synchronized void close() {
        Util.debug("Closing all MySQL connections");

        for (JDCConnection conn : connections) {
            conn.terminate();
        }

        connections.clear();
    }

    /**
     * Returns a connection from the pool
     *
     * @return returns a {JDCConnection}
     * @throws SQLException
     */
    public synchronized JDCConnection getConnection() throws SQLException {
        JDCConnection conn;

        for (JDCConnection connection : connections) {
            if (connection.lease()) {
                return connection;
            }
        }

        Util.debug("No available MySQL connections, attempting to create new one");
        conn = new JDCConnection(DriverManager.getConnection(url, prop));
        conn.lease();

        if (!conn.isValid()) {
            conn.terminate();
            throw new SQLException("Could not create new connection");
        }

        connections.add(conn);

        return conn;
    }

    /**
     * Removes a connection from the pool
     *
     * @param {JDCConnection} to remove
     */
    public static synchronized void removeConn(Connection conn) {
        connections.remove(conn);
    }

    public static boolean areConsOpen() {

        for (JDCConnection conn : connections) {
            if (conn.inUse()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Loops through connections, reaping old ones
     */
    private synchronized void reapConnections() {
        Util.debug("Attempting to reap dead connections");
        final long stale = System.currentTimeMillis() - TTL;
        int count = 0;
        int i = 1;
        for (JDCConnection conn : connections) {

            if (conn.inUse() && stale > conn.getLastUse() && !conn.isValid()) {
                connections.remove(conn);
                count++;
            }

            if (i > poolsize) {
                connections.remove(conn);
                count++;
                conn.terminate();
            }
            i++;
        }
        Util.debug(count + " connections reaped");
    }

    /**
     * Reaps connections
     *
     * @author oliverw92
     */
    private class ConnectionReaper extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(300000);
                } catch (final InterruptedException e) {
                }
                reapConnections();
            }
        }
    }

    public static List<JDCConnection> getConnections() {
        return connections;
    }
}
