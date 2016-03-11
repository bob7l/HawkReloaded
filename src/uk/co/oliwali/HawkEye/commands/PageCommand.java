package uk.co.oliwali.HawkEye.commands;

import org.bukkit.command.CommandSender;
import uk.co.oliwali.HawkEye.DisplayManager;
import uk.co.oliwali.HawkEye.SessionManager;
import uk.co.oliwali.HawkEye.util.Util;

/**
 * Displays a page from the player's previous search results
 *
 * @author oliverw92
 */
public class PageCommand extends BaseCommand {

    public PageCommand() {
        name = "page";
        permission = "page";
        argLength = 1;
        usage = "<page> <- display a page from your last search";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!Util.isInteger(args[0])) {
            Util.sendMessage(sender, "&cInvalid argument format: &7" + args[0]);
            return true;
        }
        DisplayManager.displayPage(SessionManager.getSession(sender), Integer.parseInt(args[0]));
        return true;
    }

    @Override
    public void moreHelp(CommandSender sender) {
        Util.sendMessage(sender, "&cShows the specified page of results from your latest search");
    }

}