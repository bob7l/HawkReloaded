package uk.co.oliwali.HawkEye.database;

import uk.co.oliwali.HawkEye.entry.DataEntry;
import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.Util;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author bob7l
 */
public class Consumer implements Runnable, Closeable {

    private final String PLAYER_NAME_COLUMN = "player";

    private final String WORLD_NAME_COLUMN = "world";

    private final String PLAYER_ID_COLUMN = "player_id";

    private final String WORLD_ID_COLUMN = "world_id";

    private final LinkedBlockingQueue<DataEntry> queue = new LinkedBlockingQueue<>();

    private final IdMapCache playerDb;

    private final IdMapCache worldDb;

    private DataManager dataManager;

    private ConnectionManager connectionManager;

    private AtomicBoolean busy = new AtomicBoolean(false);

    public Consumer(DataManager dataManager) {
        this.dataManager = dataManager;

        this.connectionManager = dataManager.getConnectionManager();
        this.playerDb = dataManager.getPlayerCache();
        this.worldDb = dataManager.getWorldCache();
    }

    /**
     * Adds a {@link DataEntry} to the database queue.
     * {Rule}s are checked at this point
     *
     * @param entry {@link DataEntry} to be added
     */
    public void addEntry(DataEntry entry) {

        if (!entry.getType().isLogged()) return;

        if (Config.IgnoreWorlds.contains(entry.getWorld())) return;

        queue.add(entry);
    }

    public LinkedBlockingQueue<DataEntry> getQueue() {
        return queue;
    }

    public boolean isBusy() {
        return busy.get();
    }

    @Override
    public void run() {
        if (queue.isEmpty() || busy.compareAndSet(false, true)) return;

        if (queue.size() > 70000)
            Util.info("HawkEye consumer can't keep up! Current Queue: " + queue.size());

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmnt = conn.prepareStatement("INSERT IGNORE into `" + Config.DbHawkEyeTable + "` (timestamp, player_id, action, world_id, x, y, z, data, data_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {

            for (int i = 0; i < queue.size(); i++) {

                DataEntry entry = queue.poll();

                int playerId = dataManager.getKeyId(playerDb, Config.DbPlayerTable, PLAYER_ID_COLUMN, PLAYER_NAME_COLUMN, entry.getPlayer());

                int worldId = dataManager.getKeyId(worldDb, Config.DbWorldTable, WORLD_ID_COLUMN, WORLD_NAME_COLUMN, entry.getWorld());

                if (playerId < 0) {
                    Util.debug("Player '" + entry.getPlayer() + "' not found, skipping entry");
                    continue;
                }

                if (worldId < 0) {
                    Util.debug("World '" + entry.getWorld() + "' not found, skipping entry");
                    continue;
                }

                stmnt.setTimestamp(1, entry.getTimestamp());
                stmnt.setInt(2, playerId);
                stmnt.setInt(3, entry.getType().getId());
                stmnt.setInt(4, worldDb.get(entry.getWorld()));
                stmnt.setDouble(5, entry.getX());
                stmnt.setDouble(6, entry.getY());
                stmnt.setDouble(7, entry.getZ());
                stmnt.setString(8, entry.getSqlData());
                stmnt.setInt(9, entry.getDataId());

                stmnt.addBatch();

                if (i % 1000 == 0) stmnt.executeBatch(); //If the batchsize is divisible by 1000, execute!
            }

            stmnt.executeBatch();

            conn.commit();

        } catch (Exception ex) {
            Util.warning(ex.getMessage());
            ex.printStackTrace();
        } finally {
            busy.set(false);
        }
    }

    @Override
    public void close() {
        while (!queue.isEmpty()) {

            busy.compareAndSet(true, false);

            run();
        }
    }
}
