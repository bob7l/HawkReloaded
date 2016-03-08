package uk.co.oliwali.HawkEye;

import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import uk.co.oliwali.HawkEye.Rollback.RollbackType;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.database.DeleteQueue;
import uk.co.oliwali.HawkEye.entry.DataEntry;
import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.Util;

import java.util.Iterator;
import java.util.List;

/**
 * Runnable class for reversing a {@link Rollback}.
 * This class should always be run in a separate thread to avoid impacting on server performance
 *
 * @author oliverw92
 */
public class Undo implements Runnable {

    private final PlayerSession session;
    private Iterator<DataEntry> undoQueue;
    private int timerID;
    private int counter = 0;
    private RollbackType undoType = RollbackType.GLOBAL;

    /**
     * @param session {@link PlayerSession} to retrieve undo results from
     */
    public Undo(PlayerSession session) {

        this.session = session;
        this.undoType = session.getRollbackType();

        if (undoType == null) {
            Util.sendMessage(session.getSender(), "&cNo results found to undo");
            return;
        }

        //Check if already rolling back
        if (session.doingRollback()) {
            Util.sendMessage(session.getSender(), "&cYour previous rollback is still processing, please wait before performing an undo!");
            return;
        }

        List<DataEntry> results = session.getRollbackResults();

        //Check that we actually have results
        if (results.isEmpty()) {
            Util.sendMessage(session.getSender(), "&cNo results found to undo");
            return;
        }


        undoQueue = results.iterator();


        //Re-add deleted results back to the MySQL
        if (undoType == RollbackType.GLOBAL && Config.DeleteDataOnRollback) {
            DeleteQueue dq = DataManager.getDeleteManager().getDeleteQueue(results.get(0).getDataId());

            if (dq != null) {
                DataManager.getDeleteManager().removeDeleteQueue(dq);

                if (dq.getSize() < results.size()) { //true = deletions have been made and must be restored
                    DataManager.getQueue().addAll(results);
                }
            } else {
                DataManager.getQueue().addAll(results);
            }
        }

        Util.debug("Starting undo of " + session.getRollbackResults().size() + " results");

        //Start undo
        session.setDoingRollback(true);
        Util.sendMessage(session.getSender(), "&cAttempting to undo &7" + results.size() + "&c rollback edits");
        timerID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Bukkit.getServer().getPluginManager().getPlugin("HawkEye"), this, 1, 2);

    }

    /**
     * Run the undo.
     * Contains appropriate methods of catching errors and notifying the player
     */
    public void run() {

        //Start rollback process
        while (undoQueue.hasNext()) {

            //If undo doesn't exist
            DataEntry entry = undoQueue.next();

            //Global/Rebuild undo
            if (undoType != RollbackType.LOCAL) {
                entry.undo();
            }

            //Player undo
            else {
                if (entry.getUndo() != null) {
                    Player player = (Player) session.getSender();
                    BlockState state = entry.getUndo().getState();
                    player.sendBlockChange(state.getLocation(), state.getType(), state.getData().getData());
                }
            }
            counter++;
        }

        //Check if undo is finished
        if (!undoQueue.hasNext()) {

            //End timer
            Bukkit.getServer().getScheduler().cancelTask(timerID);

            session.setRollbackType(null);
            session.setDoingRollback(false);
            session.setRollbackResults(null);

            Util.sendMessage(session.getSender(), "&cUndo complete, &7" + counter + " &cedits performed");
            Util.debug("Undo complete, " + counter + " edits performed");

        }


    }

}