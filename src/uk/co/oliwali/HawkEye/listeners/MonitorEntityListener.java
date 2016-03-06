package uk.co.oliwali.HawkEye.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.inventory.ItemStack;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.HawkEvent;
import uk.co.oliwali.HawkEye.HawkEye;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.entry.*;
import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.EntityUtil;
import uk.co.oliwali.HawkEye.util.Util;

import java.util.Arrays;

/**
 * Entity listener class for HawkEye
 * Contains system for managing player deaths
 *
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
                Entity damager = ((EntityDamageByEntityEvent) (victim.getLastDamageCause())).getDamager();
                if (damager instanceof Player) {
                    if (!DataType.PVP_DEATH.isLogged() && !Config.LogDeathDrops) return;
                    DataManager.addEntry(new DataEntry(victim, DataType.PVP_DEATH, victim.getLocation(), Util.getEntityName(damager)));
                } else {
                    if (!DataType.MOB_DEATH.isLogged() && !Config.LogDeathDrops) return;
                    DataManager.addEntry(new DataEntry(victim, DataType.MOB_DEATH, victim.getLocation(), Util.getEntityName(damager)));
                }
                //Other death
            } else {
                if (!DataType.OTHER_DEATH.isLogged() && !Config.LogDeathDrops) return;
                EntityDamageEvent dEvent = victim.getLastDamageCause();
                String cause = dEvent == null ? "Unknown" : victim.getLastDamageCause().getCause().name();
                String[] words = cause.split("_");
                for (int i = 0; i < words.length; i++)
                    words[i] = words[i].substring(0, 1).toUpperCase() + words[i].substring(1).toLowerCase();
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
        } else if (DataType.ENTITY_KILL.isLogged()) { //Mob Death

            Player killer = ((LivingEntity) entity).getKiller();

            if (killer != null) {
                DataManager.addEntry(new EntityEntry(killer.getName(), DataType.ENTITY_KILL, entity.getLocation().getBlock().getLocation(), Util.getEntityName(entity)));
            }
        }
    }

    @HawkEvent(dataType = DataType.EXPLOSION)
    public void onEntityExplode(EntityExplodeEvent event) {
        Entity e = event.getEntity();
        String s = "Environment";

        if (e != null) { //Nullcheck, the entity CAN be null!
            if (e instanceof TNTPrimed) {
                Entity source = ((TNTPrimed) e).getSource();
                if (source != null && source instanceof Player)
                    s = ((Player) source).getName();
                else s = EntityUtil.entityToString(e);
            } else if (e.getType() != null) { //Nullcheck, the entitytype CAN be null!
                s = EntityUtil.entityToString(e);
            }
        }

        for (Block b : event.blockList())
            DataManager.addEntry(new BlockEntry(s, DataType.EXPLOSION, b));
    }

    @HawkEvent(dataType = DataType.ITEM_BREAK)
    public void onPaintingBreak(HangingBreakEvent event) {

        if (event.getCause().equals(RemoveCause.ENTITY)) return;

        HangingEntry he = EntityUtil.getHangingEntry(DataType.ITEM_BREAK, event.getEntity(), event.getCause().name());

        if (he != null)
            DataManager.addEntry(he);
    }

    @HawkEvent(dataType = DataType.ITEM_BREAK)
    public void onPaintingBreak(HangingBreakByEntityEvent event) {

        if (!(event.getRemover() instanceof Player)) return;

        HangingEntry he = EntityUtil.getHangingEntry(DataType.ITEM_BREAK, event.getEntity(), EntityUtil.entityToString(event.getRemover()));

        if (he != null)
            DataManager.addEntry(he);
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
        HangingEntry he = EntityUtil.getHangingEntry(DataType.ITEM_PLACE, event.getEntity(), EntityUtil.entityToString(event.getPlayer()));

        if (he != null)
            DataManager.addEntry(he);
    }

    @HawkEvent(dataType = {DataType.ENDERMAN_PICKUP, DataType.ENDERMAN_PLACE})
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {

        if (!(event.getEntity() instanceof Enderman)) return;

        Block block = event.getBlock();

        // Enderman picking up block
        if (event.getTo() == Material.AIR && DataType.ENDERMAN_PICKUP.isLogged()) {
            if (block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST)
                DataManager.addEntry(new SignEntry("Environment", DataType.SIGN_BREAK, event.getBlock()));
            DataManager.addEntry(new BlockEntry("Environment", DataType.ENDERMAN_PICKUP, block));
        } else if (DataType.ENDERMAN_PLACE.isLogged()) {
            // Enderman placing block
            Enderman enderman = (Enderman) event.getEntity();
            BlockState newState = block.getState();
            if (enderman.getCarriedMaterial() != null) {
                try {
                    newState.setData(enderman.getCarriedMaterial());
                } catch (Exception e) {
                }
                newState.setType(enderman.getCarriedMaterial().getItemType());
            }

            DataManager.addEntry(new BlockChangeEntry("Environment", DataType.ENDERMAN_PLACE, block.getLocation(), block.getState(), newState));
        }
    }
}
