package uk.co.oliwali.HawkEye.util;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Permissions for HawkEye
 * @author oliverw92
 */
public class Permission {
	
	private static boolean hasPermission(CommandSender sender, String node) {
		if (!(sender instanceof Player)) return true;
		
		Player p = (Player) sender;
		if (p.isOp() && Config.OpPermissions) return true;
		
		return p.hasPermission(node);
	}

	public static boolean page(CommandSender player) {
		return hasPermission(player, "hawkeye.page");
	}


	public static boolean search(CommandSender player) {
		return hasPermission(player, "hawkeye.search");
	}


	public static boolean searchType(CommandSender player, String type) {
		return hasPermission(player, "hawkeye.search." + type.toLowerCase());
	}


	public static boolean tpTo(CommandSender player) {
		return hasPermission(player, "hawkeye.tpto");
	}


	public static boolean rollback(CommandSender player) {
		return hasPermission(player, "hawkeye.rollback");
	}


	public static boolean tool(CommandSender player) {
		return hasPermission(player, "hawkeye.tool");
	}


	public static boolean notify(CommandSender player) {
		return hasPermission(player, "hawkeye.notify");
	}


	public static boolean preview(CommandSender player) {
		return hasPermission(player, "hawkeye.preview");
	}


	public static boolean toolBind(CommandSender player) {
		return hasPermission(player, "hawkeye.tool.bind");
	}


	public static boolean rebuild(CommandSender player) {
		return hasPermission(player, "hawkeye.rebuild");
	}


	public static boolean delete(CommandSender player) {
		return hasPermission(player, "hawkeye.delete");
	}

}