package uk.co.oliwali.HawkEye;

import org.bukkit.command.CommandSender;
import uk.co.oliwali.HawkEye.entry.DataEntry;
import uk.co.oliwali.HawkEye.util.Util;

import java.util.List;

/**
 * Manages displaying of search results. Includes utilities for handling pages of results
 * @author oliverw92
 */
public class DisplayManager {

	/**
	 * Displays a page of data from the specified {@link PlayerSession} search results.
	 * Contains appropriate methods for detecing errors e.g. no results
	 * @param session {@link PlayerSession}
	 * @param page page number to display
	 */
	public static void displayPage(PlayerSession session, int page) {

		//Check if any results are found
		List<DataEntry> results = session.getSearchResults();
		if (results == null || results.size() == 0) {
			Util.sendMessage(session.getSender(), "&cNo results found");
			return;
		}

		//Work out max pages. Return if page is higher than max pages
		int maxLines = 6;
		int maxPages = (int)Math.ceil((double)results.size() / 6);
		if (page > maxPages || page < 1)
			return;

		//Fix the line to properly fit the screen
		int len = String.valueOf(maxPages).length();
		if (len > 3)
			len = (len - (len / 2)) + 1;
		String l = "----------------------".substring(len);
		
		//Begin displaying page
		Util.sendMessage(session.getSender(), "&8" + l + " &7Page (&c" + page + "&7/&c" + maxPages + "&7) &8" + l + (len > 1?"-":""));

		for (int i = (page-1) * maxLines; i < ((page-1) * maxLines) + maxLines; i++) {
			if (i == results.size())
				break;
			DataEntry entry = results.get(i);
			
			String time = Util.getTime(entry.getTimestamp());
			
			sendLine(session.getSender(), "&cID:" + entry.getDataId() + " &7" + time + "&c" + entry.getPlayer() + " &7" + entry.getType().getConfigName());
			sendLine(session.getSender(), "&cLoc: &7" + entry.getWorld() + "-" + entry.getX() + "," + entry.getY() + "," + entry.getZ() + " &cData: &7" + entry.getStringData());
		}

		Util.sendMessage(session.getSender(), "&8-----------------------------------------------------");
	}

	/**
	 * Handler for sending a result. Converts to multiple lines if it is too long, or if the next word cannot fit on the line
	 * @param sender {@link CommandSender} to send result to
	 * @param line text to send wordWrap
	 */
	public static void sendLine(CommandSender sender, String input) {
		int n = 65;
		String s = "";
		String[] splitInput =input.replaceAll("\\s+"," ").replaceAll(String.format(" *(.{1,%d})(?=$| ) *", n),"$1\n").split("\n");
		for (String line : splitInput) {
			Util.sendMessage(sender, "&8| " + Util.getLastColor(s) + line);
		     s = line;
		}
	}
}
