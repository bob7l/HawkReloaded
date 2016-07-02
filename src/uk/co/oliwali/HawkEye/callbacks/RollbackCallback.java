package uk.co.oliwali.HawkEye.callbacks;

import uk.co.oliwali.HawkEye.PlayerSession;
import uk.co.oliwali.HawkEye.Rollback;
import uk.co.oliwali.HawkEye.Rollback.RollbackType;
import uk.co.oliwali.HawkEye.entry.DataEntry;
import uk.co.oliwali.HawkEye.util.Util;

import java.util.List;

/**
 * Implementation of BaseCallback for use in rollback commands
 *
 * @author oliverw92
 */
public class RollbackCallback extends QueryCallback {

    private final RollbackType type;

    public RollbackCallback(PlayerSession session, RollbackType type) {
        super(session);
        this.type = type;

        Util.sendMessage(session.getSender(), "&cSearching for matching results to rollback...");
    }

    @Override
    public void call(List<DataEntry> dataEntries) {
        super.call(dataEntries);

        new Rollback(type, session);
    }

}
