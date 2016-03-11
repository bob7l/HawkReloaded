package uk.co.oliwali.HawkEye.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import uk.co.oliwali.HawkEye.PlayerSession;
import uk.co.oliwali.HawkEye.SessionManager;
import uk.co.oliwali.HawkEye.ToolManager;
import uk.co.oliwali.HawkEye.util.Util;

/**
 * Enables or disables search tool for players
 *
 * @author oliverw92
 */
public class ToolCommand extends BaseCommand {

    public ToolCommand() {
        name = "tool";
        argLength = 0;
        permission = "tool";
        usage = " <- enables/disables the searching tool";
    }

    @Override
    public boolean execute(Player sender, String[] args) {

        PlayerSession session = SessionManager.getSession(sender);

        //If not using tool, enable
        if (!session.isUsingTool())
            ToolManager.enableTool(session, sender);

            //If using tool, disable
        else
            ToolManager.disableTool(session, sender);

        return true;
    }

    @Override
    public void moreHelp(CommandSender sender) {
        Util.sendMessage(sender, "&cGives you the HawkEye tool. You can use this to see changes at specific places");
        Util.sendMessage(sender, "&cLeft click a block or place the tool to get information");
    }
}
