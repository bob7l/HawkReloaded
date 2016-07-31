package uk.co.oliwali.HawkEye.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import uk.co.oliwali.HawkEye.HawkEye;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
    public boolean execute(final Player sender, final String[] args) {

        if (!Util.isInteger(args[0])) {
            Util.sendMessage(sender, "&cPlease supply a entry id!");
        } else {

            new Thread(new Runnable() {
                @Override
                public void run() {

                    final Location loc = searchForEntryLocation(Integer.parseInt(args[0]));

                    if (loc == null) {
                        Util.sendMessage(sender, "&cEntry not found");
                    } else if (loc.getWorld() == null) {
                        Util.sendMessage(sender, "&cEntry does not have an existing world!");
                    } else {
                        Util.sendMessage(sender, "&7Teleported to location of data entry &c" + args[0]);

                        Util.runLater(new Runnable() {
                            @Override
                            public void run() {
                                sender.teleport(loc);
                            }
                        });
                    }
                }
            }, "HawkEye - TpTo Thread").start();

        }

        return true;
    }

    @Override
    public void moreHelp(CommandSender sender) {
        Util.sendMessage(sender, "&cTakes you to the location of the data entry with the specified ID");
        Util.sendMessage(sender, "&cThe ID can be found in either the DataLog interface or when you do a search command");
    }

    /**
     * Retrieves an entry location from the database
     *
     * @param id id of entry to return
     * @return The location of the searched Entry
     */
    private Location searchForEntryLocation(int id) {
        DataManager dataManager = HawkEye.getDbmanager();

        try (Connection conn = dataManager.getConnectionManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT world_id,x,y,z FROM `" + Config.DbHawkEyeTable + "` WHERE `data_id` = ?")) {

            stmt.setInt(1, id);

            try (ResultSet res = stmt.executeQuery()) {

                if (res.next()) {
                    return new Location(Bukkit.getWorld(dataManager.getWorldCache().get(res.getInt(1))), res.getInt(2), res.getInt(3), res.getInt(4));
                }

            }

        } catch (Exception ex) {
            Util.severe("Unable to retrieve data entry from MySQL Server: " + ex);
        }
        return null;
    }

}