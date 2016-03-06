package uk.co.oliwali.HawkEye.itemserializer.entries;

import org.bukkit.inventory.ItemStack;

/**
 * @author bob7l
 */
public class CountEntry implements SerializerEntry {

    @Override
    public char getKey() {
        return 'c';
    }

    @Override
    public String serialize(ItemStack item) {
        return Integer.toString(item.getAmount());
    }

    @Override
    public ItemStack applySerializedData(ItemStack item, String data) {
        item.setAmount(Integer.parseInt(data));

        return item;
    }

    /**
     * Checks whether or not the ItemStack can be serialized by CountEntry
     * @param item  The item that should be checked
     * @return      Returns whether or not the item can be serialized. If the item count is 1, no point in storing
     */
    @Override
    public boolean isApplicable(ItemStack item) {
        return (item.getAmount() > 1);
    }


}
