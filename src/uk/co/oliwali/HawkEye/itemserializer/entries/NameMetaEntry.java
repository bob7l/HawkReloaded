package uk.co.oliwali.HawkEye.itemserializer.entries;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author bob7l
 */
public class NameMetaEntry implements SerializerEntry {

    @Override
    public char getKey() {
        return 'n';
    }

    @Override
    public String serialize(ItemStack item) {
        return item.getItemMeta().getDisplayName();
    }

    @Override
    public ItemStack applySerializedData(ItemStack item, String data) {
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(data);

        item.setItemMeta(meta);

        return item;
    }

    @Override
    public boolean isApplicable(ItemStack item) {
        return (item.hasItemMeta() && item.getItemMeta().hasDisplayName());
    }


}
