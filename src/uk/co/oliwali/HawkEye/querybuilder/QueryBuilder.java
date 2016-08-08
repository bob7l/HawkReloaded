package uk.co.oliwali.HawkEye.querybuilder;

import uk.co.oliwali.HawkEye.querybuilder.filters.Filter;
import uk.co.oliwali.HawkEye.util.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * @author bob7l
 */
public class QueryBuilder {

    private String header;

    private List<Filter> filters = new LinkedList<>();

    private List<String> flags = new LinkedList<>();

    private List<Object> binds = new LinkedList<>();

    private List<String> arguments = new LinkedList<>();

    public QueryBuilder(String header) {
        this.header = header;
    }

    public List<Object> getBinds() {
        return binds;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public String getHeader() {
        return header;
    }

    public List<Filter> getFilters() {
        return filters;
    }

    public QueryBuilder addFilter(Filter filter) {
        filters.add(filter);
        return this;
    }

    public QueryBuilder addFlag(String flag) {
        flags.add(flag);
        return this;
    }

    public QueryBuilder addBind(Object bind) {
        binds.add(bind);
        return this;
    }

    public QueryBuilder addArgument(String argument) {
        arguments.add(argument);
        return this;
    }


    public PreparedStatement buildStatement(Connection connection) throws SQLException {

        StringBuilder sql = new StringBuilder();

        sql.append(header).append(" WHERE ");

        for (Filter filter : filters)
            filter.applyFilter(this);

        sql.append(Util.join(arguments, " AND "));

        for (String flag : flags)
            sql.append(" ").append(flag).append(" ");

        PreparedStatement stmnt = connection.prepareStatement(sql.toString());

        ListIterator<Object> objIter = binds.listIterator();

        for (int i = 0; i < binds.size(); i++)
            stmnt.setObject(i + 1, objIter.next());

        return stmnt;
    }
}
