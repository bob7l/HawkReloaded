package uk.co.oliwali.HawkEye.worldedit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import com.sk89q.worldedit.world.World;
import uk.co.oliwali.HawkEye.database.Consumer;
import uk.co.oliwali.HawkEye.util.Util;

public class WESessionFactory {

    private Consumer consumer;

    public WESessionFactory(Consumer consumer) {
        this.consumer = consumer;
        WorldEdit.getInstance().getEventBus().register(this);
    }

    @Subscribe
    public void wrapForLogging(EditSessionEvent event) {

        if (!event.getStage().equals(EditSession.Stage.BEFORE_CHANGE))
            return;

        Actor actor = event.getActor();
        World world = event.getWorld();

        if (actor == null || !(world instanceof BukkitWorld)) {
            Util.warning("Failed to log worldedit actions for world " + (world == null ? "NULL" : world.getName()));
        } else {
            event.setExtent(new HawkSession(consumer, actor, world, event.getExtent()));
        }
    }

}
