package uk.co.oliwali.HawkEye.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import uk.co.oliwali.HawkEye.PlayerSession;
import uk.co.oliwali.HawkEye.SessionManager;
import uk.co.oliwali.HawkEye.ToolManager;
import uk.co.oliwali.HawkEye.util.Util;

public class ToolResetCommand extends BaseCommand {

    public ToolResetCommand() {
        name = "tool reset";
        argLength = 0;
        permission = "tool.bind";
        usage = " <- resets tool to default properties";
    }

    @Override
    public boolean execute(Player sender, String[] args) {
        PlayerSession session = SessionManager.getSession(sender);

        ToolManager.resetTool(session);
        Util.sendMessage(sender, "&cTool reset to default parameters");

        return true;
    }

    @Override
    public void moreHelp(CommandSender sender) {
        Util.sendMessage(sender, "&cReset HawkEye tool to default properties");
        Util.sendMessage(sender, "&cSee &7/hawk tool bind help");
    }
}