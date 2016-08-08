package uk.co.oliwali.HawkEye.querybuilder.filters;

import uk.co.oliwali.HawkEye.querybuilder.QueryBuilder;

import java.util.LinkedList;
import java.util.List;

/**
 * @author bob7l
 */
public class MultiKeyFilter implements Filter {

    private String columnName;

    private List<Key> values = new LinkedList<>();

    public MultiKeyFilter(String columnName) {
        this.columnName = columnName;
    }

    public void addKey(String value, boolean excluded) {
        values.add(new Key(value, excluded, value.contains("%")));
    }

    @Override
    public void applyFilter(QueryBuilder builder) {

        if (!values.isEmpty()) {

            StringBuilder sb = new StringBuilder();

            sb.append("(");

            for (Key key : values) {

                if (sb.length() != 1)
                    sb.append(" OR ");

                sb.append(columnName);

                sb.append(key.excluded ? (key.wild ? " NOT LIKE " : " != ") : (key.wild ? " LIKE " : " = ")).append("?");

                builder.addBind(key.value);
            }

            sb.append(")");

            builder.addArgument(sb.toString());
        }
    }

    private class Key {

        private String value;

        private boolean excluded;

        private boolean wild;

        Key(String value, boolean excluded, boolean wild) {
            this.value = value;
            this.excluded = excluded;
            this.wild = wild;
        }
    }
}
