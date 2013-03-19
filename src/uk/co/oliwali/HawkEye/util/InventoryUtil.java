package uk.co.oliwali.HawkEye.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Furnace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class InventoryUtil {

	public static HashMap<String,Integer> compressInventory(ItemStack[] inventory) {
		HashMap<String,Integer> items = new HashMap<String,Integer>();
		for (ItemStack item : inventory) {
			if (item == null) continue;
			String enchantments = "";
			Map<Enchantment, Integer> enchants = item.getEnchantments();
			if (!enchants.isEmpty()) {
				for (Entry<Enchantment, Integer> entry : enchants.entrySet()) {
					enchantments = enchantments + "-" + entry.getKey().getName() + "x" + entry.getValue();
				}
			}
			String iString = BlockUtil.getItemString(item) + enchantments;
			if (items.containsKey(iString)) items.put(iString, items.get(iString) + item.getAmount());
			else items.put(iString, item.getAmount());
		}
		return items;
	}

	public static ItemStack uncompressItem(String data) {
		data = data.substring(1);
		String[] item = data.split("~");
		String[] enchants = item[0].split("-");
		String[] info = enchants[0].split(":");
		ItemStack stack = null;
		if (info.length == 1) {
			stack = new ItemStack(Integer.parseInt(info[0]), Integer.parseInt(item[1]));
		} else {
			stack = new ItemStack(Integer.parseInt(info[0]), Integer.parseInt(item[1]), Byte.valueOf((byte)Integer.parseInt(info[1])) != null ? Byte.valueOf((byte)Integer.parseInt(info[1])) : (short)0);
		}
		if (enchants.length > 0) {
			for (String s : enchants) {
				String[] types = s.split("x");
				if (types.length != 1) {
					Enchantment en = Enchantment.getByName(types[0]);
					if (en != null) {
						stack.addUnsafeEnchantment(en, Integer.parseInt(types[1]));
					}
				}
			}
		}
		return stack;
	}

	public static String dataToString(String data) {
		String type = null;
		for (String changes : data.split("@")) {
			String[] item = changes.split("~");
			String[] enchants = item[0].substring(1).split("-");
			String c = changes.startsWith("+")?"&a":"&4";
			String ench = "";
			if (enchants.length != 1) {
				for (String s : enchants) {
					ench = ench + "-" + s;
				}
				ench = ench.substring(enchants[0].length() + 1);
			}
			if (type == null) {
				type = c+item[1] + "x " + c+BlockUtil.getBlockStringName(enchants[0]) + ench;
			} else {
				type = type +", " + c+item[1] + "x " + c+BlockUtil.getBlockStringName(enchants[0]) + ench;
			}
		}
		return type;
	}

	public static String compareInvs(InventoryHolder holder, HashMap<String,Integer> map1, HashMap<String,Integer> map2) {
		HashMap<String,Integer> items1 = map1;
		HashMap<String,Integer> items = map2;
		if (items1 == null && items == null) return null;
		ArrayList<String> ses = new ArrayList<String>();
		String ts = "";
		for (Entry<String, Integer> entry : items.entrySet()) {
			int count = entry.getValue();
			String key = entry.getKey();
			if (items1.containsKey(key)) {
				int c = items1.get(key);
				if (count < c) {
					ses.add("-" + key + "~" + (c - count));
				} else if (count > c) {
					ses.add("+" + key + "~" + (count - c));
				}
				items1.remove(key);
			} else {
				ses.add("+" + key + "~" + count);
			}
		} 
		for (Entry<String, Integer> entry : items1.entrySet()) {
			ses.add("-" + entry.getKey() + "~" + entry.getValue());
		}
		if (!ses.isEmpty()) {
			for (String s : ses) {
				if (ts.length() < 1) ts = s;
				else ts = ts + "@" + s;
			}
			return ts.equals("@")?null:ts;
		}
		return null;
	}

	public static Location getHolderLoc(InventoryHolder holder) {
		if (holder instanceof Chest) return ((Chest)holder).getLocation();
		if (holder instanceof DoubleChest) return ((DoubleChest)holder).getLocation().getBlock().getLocation(); //Need the block location
		if (holder instanceof Furnace) return ((Furnace)holder).getLocation();
		if (holder instanceof Dispenser) return ((Dispenser)holder).getLocation();
		return null;
	}
	
	public static boolean isHolderValid(InventoryHolder holder) {
		if (holder instanceof Chest) return true;
		if (holder instanceof DoubleChest) return true;
		if (holder instanceof Furnace) return true;
		if (holder instanceof Dispenser) return true;
		return false;
	}
}
