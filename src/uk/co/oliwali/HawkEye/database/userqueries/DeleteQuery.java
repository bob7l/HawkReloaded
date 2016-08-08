package uk.co.oliwali.HawkEye.database.userqueries;

import uk.co.oliwali.HawkEye.SearchParser;
import uk.co.oliwali.HawkEye.callbacks.DeleteCallback;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.querybuilder.QueryBuilder;
import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * @author bob7l
 */
public class DeleteQuery extends Query<DeleteCallback, Integer> {

    public DeleteQuery(DataManager dataManager, DeleteCallback callBack, SearchParser parser, SearchDir dir) {
        super(dataManager, callBack, parser, dir);
    }

    public DeleteQuery(DeleteCallback callBack, SearchParser parser, SearchDir dir) {
        super(callBack, parser, dir);
    }

    @Override
    protected QueryBuilder initializeQueryBuilder() {
        return new QueryBuilder("DELETE FROM " + Config.DbHawkEyeTable);
    }

    @Override
    protected Integer executeQuery(Connection conn, PreparedStatement stmnt) throws Exception {
        int deleted;

        Util.debug("Deleting entries");

        deleted = stmnt.executeUpdate();

        conn.commit();

        return deleted;
    }
}
