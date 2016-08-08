package uk.co.oliwali.HawkEye;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import uk.co.oliwali.HawkEye.callbacks.SearchCallback;
import uk.co.oliwali.HawkEye.database.userqueries.Query.SearchDir;
import uk.co.oliwali.HawkEye.database.userqueries.SearchQuery;
import uk.co.oliwali.HawkEye.util.BlockUtil;
import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.Util;

import java.util.Arrays;
import java.util.EnumSet;

/**
 * Contains methods for controlling the HawkEye tool
 *
 * @author oliverw92
 */
public class ToolManager {

    private static EnumSet<Material> doubleChests = EnumSet.of(Material.CHEST, Material.TRAPPED_CHEST);

    /**
     * Enables the HawkEye tool
     *
     * @param session
     * @param player
     */
    public static void enableTool(PlayerSession session, Player player) {

        Inventory inv = player.getInventory();
        session.setUsingTool(true);
        ItemStack stack = new ItemStack(Config.ToolBlock);

        //If player doesn't have a tool, give them one if enabled in config
        if (!inv.contains(stack) && Config.GiveTool) {
            int first = inv.firstEmpty();
            if (first == -1)
                player.getWorld().dropItem(player.getLocation(), stack);
            else inv.setItem(first, stack);
        }


        //If they aren't holding a tool, move the tool to their hand
        int first = inv.first(stack);
        if (player.getItemInHand().getType() != Config.ToolBlock && first != -1) {
            ItemStack back = player.getItemInHand().clone();
            player.setItemInHand(inv.getItem(first));
            if (back.getAmount() == 0) inv.clear(first);
            else inv.setItem(first, back);
        }

        Util.sendMessage(player, "&cHawkEye tool enabled! &7Left click a block or place the tool to get information");

    }

    /**
     * Disables the HawkEye tool
     *
     * @param session
     * @param player
     */
    public static void disableTool(PlayerSession session, Player player) {
        session.setUsingTool(false);
        Util.sendMessage(player, "&cHawkEye tool disabled");
    }

    /**
     * Performs a HawkEye tool search at the specified location
     *
     * @param player The player who is performing the search
     * @param b       The block to perform the search on
     */
    public static void toolSearch(Player player, Block b) {

        Location loc = b.getLocation();

        PlayerSession session = SessionManager.getSession(player);
        SearchParser parser;

        //If parameters aren't bound, do some default
        if (session.getToolCommand().length == 0 || session.getToolCommand()[0].equals("")) {
            parser = new SearchParser(player);
            for (DataType type : DataType.values())
                if (type.canHere()) parser.actions.add(type);
        }
        //Else use the default ones
        else {
            parser = new SearchParser(player, session.getToolCommand());
        }

        Vector vec = Util.getSimpleLocation(loc).toVector();

        parser.minLoc = null;
        parser.maxLoc = null;
        parser.loc    =  vec;

        if (doubleChests.contains(b.getType())) {

            Material type = b.getType();

            for (BlockFace face : BlockUtil.faces) {

                Block b2 = b.getRelative(face);

                if (b2.getType() == type) {

                    Location loc2 = b2.getLocation();

                    parser.minLoc = new Vector(Math.min(loc.getX(), loc2.getX()), Math.min(loc.getY(), loc2.getY()), Math.min(loc.getZ(), loc2.getZ()));
                    parser.maxLoc = new Vector(Math.max(loc.getX(), loc2.getX()), Math.max(loc.getY(), loc2.getY()), Math.max(loc.getZ(), loc2.getZ()));

                    parser.loc = null;

                    break;
                }
            }
        }

        parser.worlds = new String[]{loc.getWorld().getName()};

        new SearchQuery(new SearchCallback(SessionManager.getSession(player)), parser, SearchDir.DESC);
    }

    /**
     * Binds arguments to the HawkEye tool
     *
     * @param player  player issueing the command
     * @param session session to save args to
     * @param args    parameters
     */
    public static void bindTool(Player player, PlayerSession session, String[] args) {

        try {
            new SearchParser(player, args);
        } catch (IllegalArgumentException e) {
            Util.sendMessage(player, "&c" + e.getMessage());
            return;
        }

        Util.sendMessage(player, "&cParameters bound to tool: &7" + Util.join(Arrays.asList(args), " "));
        session.setToolCommand(args);
        if (!session.isUsingTool()) enableTool(session, player);

    }

    /**
     * Reset tool to default parameters
     *
     * @param session
     */
    public static void resetTool(PlayerSession session) {
        session.setToolCommand(Config.DefaultToolCommand);
    }

}
