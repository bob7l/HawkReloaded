package uk.co.oliwali.HawkEye.itemserializer.entries;

import org.bukkit.inventory.ItemStack;

/**
 * @author bob7l
 */
public interface SerializerEntry {

    public char getKey();

    public String serialize(ItemStack item);

    public ItemStack applySerializedData(ItemStack item, String data);

    public boolean isApplicable(ItemStack item);

}
