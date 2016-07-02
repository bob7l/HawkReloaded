package uk.co.oliwali.HawkEye.callbacks;

import uk.co.oliwali.HawkEye.PlayerSession;
import uk.co.oliwali.HawkEye.Rebuild;
import uk.co.oliwali.HawkEye.entry.DataEntry;
import uk.co.oliwali.HawkEye.util.Util;

import java.util.List;

/**
 * Implementation of BaseCallback for use in rollback commands
 *
 * @author oliverw92
 */
public class RebuildCallback extends QueryCallback {

    public RebuildCallback(PlayerSession session) {
        super(session);
        Util.sendMessage(session.getSender(), "&cSearching for matching results to rebuild...");
    }

    @Override
    public void call(List<DataEntry> dataEntries) {
        super.call(dataEntries);

        new Rebuild(session);
    }

}
