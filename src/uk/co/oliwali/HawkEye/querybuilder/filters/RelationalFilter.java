package uk.co.oliwali.HawkEye.querybuilder.filters;

import uk.co.oliwali.HawkEye.querybuilder.QueryBuilder;

/**
 * @author bob7l
 */
public class RelationalFilter implements Filter {

    private String column;

    private String operator;

    private String value;

    public RelationalFilter(String column, String operator, String value) {
        this.column = column;
        this.operator = operator;
        this.value = value;
    }

    @Override
    public void applyFilter(QueryBuilder builder) {
        builder.addArgument(column + " " + operator + " ?");
        builder.addBind(value);
    }
}
