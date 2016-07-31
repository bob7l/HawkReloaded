package uk.co.oliwali.HawkEye.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import uk.co.oliwali.HawkEye.HawkEye;
import uk.co.oliwali.HawkEye.SessionManager;
import uk.co.oliwali.HawkEye.Undo;
import uk.co.oliwali.HawkEye.util.Util;

/**
 * Reverses the previous {@link RollbackCommand}
 *
 * @author oliverw92
 */
public class UndoCommand extends BaseCommand {

    public UndoCommand() {
        name = "undo";
        permission = "rollback";
        usage = "<- reverses your previous rollback";
    }

    @Override
    public boolean execute(Player sender, String[] args) {
        new Undo(HawkEye.getDbmanager(), SessionManager.getSession(sender));
        return true;
    }

    @Override
    public void moreHelp(CommandSender sender) {
        Util.sendMessage(sender, "&cReverses your previous rollback if you made a mistake with it");
    }
}
