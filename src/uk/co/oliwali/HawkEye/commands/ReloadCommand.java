package uk.co.oliwali.HawkEye.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.HawkEye;
import uk.co.oliwali.HawkEye.listeners.HawkEyeListener;
import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.Util;

public class ReloadCommand extends BaseCommand {

    public ReloadCommand() {
        name = "reload";
        permission = "reload";
        usage = " <- reload hawkeye";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Util.sendMessage(sender, "&c-----------&8[ &7Reload Process Started &8]&c-----------");

        HawkEye hawk = HawkEye.instance;

        hawk.reloadConfig();
        hawk.config = new Config(hawk);

        for (DataType dt : DataType.values()) {
            dt.reload();
        }

        Util.sendMessage(sender, "&8|  &7- &cConfig has been reloaded..");

        for (HawkEyeListener listener : hawk.getLoggingListeners()) {
            HandlerList.unregisterAll(listener);
        }

        hawk.registerListeners();

        Util.sendMessage(sender, "&8|  &7- &cListeners have been reloaded..");

        Util.sendMessage(sender, "&c-----------&8[ &7Reload Process Finished &8]&c-----------");
        return true;
    }

    @Override
    public void moreHelp(CommandSender sender) {
        Util.sendMessage(sender, "&cReloads Hawkeye's configuration");
    }

}
