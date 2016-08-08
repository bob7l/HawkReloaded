package uk.co.oliwali.HawkEye.commands;

import org.bukkit.command.CommandSender;
import uk.co.oliwali.HawkEye.SearchParser;
import uk.co.oliwali.HawkEye.SessionManager;
import uk.co.oliwali.HawkEye.callbacks.DeleteCallback;
import uk.co.oliwali.HawkEye.database.userqueries.DeleteQuery;
import uk.co.oliwali.HawkEye.database.userqueries.Query.SearchDir;
import uk.co.oliwali.HawkEye.util.Util;

public class DeleteCommand extends BaseCommand {

    public DeleteCommand() {
        name = "delete";
        permission = "delete";
        argLength = 1;
        usage = "<parameters> <- delete database entries";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        //Parse arguments
        SearchParser parser = null;
        try {
            parser = new SearchParser(sender, args);
        } catch (IllegalArgumentException e) {
            Util.sendMessage(sender, "&c" + e.getMessage());
            return true;
        }

        //Create new SeachQuery with data
        new DeleteQuery(new DeleteCallback(SessionManager.getSession(sender)), parser, SearchDir.DESC);

        return true;
    }

    @Override
    public void moreHelp(CommandSender sender) {
        Util.sendMessage(sender, "&cDeletes specified entries from the database permanently");
        Util.sendMessage(sender, "&cUses the same parameters and format as /hawk search");
    }

}
