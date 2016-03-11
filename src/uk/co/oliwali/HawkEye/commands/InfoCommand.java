package uk.co.oliwali.HawkEye.commands;

import org.bukkit.command.CommandSender;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.HawkEye;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.util.Util;

import java.util.ArrayList;
import java.util.List;

public class InfoCommand extends BaseCommand {

    public InfoCommand() {
        name = "info";
        argLength = 0;
        permission = "info";
        usage = " <- displays hawkeye's details";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        List<String> acs = new ArrayList<String>();

        for (DataType type : DataType.values())
            if (type.isLogged()) acs.add(type.getConfigName());

        Util.sendMessage(sender, "&c---------------------&8[ &7HawkEye &8]&c---------------------");
        Util.sendMessage(sender, "&8  - &cQueue-load: &7" + DataManager.getQueue().size());
        Util.sendMessage(sender, "&8  - &cVersion: &7" + HawkEye.instance.getDescription().getVersion());
        Util.sendMessage(sender, "&8  - &cLogged: &7" + Util.join(acs, " "));
        Util.sendMessage(sender, "&c----------------------------------------------------");

        return true;
    }

    @Override
    public void moreHelp(CommandSender sender) {
        Util.sendMessage(sender, "&cDisplays HawkEye's details");
    }

}
