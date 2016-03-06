package uk.co.oliwali.HawkEye.itemserializer.entries;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import uk.co.oliwali.HawkEye.util.Util;

import java.util.Arrays;

/**
 * @author bob7l
 */
public class LoreMetaEntry implements SerializerEntry {

    @Override
    public char getKey() {
        return 'l';
    }

    @Override
    public String serialize(ItemStack item) {
        return Util.join(item.getItemMeta().getLore(), "|");
    }

    @Override
    public ItemStack applySerializedData(ItemStack item, String data) {
        String[] lore = data.split("|");

        ItemMeta meta = item.getItemMeta();

        meta.setLore(Arrays.asList(lore));

        item.setItemMeta(meta);

        return item;
    }

    @Override
    public boolean isApplicable(ItemStack item) {
        return (item.hasItemMeta() && item.getItemMeta().hasLore());
    }


}