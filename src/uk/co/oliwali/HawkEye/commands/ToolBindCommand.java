package uk.co.oliwali.HawkEye.commands;

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
	public boolean execute() {
		ToolManager.bindTool(player, session, args);
		return true;
	}

	@Override
	public void moreHelp() {
		Util.sendMessage(sender, "&cAllows you to bind custom search parameters onto the tool");
		Util.sendMessage(sender, "&cSee &7/hawk search help for info on parameters");
	}
}