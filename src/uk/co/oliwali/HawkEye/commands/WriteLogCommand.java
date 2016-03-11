package uk.co.oliwali.HawkEye.commands;

import org.bukkit.command.CommandSender;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.SearchParser;
import uk.co.oliwali.HawkEye.SessionManager;
import uk.co.oliwali.HawkEye.callbacks.WriteLogCallback;
import uk.co.oliwali.HawkEye.database.SearchQuery;
import uk.co.oliwali.HawkEye.database.SearchQuery.SearchDir;
import uk.co.oliwali.HawkEye.util.Util;

import java.util.ArrayList;
import java.util.List;

public class WriteLogCommand extends BaseCommand {

    public WriteLogCommand() {
        name = "writelog";
        argLength = 1;
        permission = "writelog";
        usage = "<parameters> <- Search for entries to log";
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
        new SearchQuery(new WriteLogCallback(SessionManager.getSession(sender)), parser, SearchDir.DESC);
        return true;

    }

    @Override
    public void moreHelp(CommandSender sender) {
        List<String> acs = new ArrayList<>();
        for (DataType type : DataType.values()) acs.add(type.getConfigName());
        Util.sendMessage(sender, "&7There are 7 parameters you can use - &ca: p: w: r: f: t:");
        Util.sendMessage(sender, "&6Action &ca:&7 - list of actions separated by commas. Select from the following: &8" + Util.join(acs, " "));
        Util.sendMessage(sender, "&6Player &cp:&7 - list of players. &6World &cw:&7 - list of worlds");
        Util.sendMessage(sender, "&6Filter &cf:&7 - list of keywords. &6Location &cl:&7 - x,y,z location");
        Util.sendMessage(sender, "&6Radius &cr:&7 - radius to search around given location");
        Util.sendMessage(sender, "&6Time &ct:&7 - time bracket in the following format:");
        Util.sendMessage(sender, "&7  -&c Date format: yyyy-MM-dd");
        Util.sendMessage(sender, "&7  -&c t:10h45m10s &7-back specified amount of time");
        Util.sendMessage(sender, "&7  -&c t:2011-06-02,10:45:10 &7-from given date");
        Util.sendMessage(sender, "&7  -&c t:2011-06-02,10:45:10,2011-07-04,18:15:00 &7-between dates");
    }
}