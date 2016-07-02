package uk.co.oliwali.HawkEye.database;

import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.SearchParser;
import uk.co.oliwali.HawkEye.callbacks.Callback;
import uk.co.oliwali.HawkEye.callbacks.DeleteCallback;
import uk.co.oliwali.HawkEye.exceptions.FailedSearchException;
import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 * Threadable class for performing a search query
 * Used for in-game searches and rollbacks
 *
 * @author oliverw92
 */
public class SearchQuery extends Thread {

    private final SearchParser parser;

    private final SearchDir dir;

    private final Callback callBack;

    private final boolean delete;

    public SearchQuery(Callback callBack, SearchParser parser, SearchDir dir) {
        this.callBack = callBack;
        this.parser = parser;
        this.dir = dir;
        this.delete = (callBack instanceof DeleteCallback);
        //Start thread
        this.start();
    }

    /**
     * Run the search query
     */
    @Override
    public void run() {

        Util.debug("Beginning search query");

        StringBuilder sql = new StringBuilder();

        sql.append((delete ? "DELETE FROM " : "SELECT * FROM "));

        sql.append("`").append(Config.DbHawkEyeTable).append("` WHERE ");

        List<String> args = new LinkedList<>();
        List<Object> binds = new LinkedList<>();

        //Match players from database list
        Util.debug("Building players");
        if (parser.players.size() >= 1) {

            List<Integer> pids = new ArrayList<>();
            List<Integer> npids = new ArrayList<>();

            for (String player : parser.players) {

                boolean ignoredUser = player.startsWith("!");

                if (ignoredUser)
                    player = player.substring(1);

                Integer id = DataManager.getPlayerDb().searchForId(player);

                if (id != null) {
                    if (ignoredUser)
                        npids.add(id);
                    else
                        pids.add(id);
                }
            }

            //Include players
            if (pids.size() > 0)
                args.add("player_id IN (" + Util.join(pids, ",") + ")");
            //Exclude players
            if (npids.size() > 0)
                args.add("player_id NOT IN (" + Util.join(npids, ",") + ")");
            if (npids.size() + pids.size() < 1) {
                callBack.fail(new FailedSearchException("No players found matching your specifications"));
                return;
            }
        }

        //Match worlds from database list
        Util.debug("Building worlds");
        if (parser.worlds != null) {

            List<Integer> wids = new ArrayList<>();
            List<Integer> nwids = new ArrayList<>();

            for (String world : parser.worlds) {

                boolean ignoreWorld = world.startsWith("!");

                if (ignoreWorld)
                    world = world.substring(1);

                Integer id = DataManager.getWorldDb().searchForId(world);

                if (id != null) {
                    if (ignoreWorld)
                        nwids.add(id);
                    else
                        wids.add(id);
                }
            }

            //Include worlds
            if (wids.size() > 0)
                args.add("world_id IN (" + Util.join(wids, ",") + ")");
            //Exclude worlds
            if (nwids.size() > 0)
                args.add("world_id NOT IN (" + Util.join(nwids, ",") + ")");
            if (nwids.size() + wids.size() < 1) {
                callBack.fail(new FailedSearchException("No worlds found matching your specifications"));
                return;
            }
        }

        //Compile actions into SQL form
        Util.debug("Building actions");
        if (parser.actions != null && parser.actions.size() > 0) {
            List<Integer> acs = new ArrayList<>();
            for (DataType act : parser.actions)
                acs.add(act.getId());
            args.add("action IN (" + Util.join(acs, ",") + ")");
        }

        //Add dates
        Util.debug("Building dates");
        if (parser.dateFrom != null) {
            args.add("timestamp >= ?");
            binds.add(parser.dateFrom);
        }
        if (parser.dateTo != null) {
            args.add("timestamp <= ?");
            binds.add(parser.dateTo);
        }

        //Check if location is exact or a range
        Util.debug("Building location");
        if (parser.minLoc != null) {
            args.add("(x BETWEEN " + parser.minLoc.getBlockX() + " AND " + parser.maxLoc.getBlockX() + ")");
            args.add("(y BETWEEN " + parser.minLoc.getBlockY() + " AND " + parser.maxLoc.getBlockY() + ")");
            args.add("(z BETWEEN " + parser.minLoc.getBlockZ() + " AND " + parser.maxLoc.getBlockZ() + ")");
        } else if (parser.loc != null) {
            args.add("x = " + parser.loc.getX());
            args.add("y = " + parser.loc.getY());
            args.add("z = " + parser.loc.getZ());
        }

        //Build the filters into SQL form
        Util.debug("Building filters");
        if (parser.filters != null) {
            for (String filter : parser.filters) {
                args.add("data LIKE ?");
                binds.add("%" + filter + "%");
            }
        }

        //Build WHERE clause
        sql.append(Util.join(args, " AND "));

        //Add order by
        Util.debug("Ordering by data_id");
        sql.append(" ORDER BY `data_id` ").append(dir.toString());

        //Check the limits
        Util.debug("Building limits");
        if (Config.MaxLines > 0)
            sql.append(" LIMIT ").append(Config.MaxLines);

        //Util.debug("Searching: " + sql);

        //Set up some stuff for the search
        List results = new ArrayList();
        int deleted = 0;

        try (Connection conn = DataManager.getConnection();
             PreparedStatement stmnt = conn.prepareStatement(sql.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
            //Execute query

            Util.debug("Preparing statement");
            ListIterator<Object> objIter = binds.listIterator();

            for (int i = 0; i < binds.size(); i++)
                stmnt.setObject(i + 1, objIter.next());

            Util.debug("Searching: " + stmnt.toString());

            if (delete) {
                Util.debug("Deleting entries");
                deleted = stmnt.executeUpdate();
            } else {
                try (ResultSet res = stmnt.executeQuery()) {

                    Util.debug("Getting results");

                    //Results are cached to prevent constant massive hashmap lookups from DataManager
                    Map<Integer, String> playerCache = new HashMap<>();
                    Map<Integer, String> worldCache = new HashMap<>();

                    DataType type;

                    String name;
                    String world;

                    //Retrieve results
                    while (res.next()) {

                        type = DataType.fromId(res.getInt(4));

                        name = playerCache.get(res.getInt(3));

                        world = worldCache.get(res.getInt(5));

                        if (name == null) {
                            name = DataManager.getPlayerDb().get(res.getInt(3));
                            playerCache.put(res.getInt(3), name);
                        }

                        if (world == null) {
                            world = DataManager.getWorldDb().get(res.getInt(5));
                            worldCache.put(res.getInt(5), world);
                        }

                        results.add(
                                type.getEntryConstructor().newInstance(
                                        name,               //Username
                                        res.getTimestamp(2),//Timestamp of entry
                                        res.getInt(1),      //dataId
                                        type,               //Data-Type
                                        res.getString(9),   //Raw-Data
                                        world,              //World Name
                                        res.getInt(6),      //X
                                        res.getInt(7),      //Y
                                        res.getInt(8)       //Z
                                ));
                    }
                }
            }
        } catch (Exception ex) {
            Util.severe("Error executing MySQL query: " + ex);
            ex.printStackTrace();
            callBack.fail(new FailedSearchException("Error executing MySQL query: " + ex));
            return;
        }

        Util.debug(results.size() + " results found");

        callBack.call(delete ? deleted : results);

        Util.debug("Search complete");

    }

    /**
     * Enumeration for result sorting directions
     *
     * @author oliverw92
     */
    public enum SearchDir {
        ASC,
        DESC
    }

}