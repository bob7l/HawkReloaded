package uk.co.oliwali.HawkEye.itemserializer.entries;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import uk.co.oliwali.HawkEye.util.SerializeUtil;
import uk.co.oliwali.HawkEye.util.Util;

import java.util.List;

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
        List<String> lore = item.getItemMeta().getLore();

        for (int i = 0; i < lore.size(); i++) {
            lore.set(i, SerializeUtil.quote(lore.get(i)));
        }

        return Util.join(lore, "|");
    }

    @Override
    public ItemStack applySerializedData(ItemStack item, String data) {
        List<String> lore = SerializeUtil.unJoinData(data, '|');

        for (int i = 0; i < lore.size(); i++) {
            lore.set(i, SerializeUtil.unQuote(lore.get(i)));
        }

        ItemMeta meta = item.getItemMeta();

        meta.setLore(lore);

        item.setItemMeta(meta);

        return item;
    }

    @Override
    public boolean isApplicable(ItemStack item) {
        return (item.hasItemMeta() && item.getItemMeta().hasLore());
    }

}