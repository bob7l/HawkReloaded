package uk.co.oliwali.HawkEye.database;

import org.bukkit.Bukkit;
import uk.co.oliwali.HawkEye.HawkEye;
import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.Util;

import java.sql.*;

/**
 * Handler for everything to do with the database.
 * All queries except searching goes through this class.
 *
 * @author oliverw92
 */

public class DataManager implements AutoCloseable {

    private final IdMapCache playerCache = new IdMapCache();

    private final IdMapCache worldCache = new IdMapCache();

    private DeleteManager deleteManager;

    private ConnectionManager connectionManager;

    private Consumer consumer;

    /**
     * Initiates database connection pool, checks tables, starts cleansing utility
     * Throws an exception if it is unable to complete setup
     *
     * @throws Exception
     */
    public DataManager() throws Exception {

        connectionManager = new ConnectionManager();

        //Check tables and update player/world lists
        createTables();

        if (Config.populateCachesOnBoot)
            populateCaches();

        consumer = new Consumer(this);

        deleteManager = new DeleteManager(connectionManager);

        Bukkit.getScheduler().runTaskTimerAsynchronously(HawkEye.getInstance(), consumer, Config.LogDelay * 20L, Config.LogDelay * 20L);

        Bukkit.getScheduler().runTaskTimerAsynchronously(HawkEye.getInstance(), deleteManager, 20 * 15, 20 * 5);

        //Start cleansing utility
        try {
            new CleanseUtil(connectionManager);
        } catch (Exception e) {
            Util.severe(e.getMessage());
            Util.severe("Unable to start cleansing utility - check your cleanse age");
        }
    }

    public DeleteManager getDeleteManager() {
        return deleteManager;
    }

    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public Consumer getConsumer() {
        return consumer;
    }

    /**
     * Get the player cache
     */
    public IdMapCache getPlayerCache() {
        return playerCache;
    }

    /**
     * Get the world cache
     */
    public IdMapCache getWorldCache() {
        return worldCache;
    }


    /**
     * Populates world and player local caches
     */
    private void populateCaches() throws Exception {
        try (Connection conn = connectionManager.getConnection();
             Statement stmnt = conn.createStatement()) {

            try (ResultSet res = stmnt.executeQuery("SELECT * FROM `" + Config.DbPlayerTable + "`;")) {
                while (res.next())
                    playerCache.put(res.getInt("player_id"), res.getString("player"));
            }

            try (ResultSet res = stmnt.executeQuery("SELECT * FROM `" + Config.DbWorldTable + "`;")) {
                while (res.next())
                    worldCache.put(res.getInt("world_id"), res.getString("world"));
            }
        }
    }

    /**
     * Updates a table based on params - Only use on mass changes
     */
    private void updateTables(String table, String columns, Statement stmnt, String sql) {
        try {
            stmnt.execute(sql);//This is where you create the table - use new + tablename!
            stmnt.execute("INSERT INTO `new" + table + "` (" + columns + ") SELECT " + columns + " FROM `" + table + "`;");
            stmnt.execute("RENAME TABLE `" + table + "` TO `old" + table + "`, `new" + table + "` TO `" + table + "`;");
            stmnt.execute("DROP TABLE `old" + table + "`;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets or creates an ID for the provided value
     *
     * @return The ID for the value, or -1 if the method failed
     */
    public int getKeyId(IdMapCache cache, String table, String idColumn, String column, String value) {
        Integer id = cache.get(value);

        if (id == null) {
            try (Connection conn = connectionManager.getConnection();
                 PreparedStatement stmnt = conn.prepareStatement("SELECT " + idColumn + " FROM " + table + " WHERE " + column + "=?")) {

                stmnt.setString(1, value);

                try (ResultSet rs = stmnt.executeQuery()) {

                    if (rs.next()) {
                        id = rs.getInt(1);

                        cache.put(id, value);

                    } else {

                        try (PreparedStatement stmnt2 = conn.prepareStatement("INSERT INTO " + Config.DbPlayerTable + " (" + column + ") " +
                                "VALUES (?) ON DUPLICATE KEY UPDATE " + column + "=VALUES(" + column + ");", Statement.RETURN_GENERATED_KEYS)) {

                            stmnt2.setString(1, value);

                            stmnt2.executeUpdate();

                            conn.commit();

                            try (ResultSet rs2 = stmnt2.getGeneratedKeys()) {

                                if (rs2.next()) {
                                    id = rs2.getInt(1);

                                    cache.put(id, value);
                                }
                            }
                        }
                    }
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return (id == null ? -1 : id);
    }

    /**
     * Checks that all tables are up to date and exist
     *
     * @return true on success, false on failure
     */
    private void createTables() throws Exception {

        try (Connection conn = connectionManager.getConnection();
             Statement stmnt = conn.createStatement()) {

            String playerTable = "CREATE TABLE IF NOT EXISTS `" + Config.DbPlayerTable + "` (" +
                    "`player_id` SMALLINT(6) UNSIGNED NOT NULL AUTO_INCREMENT, " +
                    "`player` varchar(40) CHARACTER SET latin1 COLLATE latin1_general_ci NOT NULL, " +
                    "PRIMARY KEY (`player_id`), " +
                    "UNIQUE KEY `player` (`player`)" +
                    ") COLLATE latin1_general_ci, ENGINE = INNODB;";

            String worldTable = "CREATE TABLE IF NOT EXISTS `" + Config.DbWorldTable + "` (" +
                    "`world_id` TINYINT(3) UNSIGNED NOT NULL AUTO_INCREMENT, " +
                    "`world` varchar(40) CHARACTER SET latin1 COLLATE latin1_general_ci NOT NULL, " +
                    "PRIMARY KEY (`world_id`), " +
                    "UNIQUE KEY `world` (`world`)" +
                    ") COLLATE latin1_general_ci, ENGINE = INNODB;";

            String dataTable = "CREATE TABLE `" + Config.DbHawkEyeTable + "` (" +
                    "`data_id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT," +
                    "`timestamp` datetime NOT NULL," +
                    "`player_id` SMALLINT(6) UNSIGNED NOT NULL," +
                    "`action` TINYINT(3) UNSIGNED NOT NULL," +
                    "`world_id` TINYINT(3) UNSIGNED NOT NULL," +
                    "`x` int(11) NOT NULL," +
                    "`y` int(11) NOT NULL," +
                    "`z` int(11) NOT NULL," +
                    "`data` varchar(500) CHARACTER SET latin1 COLLATE latin1_general_ci DEFAULT NULL," +
                    "PRIMARY KEY (`data_id`)," +
                    "KEY `timestamp` (`timestamp`)," +
                    "KEY `player` (`player_id`)," +
                    "KEY `action` (`action`)," +
                    "KEY `world_id` (`world_id`)," +
                    "KEY `x_y_z` (`x`,`y`,`z`)" +
                    ") COLLATE latin1_general_ci, ENGINE = INNODB;";

            DatabaseMetaData dbm = conn.getMetaData();

            //Check if tables exist
            if (!JDBCUtil.tableExists(dbm, Config.DbPlayerTable)) {
                Util.info("Table `" + Config.DbPlayerTable + "` not found, creating...");
                stmnt.execute(playerTable);
            }

            if (!JDBCUtil.tableExists(dbm, Config.DbWorldTable)) {
                Util.info("Table `" + Config.DbWorldTable + "` not found, creating...");
                stmnt.execute(worldTable);
            }

            if (!JDBCUtil.tableExists(dbm, Config.DbHawkEyeTable)) {
                Util.info("Table `" + Config.DbHawkEyeTable + "` not found, creating...");
                stmnt.execute(dataTable);
            }

            //This will print an error if the user does not have SUPER privilege
            try {
                stmnt.execute("SET GLOBAL innodb_flush_log_at_trx_commit = 2");
                stmnt.execute("SET GLOBAL sync_binlog = 0");
            } catch (Exception e) {
                Util.debug("HawkEye does not have enough privileges for setting global settings");
            }

            //Here is were the table alterations take place (Aside from alters from making tables)

            ResultSet rs = stmnt.executeQuery("SHOW FIELDS FROM `" + Config.DbHawkEyeTable + "` where Field ='action'");

            //Older hawkeye versions x = double, and contains the column "plugin"
            if (rs.next() && !rs.getString(2).contains("tinyint") || JDBCUtil.columnExists(dbm, Config.DbHawkEyeTable, "plugin")) {

                Util.info("Updating " + Config.DbPlayerTable + "...");

                updateTables(Config.DbPlayerTable, "`player_id`,`player`", stmnt, playerTable.replace(Config.DbPlayerTable, "new" + Config.DbPlayerTable));


                Util.info("Updating " + Config.DbWorldTable + "...");

                updateTables(Config.DbWorldTable, "`world_id`,`world`", stmnt, worldTable.replace(Config.DbWorldTable, "new" + Config.DbWorldTable));

                Util.info("Updating " + Config.DbHawkEyeTable + "...");

                updateTables(Config.DbHawkEyeTable, "`data_id`,`timestamp`,`player_id`,`action`,`world_id`,`x`,`y`,`z`,`data`", stmnt,
                        dataTable.replace(Config.DbHawkEyeTable, "new" + Config.DbHawkEyeTable));

                Util.info("Finished!");

            }

            conn.commit();
        }
    }

    /**
     * Closes down all connections
     */
    public void close() throws Exception {
        if (connectionManager != null) {
            consumer.close();
            connectionManager.close();
        }
    }
}
