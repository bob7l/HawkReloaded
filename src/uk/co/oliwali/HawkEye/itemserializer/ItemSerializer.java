package uk.co.oliwali.HawkEye.itemserializer;

import org.apache.commons.lang.StringUtils;
import org.bukkit.inventory.ItemStack;
import uk.co.oliwali.HawkEye.itemserializer.entries.*;
import uk.co.oliwali.HawkEye.util.BlockUtil;
import uk.co.oliwali.HawkEye.util.SerializeUtil;

/**
 * @author bob7l
 */
public class ItemSerializer {

    private SerializerEntry[] entries;

    public ItemSerializer() {
        this(new CountEntry(), new EnchantEntry(), new LoreMetaEntry(), new NameMetaEntry());
    }

    public ItemSerializer(SerializerEntry... entires) {
        this.entries = entires;
    }

    public SerializerEntry[] getEntries() {
        return entries;
    }

    public void setEntries(SerializerEntry[] entries) {
        this.entries = entries;
    }

    public ItemStack buildItemFromString(String str) {
        ItemStack item = BlockUtil.getItemFromString(StringUtils.substringBefore(str, " "));

        return applyEntries(item, StringUtils.substringAfter(str, " "));
    }

    public ItemStack applyEntries(ItemStack item, String str) {
        for (SerializerEntry e : entries) {

            String data = SerializeUtil.findValue(Character.toString(e.getKey()), str);

            if (data != null) {
                item = e.applySerializedData(item, data);
            }

        }

        return item;
    }

    public String serializeItem(ItemStack item) {
        StringBuilder sb = new StringBuilder();

        sb.append(BlockUtil.getItemString(item)).append(' ');

        for (SerializerEntry e : entries) {
            if (e.isApplicable(item)) {
                sb.append(e.getKey()).append('{').append(e.serialize(item)).append('}');
            }
        }

        return sb.toString();
    }

}
