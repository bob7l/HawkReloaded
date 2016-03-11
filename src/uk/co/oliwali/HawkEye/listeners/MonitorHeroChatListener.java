package uk.co.oliwali.HawkEye.listeners;

import com.dthielke.herochat.ChannelChatEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.HawkEvent;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.entry.DataEntry;

/**
 * HeroChat listener class for HawkEye
 */
public class MonitorHeroChatListener extends HawkEyeListener {

	@HawkEvent(dataType = DataType.HEROCHAT)
	 public void onChannelChatEvent(ChannelChatEvent event) {
		final Player player = event.getSender().getPlayer();
		Location loc  = player.getLocation();
		DataManager.addEntry(new DataEntry(player, DataType.HEROCHAT, loc, event.getChannel().getName() + ": " + event.getMessage()));
	}
}