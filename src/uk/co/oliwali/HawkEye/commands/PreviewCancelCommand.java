package uk.co.oliwali.HawkEye.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import uk.co.oliwali.HawkEye.HawkEye;
import uk.co.oliwali.HawkEye.PlayerSession;
import uk.co.oliwali.HawkEye.Rollback.RollbackType;
import uk.co.oliwali.HawkEye.SessionManager;
import uk.co.oliwali.HawkEye.Undo;
import uk.co.oliwali.HawkEye.util.Util;

/**
 * Cancels a rollback preview.
 * Error handling for user input is done using exceptions to keep code neat.
 *
 * @author oliverw92
 */
public class PreviewCancelCommand extends BaseCommand {

    public PreviewCancelCommand() {
        name = "preview cancel";
        permission = "preview";
        usage = "<- cancel rollback preview";
    }

    @Override
    public boolean execute(Player sender, String[] args) {
        PlayerSession session = SessionManager.getSession(sender);

        //Check if player already has a rollback processing
        if (!session.isInPreview() || (session.getRollbackType() != RollbackType.LOCAL)) {
            Util.sendMessage(sender, "&cNo preview to cancel!");
            return true;
        }

        //Undo local changes to the player
        new Undo(HawkEye.getDbmanager(), session);

        Util.sendMessage(sender, "&cPreview rollback cancelled");
        session.setInPreview(false);
        return true;

    }

    @Override
    public void moreHelp(CommandSender sender) {
        Util.sendMessage(sender, "&cCancels results of a &7/hawk preview");
        Util.sendMessage(sender, "&cOnly affects you - no changes are seen by anyone else");
    }

}
