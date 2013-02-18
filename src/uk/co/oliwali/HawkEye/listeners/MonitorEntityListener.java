package uk.co.oliwali.HawkEye.listeners;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.inventory.ItemStack;

import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.HawkEvent;
import uk.co.oliwali.HawkEye.HawkEye;
import uk.co.oliwali.HawkEye.PlayerSession;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.entry.BlockChangeEntry;
import uk.co.oliwali.HawkEye.entry.BlockEntry;
import uk.co.oliwali.HawkEye.entry.DataEntry;
import uk.co.oliwali.HawkEye.entry.EntityEntry;
import uk.co.oliwali.HawkEye.entry.HangingEntry;
import uk.co.oliwali.HawkEye.entry.SignEntry;
import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.EntityUtil;
import uk.co.oliwali.HawkEye.util.Util;

/**
 * Entity listener class for HawkEye
 * Contains system for managing player deaths
 * @author oliverw92
 */
public class MonitorEntityListener extends HawkEyeListener {

	public MonitorEntityListener(HawkEye HawkEye) {
		super(HawkEye);
	}

	/**
	 * Uses the lastAttacker field in the players {@link PlayerSession} to log the death and cause
	 * We may have to redo this, newer API would work better for this
	 */
	@HawkEvent(dataType = {DataType.PVP_DEATH, DataType.MOB_DEATH, DataType.OTHER_DEATH, DataType.ENTITY_KILL})
	public void onEntityDeath(EntityDeathEvent event) {
		Entity entity = event.getEntity();
		
		if (entity instanceof Player) { //Player death
			Player victim = (Player) entity;

			//Mob or PVP death
			if (victim.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
				Entity damager = ((EntityDamageByEntityEvent)(victim.getLastDamageCause())).getDamager();
				if (damager instanceof Player) {
					if (!Config.isLogged(DataType.PVP_DEATH) && !Config.LogDeathDrops) return;
					DataManager.addEntry(new DataEntry(victim, DataType.PVP_DEATH, victim.getLocation(), Util.getEntityName(damager)));
				} else {
					if (!Config.isLogged(DataType.MOB_DEATH) && !Config.LogDeathDrops) return;
					DataManager.addEntry(new DataEntry(victim, DataType.MOB_DEATH, victim.getLocation(), Util.getEntityName(damager)));
				}
				//Other death
			} else {
				if (!Config.isLogged(DataType.OTHER_DEATH) && !Config.LogDeathDrops) return;
				EntityDamageEvent dEvent = victim.getLastDamageCause();
				String cause = dEvent == null?"Unknown":victim.getLastDamageCause().getCause().name();
				String[] words = cause.split("_");
				for (int i = 0; i < words.length; i++)
					words[i] = words[i].substring(0,1).toUpperCase() + words[i].substring(1).toLowerCase();
				cause = Util.join(Arrays.asList(words), " ");
				DataManager.addEntry(new DataEntry(victim, DataType.OTHER_DEATH, victim.getLocation(), cause));
			}

			//Log item drops
			if (Config.LogDeathDrops) {
				String data = null;
				for (ItemStack stack : event.getDrops()) {
					if (stack.getData() != null)
						data = stack.getAmount() + "x " + stack.getTypeId() + ":" + stack.getData().getData();
					else
						data = stack.getAmount() + "x " + stack.getTypeId();
					DataManager.addEntry(new DataEntry(victim, DataType.ITEM_DROP, victim.getLocation(), data));
				}
			}
		} else { //Mob Death
			if (!Config.isLogged(DataType.ENTITY_KILL)) return;

			Entity killer = ((LivingEntity) entity).getKiller();

			if ((!(killer == null)) && killer instanceof Player) {
				Player kill = (Player)killer;

				DataManager.addEntry(new EntityEntry(kill.getName(), DataType.ENTITY_KILL, entity.getLocation().getBlock().getLocation(), Util.getEntityName(entity)));
			}
		}
	}

	@HawkEvent(dataType = DataType.EXPLOSION)
	public void onEntityExplode(EntityExplodeEvent event) {
		for (Block b : event.blockList().toArray(new Block[0]))
			DataManager.addEntry(new BlockEntry(EntityUtil.entityToString(event.getEntity()), DataType.EXPLOSION, b));
	}

	@HawkEvent(dataType = DataType.ITEM_BREAK) 
	public void onPaintingBreak(HangingBreakEvent event) {
		if (event.getCause().equals(RemoveCause.ENTITY)) return;
		Entity e = event.getEntity();
		int face = 0;
		int type = 0;
		int extra = 0;

		if (e instanceof ItemFrame) {
			ItemFrame frame = (ItemFrame) e;
			type = 389;
			face = EntityUtil.getFace(frame.getAttachedFace());
			
			extra = frame.getItem().getTypeId();
		} else if (e instanceof Painting) {
			Painting paint = (Painting) e;
			type = 321;
			face = EntityUtil.getFace(paint.getAttachedFace());
			extra = paint.getArt().getId();
		} else return;
		DataManager.addEntry(new HangingEntry(event.getCause().name(), DataType.ITEM_BREAK, e.getLocation().getBlock().getLocation(), type, face, extra));
	}

	@HawkEvent(dataType = DataType.ITEM_BREAK) 
	public void onPaintingBreak(HangingBreakByEntityEvent event) {
		Entity e = event.getEntity();
		int face = 0;
		int type = 0;
		int extra = 0;

		if (!(event.getRemover() instanceof Player)) return;

		if (e instanceof ItemFrame) {
			ItemFrame frame = (ItemFrame) e;
			type = 389;
			face = EntityUtil.getFace(frame.getAttachedFace());
			extra = frame.getItem().getTypeId();
		} else if (e instanceof Painting) {
			Painting paint = (Painting) e;
			type = 321;
			face = EntityUtil.getFace(paint.getAttachedFace());
			extra = paint.getArt().getId();
		} else return;
		DataManager.addEntry(new HangingEntry((Player) event.getRemover(), DataType.ITEM_BREAK, e.getLocation().getBlock().getLocation(), type, face, extra));
	}

	@HawkEvent(dataType = DataType.ENTITY_MODIFY) 
	public void onEntityModifyBlock(EntityChangeBlockEvent event) {
		Entity en = event.getEntity();
		if (en instanceof Silverfish) return;
		DataManager.addEntry(new BlockEntry(EntityUtil.entityToString(en), DataType.ENTITY_MODIFY, event.getBlock()));
	}

	@HawkEvent(dataType = DataType.BLOCK_INHABIT)
	public void onEntityBlockChange(EntityChangeBlockEvent event) {
		Entity en = event.getEntity();
		if (!(en instanceof Silverfish)) return;
		DataManager.addEntry(new BlockEntry("SilverFish", DataType.BLOCK_INHABIT, event.getBlock()));
	}

	@HawkEvent(dataType = DataType.ITEM_PLACE)
	public void onHangingPlace(HangingPlaceEvent event) {
		Entity e = event.getEntity();
		int face = 0;
		int type = 0;
		int extra = 0;
		
		if (e instanceof ItemFrame) {
			ItemFrame frame = (ItemFrame) e;
			type = 389;
			face = EntityUtil.getFace(frame.getAttachedFace());
			extra = frame.getItem().getTypeId();
		} else if (e instanceof Painting) {
			Painting paint = (Painting) e;
			type = 321;
			face = EntityUtil.getFace(paint.getAttachedFace());
			extra = paint.getArt().getId();
		} else return;
		DataManager.addEntry(new HangingEntry(event.getPlayer(), DataType.ITEM_PLACE, e.getLocation().getBlock().getLocation(), type, face, extra));
	}

	@HawkEvent(dataType = {DataType.ENDERMAN_PICKUP, DataType.ENDERMAN_PLACE})
	public void onEntityChangeBlock(EntityChangeBlockEvent event) {

		if (!(event.getEntity() instanceof Enderman)) return;

		Block block = event.getBlock();

		// Enderman picking up block
		if (event.getTo() == Material.AIR && Config.isLogged(DataType.ENDERMAN_PICKUP)) {
			if (block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST)
				DataManager.addEntry(new SignEntry("Environment", DataType.SIGN_BREAK, event.getBlock()));
			DataManager.addEntry(new BlockEntry("Environment", DataType.ENDERMAN_PICKUP, block));
		} else if (Config.isLogged(DataType.ENDERMAN_PLACE)) {
			// Enderman placing block
			Enderman enderman = (Enderman) event.getEntity();
			BlockState newState = block.getState();
			if (enderman.getCarriedMaterial() != null) {
				try {
					newState.setData(enderman.getCarriedMaterial());
				} catch (Exception e) { }
				newState.setType(enderman.getCarriedMaterial().getItemType());
			}

			DataManager.addEntry(new BlockChangeEntry("Environment", DataType.ENDERMAN_PLACE, block.getLocation(), block.getState(), newState));
		}
	}
}
