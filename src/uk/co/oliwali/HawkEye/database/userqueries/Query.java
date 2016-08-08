package uk.co.oliwali.HawkEye.database.userqueries;

import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.HawkEye;
import uk.co.oliwali.HawkEye.SearchParser;
import uk.co.oliwali.HawkEye.callbacks.Callback;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.exceptions.FailedSearchException;
import uk.co.oliwali.HawkEye.querybuilder.QueryBuilder;
import uk.co.oliwali.HawkEye.querybuilder.filters.BasicFilter;
import uk.co.oliwali.HawkEye.querybuilder.filters.MultiKeyFilter;
import uk.co.oliwali.HawkEye.querybuilder.filters.RelationalFilter;
import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author bob7l
 */
public abstract class Query<C extends Callback<V>, V> extends Thread {

    private final SearchParser parser;

    private final SearchDir dir;

    private final C callBack;

    private DataManager dataManager;

    public Query(DataManager dataManager, C callBack, SearchParser parser, SearchDir dir) {
        this.dataManager = dataManager;
        this.callBack = callBack;
        this.parser = parser;
        this.dir = dir;
        //Start thread
        this.start();
    }

    public Query(C callBack, SearchParser parser, SearchDir dir) {
        this(HawkEye.getDbmanager(), callBack, parser, dir);
    }

    @Override
    public void run() {
        Util.debug("Beginning query");

        QueryBuilder queryBuilder = initializeQueryBuilder();

        //Match players from database list
        Util.debug("Building players");
        if (parser.players.size() >= 1) {

            MultiKeyFilter filter = new MultiKeyFilter("player");

            for (String value : parser.players) {
                filter.addKey(value.replace("!", ""), value.startsWith("!"));
            }

            queryBuilder.addFilter(filter);
        }

        //Match worlds from database list
        Util.debug("Building worlds");
        if (parser.worlds != null) {

            MultiKeyFilter filter = new MultiKeyFilter("world");

            for (String value : parser.worlds) {
                filter.addKey(value.replace("!", ""), value.startsWith("!"));
            }

            queryBuilder.addFilter(filter);
        }

        //Compile actions into SQL form
        Util.debug("Building actions");
        if (parser.actions != null && parser.actions.size() > 0) {
            List<Integer> acs = new ArrayList<>();

            for (DataType act : parser.actions)
                acs.add(act.getId());

            queryBuilder.addFilter(new BasicFilter("action IN (" + Util.join(acs, ",") + ")"));
        }

        //Add dates
        Util.debug("Building dates");
        if (parser.dateFrom != null) {
            queryBuilder.addFilter(new RelationalFilter("timestamp", ">=", parser.dateFrom));
        }
        if (parser.dateTo != null) {
            queryBuilder.addFilter(new RelationalFilter("timestamp", "<=", parser.dateTo));
        }

        //Check if location is exact or a range
        Util.debug("Building location");
        if (parser.minLoc != null) {

            queryBuilder.addFilter(new BasicFilter("(x BETWEEN " + parser.minLoc.getBlockX() + " AND " + parser.maxLoc.getBlockX() + ")"));
            queryBuilder.addFilter(new BasicFilter("(y BETWEEN " + parser.minLoc.getBlockY() + " AND " + parser.maxLoc.getBlockY() + ")"));
            queryBuilder.addFilter(new BasicFilter("(z BETWEEN " + parser.minLoc.getBlockZ() + " AND " + parser.maxLoc.getBlockZ() + ")"));

        } else if (parser.loc != null) {
            queryBuilder.addFilter(new RelationalFilter("x", "=", Double.toString(parser.loc.getBlockX())));
            queryBuilder.addFilter(new RelationalFilter("y", "=", Double.toString(parser.loc.getBlockY())));
            queryBuilder.addFilter(new RelationalFilter("z", "=", Double.toString(parser.loc.getBlockZ())));
        }

        //Build the filters into SQL form
        Util.debug("Building filters");
        if (parser.filters != null) {

            MultiKeyFilter filter = new MultiKeyFilter("data");

            for (String value : parser.filters) {
                filter.addKey(value.startsWith("!") ? value.substring(1) : value, value.startsWith("!"));
            }

            queryBuilder.addFilter(filter);
        }

        //Add order by
        Util.debug("Ordering by data_id");

        queryBuilder.addFlag("ORDER BY `data_id` " + dir.toString());

        //Check the limits
        Util.debug("Building limits");

        if (Config.MaxLines > 0)
            queryBuilder.addFlag("LIMIT " + Config.MaxLines);

        try (Connection conn = dataManager.getConnectionManager().getConnection();
             PreparedStatement stmnt = queryBuilder.buildStatement(conn)) {

            Util.debug(stmnt.toString());

            callBack.call(executeQuery(conn, stmnt));

        } catch (Exception ex) {
            Util.severe("Error executing MySQL query: " + ex);
            ex.printStackTrace();
            callBack.fail(new FailedSearchException("Error executing MySQL query: " + ex));
        }

        Util.debug("Query complete");
    }

    protected abstract QueryBuilder initializeQueryBuilder();

    protected abstract V executeQuery(Connection conn, PreparedStatement stmnt) throws Exception;

    public enum SearchDir {
        ASC,
        DESC
    }
}
