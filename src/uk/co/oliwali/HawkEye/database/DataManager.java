package uk.co.oliwali.HawkEye.database;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingQueue;

import org.bukkit.scheduler.BukkitTask;

import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.HawkEye;
import uk.co.oliwali.HawkEye.entry.DataEntry;
import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.Util;

/**
 * Handler for everything to do with the database.
 * All queries except searching goes through this class.
 * @author oliverw92
 */

public class DataManager implements Runnable {

	private static final LinkedBlockingQueue<DataEntry> queue = new LinkedBlockingQueue<DataEntry>();
	private static ConnectionManager connections;
	public static BukkitTask cleanseTimer = null;
	public static final HashMap<String, Integer> dbPlayers = new HashMap<String, Integer>();
	public static final HashMap<String, Integer> dbWorlds = new HashMap<String, Integer>();

	private boolean threadbusy = false;
	
	/**
	 * Initiates database connection pool, checks tables, starts cleansing utility
	 * Throws an exception if it is unable to complete setup
	 * @param instance
	 * @throws Exception
	 */
	public DataManager(HawkEye instance) throws Exception {

		connections = new ConnectionManager(Config.DbUrl, Config.DbUser, Config.DbPassword);
		getConnection().close();
		//Check tables and update player/world lists
		if (!checkTables())
			throw new Exception();
		if (!updateDbLists())
			throw new Exception();

		//Start cleansing utility
		try {
			new CleanseUtil(instance);
		} catch (Exception e) {
			Util.severe(e.getMessage());
			Util.severe("Unable to start cleansing utility - check your cleanse age");
		}
	}

	/**
	 * Returns current queue
	 */
	public static LinkedBlockingQueue<DataEntry> getQueue() {
		return queue;
	}
	
	/**
	 * Closes down all connections
	 */
	public static void close() {
		connections.close();
		if (cleanseTimer != null) cleanseTimer.cancel();
	}

	/**
	 * Adds a {@link DataEntry} to the database queue.
	 * {Rule}s are checked at this point
	 * @param entry {@link DataEntry} to be added
	 * @return
	 */
	public static void addEntry(DataEntry entry) {

		if (!entry.getType().isLogged()) return;

		if (Config.IgnoreWorlds.contains(entry.getWorld())) return;

		queue.add(entry);
	}

	/**
	 * Retrieves an entry from the database
	 * @param id id of entry to return
	 * @return
	 */
	public static DataEntry getEntry(int id) {
		JDCConnection conn = null;
		try {
			conn = getConnection();
			ResultSet res = conn.createStatement().executeQuery("SELECT * FROM `" + Config.DbHawkEyeTable + "` WHERE `data_id` = " + id);
			res.next();
			return createEntryFromRes(res);
		} catch (Exception ex) {
			Util.severe("Unable to retrieve data entry from MySQL Server: " + ex);
		} finally {
			conn.close();
		}
		return null;
	}

	/**
	 * Deletes an entry from the database
	 * @param dataid id to delete
	 */
	public static void deleteEntry(int dataid) {
		Thread thread = new Thread(new DeleteEntry(dataid));
		thread.start();
	}
	public static void deleteEntries(List<?> entries) {
		Thread thread = new Thread(new DeleteEntry(entries));
		thread.start();
	}

	/**
	 * Get a players name from the database player list
	 * @param id
	 * @return player name
	 */
	public static String getPlayer(int id) {
		for (Entry<String, Integer> entry : dbPlayers.entrySet())
			if (entry.getValue() == id)
				return entry.getKey();
				return null;
	}

	/**
	 * Get a world name from the database world list
	 * @param id
	 * @return world name
	 */
	public static String getWorld(int id) {
		for (Entry<String, Integer> entry : dbWorlds.entrySet())
			if (entry.getValue() == id)
				return entry.getKey();
				return null;
	}

	/**
	 * Returns a database connection from the pool
	 * @return {JDCConnection}
	 */
	public static JDCConnection getConnection() {
		try {
			return connections.getConnection();
		} catch (final SQLException ex) {
			Util.severe("Error whilst attempting to get connection: " + ex);
			return null;
		}
	}

	/**
	 * Creates a {@link DataEntry} from the inputted {ResultSet}
	 * @param res
	 * @return returns a {@link DataEntry}
	 * @throws SQLException
	 */
	public static DataEntry createEntryFromRes(ResultSet res) throws Exception {
		DataType type = DataType.fromId(res.getInt(4));
		return (DataEntry)type.getEntryConstructor().newInstance(res.getInt(3), res.getTimestamp(2), res.getInt(1), res.getInt(4), res.getString(9), res.getInt(5), res.getInt(6), res.getInt(7), res.getInt(8));
	}

	/**
	 * Adds a player to the database
	 */
	private boolean addPlayer(String name) {
		JDCConnection conn = null;
		try {
			Util.debug("Attempting to add player '" + name + "' to database");
			conn = getConnection();
			//Instead of ignoring a dup'd key, we update the entry. Ignore is a very bad idea!
			conn.createStatement().execute("INSERT INTO `" + Config.DbPlayerTable + "` (player) VALUES ('" + name + "') ON DUPLICATE KEY UPDATE player='" + name + "';");
		} catch (SQLException ex) {
			Util.severe("Unable to add player to database: " + ex);
			return false;
		} finally {
			conn.close();
		}
		if (!updateDbLists())
			return false;
		return true;
	}

	/**
	 * Adds a world to the database
	 */
	private boolean addWorld(String name) {
		JDCConnection conn = null;
		try {
			Util.debug("Attempting to add world '" + name + "' to database");
			conn = getConnection();
			conn.createStatement().execute("INSERT IGNORE INTO `" + Config.DbWorldTable + "` (world) VALUES ('" + name + "');");
		} catch (SQLException ex) {
			Util.severe("Unable to add world to database: " + ex);
			return false;
		} finally {
			conn.close();
		}
		if (!updateDbLists())
			return false;
		return true;
	}

	/**
	 * Updates world and player local lists
	 * @return true on success, false on failure
	 */
	private boolean updateDbLists() {
		JDCConnection conn = null;
		Statement stmnt = null;
		try {
			conn = getConnection();
			stmnt = conn.createStatement();
			ResultSet res = stmnt.executeQuery("SELECT * FROM `" + Config.DbPlayerTable + "`;");
			while (res.next())
				dbPlayers.put(res.getString("player"), res.getInt("player_id"));
			res = stmnt.executeQuery("SELECT * FROM `" + Config.DbWorldTable + "`;");
			while (res.next())
				dbWorlds.put(res.getString("world"), res.getInt("world_id"));
		} catch (SQLException ex) {
			Util.severe("Unable to update local data lists from database: " + ex);
			return false;
		} finally {
			try {
				if (stmnt != null)
					stmnt.close();
				conn.close();
			} catch (SQLException ex) {
				Util.severe("Unable to close SQL connection: " + ex);
			}

		}
		return true;
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
	 * Checks that all tables are up to date and exist
	 * @return true on success, false on failure
	 */
	private boolean checkTables() {

		JDCConnection conn = null;
		Statement stmnt = null;
		try {
			conn = getConnection();
			stmnt = conn.createStatement();
			DatabaseMetaData dbm = conn.getMetaData();

			//Check if tables exist
			if (!JDBCUtil.tableExists(dbm, Config.DbPlayerTable)) {
				Util.info("Table `" + Config.DbPlayerTable + "` not found, creating...");
				stmnt.execute("CREATE TABLE IF NOT EXISTS `" + Config.DbPlayerTable + "` (" +
						"`player_id` int(6) NOT NULL AUTO_INCREMENT, " +
						"`player` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL, " +
						"PRIMARY KEY (`player_id`), " +
						"UNIQUE KEY `player` (`player`)" +
						") COLLATE utf8_general_ci, ENGINE = INNODB;");
			}
			
			if (!JDBCUtil.tableExists(dbm, Config.DbWorldTable)) {
				Util.info("Table `" + Config.DbWorldTable + "` not found, creating...");
				stmnt.execute("CREATE TABLE IF NOT EXISTS `" + Config.DbWorldTable + "` (" +
								"`world_id` int(3) NOT NULL AUTO_INCREMENT, " +
								"`world` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL, " +
								"PRIMARY KEY (`world_id`), " +
								"UNIQUE KEY `world` (`world`)" +
								") COLLATE utf8_general_ci, ENGINE = INNODB;");
			}
				
			if (!JDBCUtil.tableExists(dbm, Config.DbHawkEyeTable)) {
				Util.info("Table `" + Config.DbHawkEyeTable + "` not found, creating...");
				stmnt.execute("CREATE TABLE `" + Config.DbHawkEyeTable + "` (" +
								  "`data_id` int(10) NOT NULL AUTO_INCREMENT," +
								  "`timestamp` datetime NOT NULL," +
								  "`player_id` int(6) NOT NULL," +
								  "`action` int(3) NOT NULL," +
								  "`world_id` int(3) NOT NULL," +
								  "`x` int(11) NOT NULL," +
								  "`y` int(11) NOT NULL," +
								  "`z` int(11) NOT NULL," +
								  "`data` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL," +
								  "PRIMARY KEY (`data_id`)," +
								  "KEY `timestamp` (`timestamp`)," +
								  "KEY `player` (`player_id`)," +
								  "KEY `action` (`action`)," +
								  "KEY `world_id` (`world_id`)," +
								  "KEY `x_y_z` (`x`,`y`,`z`)" +
						") COLLATE utf8_general_ci, ENGINE = INNODB;");
			}
			
			//This will print an error if the user does not have SUPER privilege
			try {
				stmnt.execute("SET GLOBAL innodb_flush_log_at_trx_commit = 2");
				stmnt.execute("SET GLOBAL sync_binlog = 0");
			} catch (Exception e) { }


			//Here is were the table alterations take place (Aside from alters from making tables)

			ResultSet rs = stmnt.executeQuery("SHOW FIELDS FROM `" + Config.DbHawkEyeTable + "` where Field ='x'");

			//Older hawkeye versions x = double, and contains the column "plugin"
			if (rs.next() && !rs.getString(2).contains("int") || JDBCUtil.columnExists(dbm, Config.DbHawkEyeTable, "plugin")) {

				Util.info("Updating " + Config.DbPlayerTable + "...");

				updateTables(Config.DbPlayerTable, "`player_id`,`player`", stmnt, "CREATE TABLE IF NOT EXISTS `new" + Config.DbPlayerTable + "` (" +
						"`player_id` int(6) NOT NULL AUTO_INCREMENT, " +
						"`player` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL, " +
						"PRIMARY KEY (`player_id`), " +
						"UNIQUE KEY `player` (`player`)" +
						");");


				Util.info("Updating " + Config.DbWorldTable + "...");

				updateTables(Config.DbWorldTable, "`world_id`,`world`", stmnt, "CREATE TABLE IF NOT EXISTS `new" + Config.DbWorldTable + "` (" +
						"`world_id` int(3) NOT NULL AUTO_INCREMENT, " +
						"`world` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL, " +
						"PRIMARY KEY (`world_id`), " +
						"UNIQUE KEY `world` (`world`)" +
						") COLLATE utf8_general_ci, ENGINE = INNODB;");


				Util.info("Updating " + Config.DbHawkEyeTable + "...");

				updateTables(Config.DbHawkEyeTable, "`data_id`,`timestamp`,`player_id`,`action`,`world_id`,`x`,`y`,`z`,`data`", stmnt, "CREATE TABLE IF NOT EXISTS `new" + Config.DbHawkEyeTable + "` (" +
						"`data_id` int(10) NOT NULL AUTO_INCREMENT," +
						"`timestamp` datetime NOT NULL," +
						"`player_id` int(6) NOT NULL," +
						"`action` int(3) NOT NULL," +
						"`world_id` int(3) NOT NULL," +
						"`x` int(11) NOT NULL," +
						"`y` int(11) NOT NULL," +
						"`z` int(11) NOT NULL," +
						"`data` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL," +
						"PRIMARY KEY (`data_id`)," +
						"KEY `timestamp` (`timestamp`)," +
						"KEY `player` (`player_id`)," +
						"KEY `action` (`action`)," +
						"KEY `world_id` (`world_id`)," +
						"KEY `x_y_z` (`x`,`y`,`z`)" +
						") COLLATE utf8_general_ci, ENGINE = INNODB;");
				
				Util.info("Finished!");

			}
			
		} catch (SQLException ex) {
			Util.severe("Error checking HawkEye tables: " + ex);
			return false;
		} finally {
			try {
				if (stmnt != null)
					stmnt.close();
				conn.close();
			} catch (SQLException ex) {
				Util.severe("Unable to close SQL connection: " + ex);
			}

		}
		return true;

	}
	/**
	 * Empty the {@link DataEntry} queue into the database
	 */
	@Override
	public void run() {
		if (threadbusy || queue.isEmpty()) return;
		
		threadbusy = true;
		
		if (queue.size() > 70000)
			Util.info("The queue is almost overloaded! Queue: " + queue.size());
		
		JDCConnection conn = getConnection();
		PreparedStatement stmnt = null;
		try {
			conn.setAutoCommit(false); //Disable when process starts (We need this to properly use batch!)
			
			stmnt = conn.prepareStatement("INSERT IGNORE into `" + Config.DbHawkEyeTable + "` (timestamp, player_id, action, world_id, x, y, z, data, data_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);");

			for (int i = 0; i < queue.size(); i++) {
				DataEntry entry = queue.poll();
				
				if (!dbPlayers.containsKey(entry.getPlayer()) && !addPlayer(entry.getPlayer())) {
					Util.debug("Player '" + entry.getPlayer() + "' not found, skipping entry");
					continue;
				}
				if (!dbWorlds.containsKey(entry.getWorld()) && !addWorld(entry.getWorld())) {
					Util.debug("World '" + entry.getWorld() + "' not found, skipping entry");
					continue;
				}

				//If player ID is unable to be found, continue
				if (entry.getPlayer() == null || dbPlayers.get(entry.getPlayer()) == null) {
					Util.debug("No player found, skipping entry");
					continue;
				}
				
				stmnt.setTimestamp(1, entry.getTimestamp());
				stmnt.setInt(2, dbPlayers.get(entry.getPlayer()));
				stmnt.setInt(3, entry.getType().getId());
				stmnt.setInt(4, dbWorlds.get(entry.getWorld()));
				stmnt.setDouble(5, entry.getX());
				stmnt.setDouble(6, entry.getY());
				stmnt.setDouble(7, entry.getZ());
				stmnt.setString(8, entry.getSqlData());
				if (entry.getDataId() > 0) stmnt.setInt(9, entry.getDataId());
				else stmnt.setInt(9, 0); //0 is better then setting it to null, like before
				stmnt.addBatch();

				if (i % 1000 == 0) stmnt.executeBatch(); //If the batchsize is divisible by 1000, execute!
			}
			
			stmnt.executeBatch();
			conn.commit();
			conn.setAutoCommit(true); //Enable when commit is over (We need this to properly use batch!)

		} catch (Exception ex) {
			Util.warning(ex.getMessage());
		} finally {
			try {
				if (stmnt != null)
					stmnt.close();
				conn.close();
			} catch (Exception ex) {
				Util.severe("Unable to close SQL connection: " + ex);
			}
			
			threadbusy = false;
		}
	}
	
	public boolean isInsertThreadBusy() {
		return threadbusy;
	}
}
