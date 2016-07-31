package uk.co.oliwali.HawkEye;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import uk.co.oliwali.HawkEye.database.DeleteQueue;
import uk.co.oliwali.HawkEye.entry.DataEntry;
import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Runnable class for performing a data rollback.
 * This class should always be run in a separate thread to avoid impacting on server performance
 * @author oliverw92
 */
public class Rollback implements Runnable {

	private final PlayerSession session;
	private Iterator<DataEntry> rollbackQueue;
	private final List<DataEntry> undo = new ArrayList<DataEntry>();
	private int timerID;
	private int speed = Config.DefaultEditSpeed;
	private RollbackType rollbackType = RollbackType.GLOBAL;

	/**
	 * @param session {@link PlayerSession} to retrieve rollback results from
	 */
	public Rollback(RollbackType rollbackType, PlayerSession session) {

		this.rollbackType = rollbackType;
		this.session = session;
		this.speed = session.getEditSpeed();
		session.setRollbackType(rollbackType);
		rollbackQueue = session.getRollbackResults().iterator();
		
		//Check that we actually have results
		if (!rollbackQueue.hasNext()) {
			Util.sendMessage(session.getSender(), "&cNo results found to rollback");
			return;
		}

		Util.debug("Starting rollback of " + session.getRollbackResults().size() + " results");

		//Start rollback
		session.setDoingRollback(true);
		Util.sendMessage(session.getSender(), "&cAttempting to rollback &7" + session.getRollbackResults().size() + "&c results");
		timerID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Bukkit.getServer().getPluginManager().getPlugin("HawkEye"), this, 1, 2);

	}

	/**
	 * Run the rollback.
	 * Contains appropriate methods of catching errors and notifying the player
	 */
	public void run() {

		//Start rollback process
		int i = 0;
		while (i < speed && rollbackQueue.hasNext()) {
			i++;

			DataEntry entry = rollbackQueue.next();

			//If the action can't be rolled back, skip this entry
			if (entry.getType() == null || !entry.getType().canRollback())
				continue;

			//If the world doesn't exist, skip this entry
			World world = Bukkit.getWorld(entry.getWorld());
			if (world == null)
				continue;

			//Get some data from the entry
			Location loc = new Location(world, entry.getX(), entry.getY(), entry.getZ());
			Block block = world.getBlockAt(loc);
			BlockState state = block.getState();
			
			entry.setUndoState(state);
				
			//Attempt global rollback
			if (rollbackType == RollbackType.GLOBAL && entry.rollback(world.getBlockAt(loc))) {
				undo.add(entry);
			}
			//Local rollback preview
			else if (rollbackType == RollbackType.LOCAL && entry.rollbackPlayer(block, (Player)session.getSender())) {
				undo.add(entry);
			}
		}

		//Check if rollback is finished
		if (!rollbackQueue.hasNext()) {

			Bukkit.getServer().getScheduler().cancelTask(timerID);
			
			Collections.reverse(undo); //Reverse the order so we properly undo the rollback!
			session.setDoingRollback(false);
			session.setRollbackResults(undo);
			session.setEditSpeed(Config.DefaultEditSpeed);

			//Store undo results and notify player
			if (rollbackType == RollbackType.GLOBAL) {
				Util.sendMessage(session.getSender(), "&cRollback complete, &7" + undo.size() + "&c edits performed");
				Util.sendMessage(session.getSender(), "&cUndo this rollback using &7/hawk undo");
				//Delete data if told to
				if (Config.DeleteDataOnRollback)
					HawkEye.getDbmanager().getDeleteManager().addDeleteQueue(new DeleteQueue(undo));
			} else {
				Util.sendMessage(session.getSender(), "&cRollback preview complete, &7" + undo.size() + "&c edits performed to you");
				Util.sendMessage(session.getSender(), "&cType &7/hawk preview apply&c to make these changes permanent or &7/hawk preview cancel&c to cancel");
			}

			Util.debug("Rollback complete, " + undo.size() + " edits performed");

		}

	}

	public enum RollbackType {
		GLOBAL,
		REBUILD,
		LOCAL
	}
}