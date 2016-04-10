package uk.co.oliwali.HawkEye.database;

import uk.co.oliwali.HawkEye.util.Util;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * @author bob7l
 *
 * Stores Id and value references in two diffrent maps to insure
 * quick access to both the value and the key
 */
public class IdMapCache {

    private final Map<Integer, String> idMap = new IdentityHashMap<>();

    private final Map<String, Integer> valueMap = new HashMap<>();

    public void put(Integer id, String value) {
        idMap.put(id, value);
        valueMap.put(value, id);
    }

    public void remove(Integer id, String value) {
        idMap.remove(id);
        valueMap.remove(value);
    }

    public String get(Integer id) {
        return idMap.get(id);
    }

    public Integer get(String value) {
        return valueMap.get(value);
    }

    public boolean containsKey(Integer id) {
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
