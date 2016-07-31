package uk.co.oliwali.HawkEye.database;


import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author bob7l
 */
public class DeleteManager implements Runnable {

    private ConnectionManager connectionManager;

    public DeleteManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    private LinkedBlockingQueue<DeleteQueue> deletions = new LinkedBlockingQueue<>();

    public void addDeleteQueue(DeleteQueue deleteQueue) {
        deletions.add(deleteQueue);
    }

    /**
     * Removes a DeleteQueue from the queue
     */
    public void removeDeleteQueue(DeleteQueue deleteQueue) {
        deletions.remove(deleteQueue);
    }

    /**
     * Finds the DeleteQueue containing the key param
     * @param key - hashcode of the DeleteQueue
     * @return Returns the DeleteQueue with key == DeleteQueue#hashCode
     */
    public DeleteQueue getDeleteQueue(int key) {
        for (DeleteQueue dq : deletions) {
            if (dq.hashCode() == key) {
                return dq;
            }
        }
        return null;
    }

    @Override
    public void run() {
        if (!deletions.isEmpty()) {

            try (Connection conn = connectionManager.getConnection()) {

                DeleteQueue deleteQueue = deletions.peek();

                Util.debug("Running DeleteQueue, key: " + deleteQueue.hashCode() + ", size: " + deleteQueue.getSize());

                try (PreparedStatement stmnt = conn.prepareStatement("DELETE FROM `" + Config.DbHawkEyeTable + "` WHERE `data_id` = ?")) {

                    int removeAmount = (deleteQueue.getSize() > 10000 ? 10000 : deleteQueue.getSize());

                    for (int i = 0; i < removeAmount; i++) {

                        stmnt.setInt(1, deleteQueue.poll());
                        stmnt.addBatch();

                        if (i % 1000 == 0)
                            stmnt.executeBatch(); //If the batchsize is divisible by 1000, execute!

                    }

                    stmnt.executeBatch();

                    conn.commit();

                    if (deleteQueue.isFinished()) {
                        deletions.poll();
                    }
                }
            } catch (Exception ex) {
                Util.warning("Unable to purge MySQL:" + ex.getMessage());
            }
        }
    }

}
