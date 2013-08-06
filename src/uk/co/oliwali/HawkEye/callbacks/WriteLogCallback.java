package uk.co.oliwali.HawkEye.callbacks;

import org.bukkit.command.CommandSender;

import uk.co.oliwali.HawkEye.LogManager;
import uk.co.oliwali.HawkEye.PlayerSession;
import uk.co.oliwali.HawkEye.database.SearchQuery.SearchError;
import uk.co.oliwali.HawkEye.util.Util;

public class WriteLogCallback extends BaseCallback {

	private final PlayerSession session;
	private final CommandSender sender;

	public WriteLogCallback(PlayerSession session) {
		this.session = session;
		sender = session.getSender();
		Util.sendMessage(sender, "&cSearching for matching results...");
	}

	@Override
	public void execute() {
		session.setSearchResults(results);
		LogManager.log(session);
	}

	@Override
	public void error(SearchError error, String message) {
		Util.sendMessage(sender, message);
	}

}
