package uk.co.oliwali.HawkEye.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.dthielke.herochat.ChannelChatEvent;

import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.HawkEvent;
import uk.co.oliwali.HawkEye.HawkEye;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.entry.DataEntry;

/**
 * HeroChat listener class for HawkEye
 */
public class MonitorHeroChatListener extends HawkEyeListener {

	public MonitorHeroChatListener(HawkEye HawkEye) {
		super(HawkEye);
	}
	@HawkEvent(dataType = DataType.HEROCHAT)
	 public void onChannelChatEvent(ChannelChatEvent event) {
		final Player player = event.getSender().getPlayer();
		Location loc  = player.getLocation();
		DataManager.addEntry(new DataEntry(player, DataType.HEROCHAT, loc, event.getChannel().getName() + ": " + event.getMessage()));
	}
}