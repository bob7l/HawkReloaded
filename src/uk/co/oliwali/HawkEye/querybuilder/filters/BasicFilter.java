package uk.co.oliwali.HawkEye.querybuilder.filters;

import uk.co.oliwali.HawkEye.querybuilder.QueryBuilder;

/**
 * @author bob7l
 */
public class BasicFilter implements Filter {

    private String filter;

    public BasicFilter(String filter) {
        this.filter = filter;
    }

    @Override
    public void applyFilter(QueryBuilder builder) {
        builder.addArgument(filter);
    }
}
