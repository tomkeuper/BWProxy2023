package com.tomkeuper.bedwars.proxy;

import com.tomkeuper.bedwars.proxy.addon.AddonManager;
import com.tomkeuper.bedwars.proxy.api.BedWars;
import com.tomkeuper.bedwars.proxy.api.addon.Addon;
import com.tomkeuper.bedwars.proxy.api.addon.IAddonManager;
import com.tomkeuper.bedwars.proxy.api.database.Database;
import com.tomkeuper.bedwars.proxy.api.party.Party;
import com.tomkeuper.bedwars.proxy.arenamanager.ArenaSelectorListener;
import com.tomkeuper.bedwars.proxy.arenasign.SignManager;
import com.tomkeuper.bedwars.proxy.command.RejoinCommand;
import com.tomkeuper.bedwars.proxy.command.main.MainCommand;
import com.tomkeuper.bedwars.proxy.command.party.PartyCommand;
import com.tomkeuper.bedwars.proxy.configuration.BedWarsConfig;
import com.tomkeuper.bedwars.proxy.configuration.ConfigPath;
import com.tomkeuper.bedwars.proxy.configuration.SoundsConfig;
import com.tomkeuper.bedwars.proxy.connectionmanager.redis.RedisConnection;
import com.tomkeuper.bedwars.proxy.connectionmanager.redis.RetrieveArenaTask;
import com.tomkeuper.bedwars.proxy.language.LangListeners;
import com.tomkeuper.bedwars.proxy.language.LanguageManager;
import com.tomkeuper.bedwars.proxy.api.level.Level;
import com.tomkeuper.bedwars.proxy.levels.internal.InternalLevel;
import com.tomkeuper.bedwars.proxy.levels.internal.LevelListeners;
import com.tomkeuper.bedwars.proxy.party.Internal;
import com.tomkeuper.bedwars.proxy.party.PAF;
import com.tomkeuper.bedwars.proxy.party.PAFBungeeCordParty;
import com.tomkeuper.bedwars.proxy.party.Parties;
import com.tomkeuper.bedwars.proxy.support.papi.SupportPAPI;
import com.tomkeuper.bedwars.proxy.database.CacheListener;
import com.tomkeuper.bedwars.proxy.database.MySQL;
import com.tomkeuper.bedwars.proxy.database.NoDatabase;
import com.tomkeuper.bedwars.proxy.database.StatsCache;
import com.tomkeuper.spigot.versionsupport.BlockSupport;
import com.tomkeuper.spigot.versionsupport.ItemStackSupport;
import com.tomkeuper.spigot.versionsupport.MaterialSupport;
import com.tomkeuper.spigot.versionsupport.SoundSupport;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.StringJoiner;

public class BedWarsProxy extends JavaPlugin {

    private static BedWarsProxy plugin;
    private static BedWars api;
    public static BedWarsConfig config;
    private static Database remoteDatabase = null;

    private static RedisConnection redisConnection;
    private static StatsCache statsCache;

    private static SoundSupport soundAdapter;
    private static MaterialSupport materialAdapter;
    private static BlockSupport blockAdapter;
    private static ItemStackSupport itemAdapter;

    public static IAddonManager addonManager = new AddonManager();

    private static Party party;
    private static Level levelManager;

    public static boolean isPapi = false, debug = true;
    public static int defaultRankupCost;

    @Override
    public void onLoad() {
        plugin = this;
        api = new API();
        Bukkit.getServicesManager().register(BedWars.class, api, this, ServicePriority.Highest);
        // Setup languages
    }

    @Override
    public void onEnable() {
        soundAdapter = SoundSupport.SupportBuilder.load();
        materialAdapter = MaterialSupport.SupportBuilder.load();
        blockAdapter = BlockSupport.SupportBuilder.load();
        itemAdapter = ItemStackSupport.SupportBuilder.load();

        LanguageManager.init();
        config = new BedWarsConfig();
        if (config.getBoolean("database.enable")) {
            Bukkit.getScheduler().runTaskAsynchronously(this, () -> remoteDatabase = new MySQL());
        } else {
            remoteDatabase = new NoDatabase();
        }
        statsCache = new StatsCache();

        getLogger().info("Starting Redis connection...");
        redisConnection = new RedisConnection();
        new RetrieveArenaTask(redisConnection);
        if (!redisConnection.connect()){
            getLogger().severe("Could not connect to redis server! Please check the redis configuration and make sure the redis server is running! Disabling the plugin...");
            setEnabled(false);
            return;
        }

        // Check stored settings
        String retrievedValue = redisConnection.retrieveSetting("default_rankup_cost");
        if (retrievedValue != null) {
            try {
                defaultRankupCost = Integer.parseInt(retrievedValue);
            } catch (NumberFormatException e) {
                getLogger().severe("Stored default rankup cost is not a number! Using default value of 1000.");
                defaultRankupCost = 1000;
            }
        } else {
            getLogger().severe("No stored value has been found! Using default value of 1000.");
            defaultRankupCost = 1000;
        }

        registerListeners(new LangListeners(), new ArenaSelectorListener(), new CacheListener());
        //noinspection InstantiationOfUtilityClass
        new SoundsConfig();

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        //Party support
        if (config.getYml().getBoolean(ConfigPath.GENERAL_CONFIGURATION_ALLOW_PARTIES)) {
            if (Bukkit.getServer().getPluginManager().isPluginEnabled("Parties")) {
                getLogger().info("Hook into Parties (by AlessioDP) support!");
                party = new Parties();
            } else if (Bukkit.getServer().getPluginManager().isPluginEnabled("Spigot-Party-API-PAF")) {
                getLogger().info("Hook into Party and Friends Extended Edition for BungeeCord (by Simonsator) support!");
                party = new PAFBungeeCordParty();
            } else if (Bukkit.getServer().getPluginManager().isPluginEnabled("PartyAndFriends")) {
                getLogger().info("Hook into Party and Friends for Spigot (by Simonsator) support!");
                party = new PAF();
            }
        }
        if (party == null) {
            party = new Internal();
            getLogger().info("Loading internal Party system. /party");
        }

        levelManager = new InternalLevel();
        Bukkit.getPluginManager().registerEvents(new LevelListeners(), this);

        try {
            Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
            commandMap.register("bw", new MainCommand("bw"));
            commandMap.register("rejoin", new RejoinCommand("rejoin"));
            if (config.getBoolean(ConfigPath.GENERAL_ENABLE_PARTY_CMD)) {
                commandMap.register("party", new PartyCommand("party"));
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        /* PlaceholderAPI Support */
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            getLogger().info("Hook into PlaceholderAPI support!");
            new SupportPAPI().register();
            isPapi = true;
        }

        Metrics m = new Metrics(this, 20358);
        m.addCustomChart(new SimplePie("default_language", () -> LanguageManager.get().getDefaultLanguage().getIso()));
        m.addCustomChart(new SimplePie("party_adapter", () -> getParty().getClass().getSimpleName()));
        m.addCustomChart(new SimplePie("level_adapter", () -> getLevelManager().getClass().getSimpleName()));
        SignManager.init();

        // Initialize the addons
        Bukkit.getScheduler().runTaskLater(this, () -> addonManager.loadAddons(), 60L);

        // Send startup message, delayed to make sure everything is loaded and registered.
        Bukkit.getScheduler().runTaskLater(this, () -> {
            this.getLogger().info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            this.getLogger().info("BWProxy2023 v"+ plugin.getDescription().getVersion()+" has been enabled!");
            this.getLogger().info("");
            this.getLogger().info("PAPI Support: " + isPapi);
            this.getLogger().info("");
            this.getLogger().info("Party Adapter: " + getParty().getClass().getSimpleName());
            this.getLogger().info("Default Language: " + LanguageManager.get().getDefaultLanguage().getIso());
            this.getLogger().info("Level Adapter: " + getLevelManager().getClass().getSimpleName());
            this.getLogger().info("");

            StringJoiner addonString = new StringJoiner(", ");
            addonString.setEmptyValue("None");
            for (Addon addon : api.getAddonsUtil().getAddons()){
                addonString.add(addon.getName());
            }

            this.getLogger().info("Addon" + (addonManager.getAddons().isEmpty() || addonManager.getAddons().size() > 1 ? "s" : "") + " (" + addonManager.getAddons().size() + "): " + addonString);
            this.getLogger().info("");
            this.getLogger().info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        }, 80L);
    }

    @Override
    public void onDisable() {
        redisConnection.close();

        Bukkit.getScheduler().cancelTasks(this);
    }

    public static Plugin getPlugin() {
        return plugin;
    }

    public static Database getRemoteDatabase() {
        return remoteDatabase;
    }

    public static RedisConnection getRedisConnection() {
        return redisConnection;
    }

    public static StatsCache getStatsCache() {
        return statsCache;
    }

    public static MaterialSupport getMaterialAdapter() {
        return materialAdapter;
    }

    @SuppressWarnings("unused")
    public static BlockSupport getBlockAdapter() {
        return blockAdapter;
    }

    public static ItemStackSupport getItemAdapter() {
        return itemAdapter;
    }

    public static SoundSupport getSoundAdapter() {
        return soundAdapter;
    }

    private static void registerListeners(@NotNull Listener... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, getPlugin());
        }
    }

    public static Party getParty() {
        return party;
    }

    public static Level getLevelManager() {
        return levelManager;
    }

    /**
     * Create a text component.
     */
    @NotNull
    public static TextComponent createTC(String text, String suggest, String shot_text) {
        TextComponent tx = new TextComponent(text);
        tx.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggest));
        tx.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(shot_text).create()));
        return tx;
    }

    public static void setRemoteDatabase(Database remoteDatabase) {
        BedWarsProxy.remoteDatabase = remoteDatabase;
    }

    public static BedWars getAPI() {
        return api;
    }

    public static void setParty(Party party) {
        BedWarsProxy.party = party;
    }

    public static void setLevel(Level level) {
        if (level instanceof InternalLevel) {
            if (LevelListeners.instance == null) {
                Bukkit.getPluginManager().registerEvents(new LevelListeners(), BedWarsProxy.plugin);
            }
        } else {
            if (LevelListeners.instance != null) {
                PlayerJoinEvent.getHandlerList().unregister(LevelListeners.instance);
                PlayerQuitEvent.getHandlerList().unregister(LevelListeners.instance);
                LevelListeners.instance = null;
            }
        }
        levelManager = level;
    }

    public static void setDebug(boolean value) {
        debug = value;
    }

    public static void debug(String message) {
        if (debug) {
            plugin.getLogger().info("DEBUG: " + message);
        }
    }

}
