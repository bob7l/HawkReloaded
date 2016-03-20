package uk.co.oliwali.HawkEye;

import uk.co.oliwali.HawkEye.entry.DataEntry;
import uk.co.oliwali.HawkEye.util.Util;

import java.util.List;

/**
 * Manages displaying of search results. Includes utilities for handling pages of results
 *
 * @author oliverw92
 */
public class DisplayManager {

    /**
     * Displays a page of data from the specified {@link PlayerSession} search results.
     * Contains appropriate methods for detecing errors e.g. no results
     *
     * @param session {@link PlayerSession}
     * @param page    page number to display
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
        int maxPages = (int) Math.ceil((double) results.size() / 6);

        if (page > maxPages || page < 1)
            return;

        //Calculates how many pixels we should fill in with '-'. 255 = max, 42 = other characters
        int fillPixels = (255 - (42 + (Integer.toString(page).length() * 5) + (Integer.toString(maxPages).length() * 5))) / 2;

        StringBuilder lineBuilder = new StringBuilder();

        for (; fillPixels >= 0; fillPixels -= 5) {
            lineBuilder.append("-");
        }

        String line = lineBuilder.toString();

        //Begin displaying page
        Util.sendMessage(session.getSender(), "&8" + line + " &7Page (&c" + page + "&7/&c" + maxPages + "&7) &8" + line);

        for (int i = (page - 1) * maxLines; i < ((page - 1) * maxLines) + maxLines; i++) {
            if (i == results.size())
                break;
            DataEntry entry = results.get(i);

            String time = Util.getTime(entry.getTimestamp());

            Util.sendMessage(session.getSender(), " &cID:" + entry.getDataId() + " &7" + time + "&c" + entry.getPlayer() + " &7" + entry.getType().getConfigName());
            Util.sendMessage(session.getSender(), " &cLoc: &7" + entry.getWorld() + " " + entry.getX() + "," + entry.getY() + "," + entry.getZ() + " &cData: &7" + entry.getStringData());
        }

        Util.sendMessage(session.getSender(), "&8-----------------------------------------------------");
    }

}
