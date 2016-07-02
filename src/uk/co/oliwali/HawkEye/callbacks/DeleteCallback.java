package uk.co.oliwali.HawkEye.callbacks;

import org.bukkit.command.CommandSender;
import uk.co.oliwali.HawkEye.PlayerSession;
import uk.co.oliwali.HawkEye.util.Util;

public class DeleteCallback implements Callback<Integer> {

    private final CommandSender sender;

    public DeleteCallback(PlayerSession session) {
        sender = session.getSender();
        Util.sendMessage(sender, "&cDeleting matching results...");
    }

    @Override
    public void call(Integer deleted) {
        Util.sendMessage(sender, "&c" + deleted + " entries removed from database.");
    }

    @Override
    public void fail(Throwable throwable) {
        Util.sendMessage(sender, throwable.getMessage());
    }

}
