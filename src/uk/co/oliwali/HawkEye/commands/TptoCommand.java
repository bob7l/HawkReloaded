package uk.co.oliwali.HawkEye.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.entry.DataEntry;
import uk.co.oliwali.HawkEye.util.Util;

/**
 * Teleports player to location of specified data entry
 *
 * @author oliverw92
 */
public class TptoCommand extends BaseCommand {

    public TptoCommand() {
        name = "tpto";
        argLength = 1;
        permission = "tpto";
        usage = "<id> <- teleport to location of the data entry";
    }

    @Override
    public boolean execute(Player sender, String[] args) {

        if (!Util.isInteger(args[0])) {
            Util.sendMessage(sender, "&cPlease supply a entry id!");
            return true;
        }

        DataEntry entry = DataManager.getEntry(Integer.parseInt(args[0]));

        if (entry == null) {
            Util.sendMessage(sender, "&cEntry not found");
            return true;
        }

        World world = Bukkit.getWorld(entry.getWorld());

        if (world == null) {
            Util.sendMessage(sender, "&cWorld &7" + entry.getWorld() + "&c does not exist!");
            return true;
        }

        Location loc = new Location(world, entry.getX(), entry.getY(), entry.getZ());

        sender.teleport(loc);

        Util.sendMessage(sender, "&7Teleported to location of data entry &c" + args[0]);

        return true;
    }

    @Override
    public void moreHelp(CommandSender sender) {
        Util.sendMessage(sender, "&cTakes you to the location of the data entry with the specified ID");
        Util.sendMessage(sender, "&cThe ID can be found in either the DataLog interface or when you do a search command");
    }
}