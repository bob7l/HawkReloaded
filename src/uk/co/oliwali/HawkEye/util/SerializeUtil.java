package uk.co.oliwali.HawkEye.util;

import java.util.ArrayList;
import java.util.List;

/**
 * This is designed to properly serialize data for inserting/decoding data from
 * and to the database
 * <p/>
 * I don't use RegEX because in benchmarks this proved MUCH faster
 *
 * @author bob7l
 */
public class SerializeUtil {

    private static final char ESCAPER = '\\';

    private static final char BEGIN = '{';

    private static final char END = '}';

    /**
     * Brackets '{ }' define data borders
     * Character '|' defines newline contained in data
     */
    private static final char[] ESCAPED_CHARS = {BEGIN, END, '|', ESCAPER};

    private static final char NEW_LINE = ',';

    /**
     * This method will find the end of serialized lines and split them
     * to be built into an object later on
     *
     * @param line The serialized line to be split
     * @return A List containing the split lines
     */
    public static List<String> unJoin(String line) {
        List<String> lines = new ArrayList<>();

        StringBuilder sb = new StringBuilder();

        boolean escaped = false;
        boolean atEnd = false;

        for (char c : line.toCharArray()) {
            if (c == NEW_LINE && atEnd) {
                lines.add(sb.toString());
                sb = new StringBuilder();
            } else {

                if (c == ESCAPER) {
                    escaped = true;
                } else if (c == END && !escaped) {
                    atEnd = true;
                } else if (atEnd)
                    atEnd = false;
                else if (escaped)
                    escaped = false;

                sb.append(c);
            }
        }
        lines.add(sb.toString());

        return lines;
    }

    public static List<String> unJoinData(String line, char splitter) {
        List<String> lines = new ArrayList<>();

        StringBuilder sb = new StringBuilder();

        boolean escaped = false;

        for (char c : line.toCharArray()) {

            if (c == splitter && !escaped) {
                lines.add(sb.toString());
                sb = new StringBuilder();
            } else {

                if (c == ESCAPER && !escaped)
                    escaped = true;
                else if (escaped)
                    escaped = false;

                sb.append(c);
            }
        }

        lines.add(sb.toString());

        return lines;
    }

    /**
     * Checks whether a character is used in the serialization process
     * and must be escaped to be used within data
     *
     * @param c Character to be checked
     * @return Returns whether or not the character is used in the serialization process
     */
    public static boolean isSerializeCharacter(char c) {
        for (char es : ESCAPED_CHARS) {
            if (es == c) {
                return true;
            }
        }
        return false;
    }

    public static String quote(String str) {
        StringBuilder sb = new StringBuilder();

        for (char c : str.toCharArray()) {
            if (isSerializeCharacter(c))
                sb.append(ESCAPER);

            sb.append(c);
        }

        return sb.toString();
    }

    public static String unQuote(String str) {
        StringBuilder sb = new StringBuilder();

        boolean escaped = false;

        for (char c : str.toCharArray()) {

            if (c == ESCAPER && !escaped) {
                escaped = true;
            } else {
                if (isSerializeCharacter(c)) {
                    escaped = false;
                }

                sb.append(c);
            }
        }

        return sb.toString();
    }

    /**
     * This method will find the value between serialize-defined brackets and return it
     *
     * @param key The key of the serialized value
     * @param str The serialized string to be searched through
     * @return The value within your entries bounds
     */
    public static String findValue(String key, String str) {
        if (!str.contains(key + BEGIN)) {
            return null;
        }

        StringBuilder value = new StringBuilder();

        boolean escaped = false;

        String begin = key + BEGIN;

        str = str.substring(str.indexOf(begin) + (begin.length()));

        for (char c : str.toCharArray()) {
            if (c == END && !escaped) {
                return value.toString();
            } else {
                if (c == ESCAPER && !escaped)
                    escaped = true;
                else if (escaped)
                    escaped = false;

                value.append(c);
            }
        }

        return null;
    }

}
