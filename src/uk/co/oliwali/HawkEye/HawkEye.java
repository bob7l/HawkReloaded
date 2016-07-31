package uk.co.oliwali.HawkEye;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import uk.co.oliwali.HawkEye.WorldEdit.WESessionFactory;
import uk.co.oliwali.HawkEye.blocks.BlockHandlerContainer;
import uk.co.oliwali.HawkEye.database.Consumer;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.listeners.*;
import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HawkEye extends JavaPlugin {

    public static String version;

    public Config config;

    private static HawkEye instance;

    private List<HawkEyeListener> loggingListeners = new ArrayList<>();

    private static DataManager dbmanager;

    private static BlockHandlerContainer blockHandlerContainer;

    /**
     * Safely shuts down HawkEye
     */
    @Override
    public void onDisable() {

        if (dbmanager != null) {
            try {
                dbmanager.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Util.info("Version " + version + " disabled!");
    }

    /**
     * Starts up HawkEye initiation process
     */
    @Override
    public void onEnable() {
        version = getDescription().getVersion();

        //Setup metrics
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        PluginManager pm = getServer().getPluginManager();

        //Check bukkit dependencies
        try {
            Class.forName("org.bukkit.event.hanging.HangingPlaceEvent");
        } catch (ClassNotFoundException ex) {
            Util.info("HawkEye requires CraftBukkit 1.4+ to run properly!");
            pm.disablePlugin(this);
            return;
        }

        instance = this;

        Util.info("Starting HawkEye " + version + " initiation process...");

        //Load config
        config = new Config(this);

        setupUpdater();

        new SessionManager();

        //Initiate database connection
        try {
            dbmanager = new DataManager();
        } catch (Exception e) {
            Util.severe("Error initiating HawkEye database connection, disabling plugin");
            pm.disablePlugin(this);
            e.printStackTrace();
            return;
        }

        pm.registerEvents(new ToolListener(), this);

        Consumer consumer = dbmanager.getConsumer();

        blockHandlerContainer = new BlockHandlerContainer();

        loggingListeners.add(new MonitorBlockListener(consumer, blockHandlerContainer));
        loggingListeners.add(new MonitorEntityListener(consumer));
        loggingListeners.add(new MonitorFallingBlockListener(consumer));
        loggingListeners.add(new MonitorLiquidFlow(consumer));
        loggingListeners.add(new MonitorPlayerListener(consumer, blockHandlerContainer));
        loggingListeners.add(new MonitorWorldListener(consumer));

        if (hasDependency("WorldEdit"))
            loggingListeners.add(new MonitorWorldEditListener(consumer, blockHandlerContainer));
        if (hasDependency("Herochat"))
            loggingListeners.add(new MonitorHeroChatListener(consumer));

        registerListeners();

        getCommand("hawk").setExecutor(new HawkCommand());

        Util.info("Version " + version + " enabled!");
    }

    private boolean hasDependency(String plugin) {
        return Bukkit.getPluginManager().getPlugin(plugin) != null;
    }

    /**
     * Registers event listeners
     */
    public void registerListeners() {

        for (HawkEyeListener listener : loggingListeners)
            listener.registerEvents();

        if (hasDependency("WorldEdit") && (DataType.WORLDEDIT_BREAK.isLogged() || DataType.WORLDEDIT_PLACE.isLogged())) {

            //This makes sure we OVERRIDE any other plugin that tried to register a EditSessionFactory!
            Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                @Override
                public void run() {
                    try {
                        Class.forName("com.sk89q.worldedit.extent.logging.AbstractLoggingExtent");
                        new WESessionFactory(dbmanager.getConsumer());
                    } catch (ClassNotFoundException ex) {
                        Util.warning("[!] Failed to initialize WorldEdit logging [!]");
                        Util.warning("[!] Please upgrade WorldEdit to 6.0+       [!]");
                    }
                }
            }, 2L);
        }

    }

    private void setupUpdater() {
        if (getConfig().getBoolean("general.check-for-updates"))
            new Updater(this, "hawkeye-reload", this.getFile(), Updater.UpdateType.DEFAULT, false);
    }

    public List<HawkEyeListener> getLoggingListeners() {
        return loggingListeners;
    }

    public static HawkEye getInstance() {
        return instance;
    }

    public static DataManager getDbmanager() {
        return dbmanager;
    }

    public static BlockHandlerContainer getBlockHandlerContainer() {
        return blockHandlerContainer;
    }
}
