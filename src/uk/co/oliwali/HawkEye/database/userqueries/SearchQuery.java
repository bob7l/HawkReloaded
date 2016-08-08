package uk.co.oliwali.HawkEye.database.userqueries;

import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.SearchParser;
import uk.co.oliwali.HawkEye.callbacks.QueryCallback;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.entry.DataEntry;
import uk.co.oliwali.HawkEye.querybuilder.QueryBuilder;
import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Threadable class for performing a search query
 * Used for in-game searches and rollbacks
 *
 * @author oliverw92
 */
public class SearchQuery extends Query<QueryCallback, List<DataEntry>> {

    public SearchQuery(DataManager dataManager, QueryCallback callBack, SearchParser parser, Query.SearchDir dir) {
        super(dataManager, callBack, parser, dir);
    }

    public SearchQuery(QueryCallback callBack, SearchParser parser, Query.SearchDir dir) {
        super(callBack, parser, dir);
    }

    @Override
    protected QueryBuilder initializeQueryBuilder() {
        return new QueryBuilder("SELECT D.*, P.player, W.world " +
                "FROM " + Config.DbHawkEyeTable + " D " +
                "INNER JOIN " + Config.DbPlayerTable + " P ON P.player_id=D.player_id " +
                "INNER JOIN " + Config.DbWorldTable + " W on W.world_id=D.world_id");
    }


    @Override
    protected List<DataEntry> executeQuery(Connection conn, PreparedStatement stmnt) throws Exception {
        List<DataEntry> results = new ArrayList<>();

        try (ResultSet res = stmnt.executeQuery()) {

            Util.debug("Getting results");

            DataType type;

            //Retrieve results
            while (res.next()) {

                type = DataType.fromId(res.getInt(4));

                results.add(
                        type.getEntryConstructor().newInstance(
                                res.getString(10),               //Username
                                res.getTimestamp(2),//Timestamp of entry
                                res.getInt(1),      //dataId
                                type,               //Data-Type
                                res.getString(9),   //Raw-Data
                                res.getString(11),              //World Name
                                res.getInt(6),      //X
                                res.getInt(7),      //Y
                                res.getInt(8)       //Z
                        ));
            }
        }

        Util.debug(results.size() + " results found");

        return results;
    }
}