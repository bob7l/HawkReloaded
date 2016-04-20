package uk.co.oliwali.HawkEye.util;

import org.bukkit.Location;
import org.bukkit.block.*;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.entry.containerentries.ContainerEntry;
import uk.co.oliwali.HawkEye.entry.containerentries.ContainerExtract;
import uk.co.oliwali.HawkEye.itemserializer.ItemSerializer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class InventoryUtil {

    /**
     * Gets the missing items in newInv
     *
     * @param oldInv The compressed inventory to check against
     * @param newInv The compressed inventory to check for changes
     * @return Returns the missing items from newInv, and newly added items [missing, new]
     */
    public static List<ItemStack>[] getDifference(List<ItemStack> oldInv, List<ItemStack> newInv) {
        ArrayList<ItemStack> removedItems = new ArrayList<>();
        ArrayList<ItemStack> addedItems = new ArrayList<>();

        for (ItemStack oldItem : oldInv) {

            ItemStack newItem = findSimilarItemStack(newInv, oldItem);

            if (newItem != null) {

                int amountDif = oldItem.getAmount() - newItem.getAmount();

                if (amountDif > 0) {
                    oldItem.setAmount(amountDif);
                    removedItems.add(oldItem);
                } else if (amountDif < 0) {
                    oldItem.setAmount(Math.abs(amountDif));
                    addedItems.add(oldItem);
                }

                newInv.remove(newItem);

            } else {
                removedItems.add(oldItem);
            }

        }

        for (ItemStack newItem : newInv) {
            addedItems.add(newItem);
        }

        return (List<ItemStack>[]) new ArrayList<?>[]{removedItems, addedItems};
    }

    private static ItemStack findSimilarItemStack(Collection<ItemStack> items, ItemStack item) {
        for (ItemStack citem : items) {
            if (item.isSimilar(citem)) {
                return citem;
            }
        }
        return null;
    }

    /**
     * Clones and compresses an inventory for storage
     *
     * @param items The inventory contents to be compressed and cloned
     * @return The compressed cloned inventory
     */
    public static List<ItemStack> compressInventory(ItemStack[] items) {
        ArrayList<ItemStack> comItems = new ArrayList<>(items.length);

        for (ItemStack item : items) {

            if (item != null) {

                item = item.clone();

                ItemStack similarItem = findSimilarItemStack(comItems, item);

                if (similarItem != null) {
                    similarItem.setAmount(similarItem.getAmount() + item.getAmount());
                } else {
                    comItems.add(item);
                }
            }
        }

        return comItems;
    }

    public static List<String> serializeInventory(ItemSerializer itemSerializer, List<ItemStack> items) {
        List<String> lines = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        for (ItemStack item : items) {

            String itemSerial = itemSerializer.serializeItem(item);

            //Data column is varchar(500), so we can't go over that char limit
            if (sb.length() + itemSerial.length() >= 500) {
                lines.add(sb.toString());
                sb = new StringBuilder();
            }

            if (sb.length() > 0)
                sb.append(',');

            sb.append(itemSerial);
        }

        lines.add(sb.toString());

        return lines;
    }

    public static void handleHolderRemoval(String remover, BlockState state) {
        InventoryHolder holder = (InventoryHolder) state;

        if (InventoryUtil.isHolderValid(holder)) {
            List<ItemStack> invContents = compressInventory((holder instanceof Chest ? ((Chest) state).getBlockInventory() : holder.getInventory()).getContents());

            if (!invContents.isEmpty()) {
                for (String str : serializeInventory(ContainerEntry.getSerializer(), invContents))
                    DataManager.addEntry(new ContainerExtract(remover, DataType.CONTAINER_EXTRACT, InventoryUtil.getHolderLoc(holder), str));
            }
        }
    }

    public static Location getHolderLoc(InventoryHolder holder) {
        if (holder instanceof DoubleChest)
            return ((DoubleChest) holder).getLocation().getBlock().getLocation(); //Need the block location

        if (holder instanceof BlockState) {
            return ((BlockState) holder).getLocation();
        }

        return null;
    }

    public static boolean isHolderValid(InventoryHolder holder) {
        if (holder instanceof Chest) return Config.logChest;
        if (holder instanceof DoubleChest) return Config.logDoubleChest;
        if (holder instanceof Furnace) return Config.logFurnace;
        if (holder instanceof Dispenser) return Config.logDispenser;
        if (holder instanceof Hopper) return Config.LogHopper;
        return holder instanceof Dropper && Config.LogDropper;
    }

}
