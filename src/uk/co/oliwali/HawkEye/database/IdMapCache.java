package uk.co.oliwali.HawkEye.database;

import uk.co.oliwali.HawkEye.util.Util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author bob7l
 *
 * Stores Id and value references in two diffrent maps to insure
 * quick access to both the value and the key
 */
public class IdMapCache {

    private final Map<Integer, String> idMap = new HashMap<>();

    private final Map<String, Integer> valueMap = new HashMap<>();

    public void put(int id, String value) {
        idMap.put(id, value);
        valueMap.put(value, id);
    }

    public void remove(int id, String value) {
        idMap.remove(id);
        valueMap.remove(value);
    }

    public String get(int id) {
        return idMap.get(id);
    }

    public Integer get(String value) {
        return valueMap.get(value);
    }

    public boolean containsKey(int id) {
        return idMap.containsKey(id);
    }

    public boolean containsKey(String value) {
        return valueMap.containsKey(value);
    }

    public Integer searchForId(String value) {
        Integer id = valueMap.get(value);

        if (id != null)
            return id;

        for (String str : valueMap.keySet()) {
            if (Util.startsWithIgnoreCase(str, value)) {
                return valueMap.get(str);
            }
        }

        return null;
    }
}
