package uk.co.oliwali.HawkEye.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import uk.co.oliwali.HawkEye.SessionManager;
import uk.co.oliwali.HawkEye.ToolManager;
import uk.co.oliwali.HawkEye.util.Util;

public class ToolBindCommand extends BaseCommand {

	public ToolBindCommand() {
		name = "tool bind";
		argLength = 1;
		permission = "tool.bind";
		usage = " <- bind custom parameters to the tool";
	}

	@Override
	public boolean execute(Player sender, String[] args) {
		ToolManager.bindTool(sender, SessionManager.getSession(sender), args);
		return true;
	}

	@Override
	public void moreHelp(CommandSender sender) {
		Util.sendMessage(sender, "&cAllows you to bind custom search parameters onto the tool");
		Util.sendMessage(sender, "&cSee &7/hawk search help for info on parameters");
	}
}