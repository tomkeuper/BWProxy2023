package com.tomkeuper.bedwars.proxy.configuration;

import com.tomkeuper.bedwars.proxy.language.English;
import com.tomkeuper.bedwars.proxy.language.Language;
import com.tomkeuper.bedwars.proxy.BedWarsProxy;
import com.tomkeuper.bedwars.proxy.language.LanguageManager;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Collections;

public class BedWarsConfig extends PluginConfig {

    public BedWarsConfig() {
        super(BedWarsProxy.getPlugin(), "config", BedWarsProxy.getPlugin().getDataFolder().getPath());

        YamlConfiguration yml = getYml();
        yml.addDefault("language", "en");
        yml.addDefault(ConfigPath.GENERAL_CONFIGURATION_DEBUG_MODE, "false");
        yml.addDefault(ConfigPath.GENERAL_CONFIG_PLACEHOLDERS_REPLACEMENTS_SERVER_IP, "yourServer.com");
        yml.addDefault("storeLink", "https://www.spigotmc.org/resources/authors/39904/");
        yml.addDefault(ConfigPath.GENERAL_CONFIGURATION_ALLOW_PARTIES, true);
        yml.addDefault(ConfigPath.GENERAL_CONFIGURATION_RANDOMARENAS, true);
        yml.addDefault("database.enable", false);
        yml.addDefault("database.host", "localhost");
        yml.addDefault("database.database", "bedWars");
        yml.addDefault("database.user", "root");
        yml.addDefault("database.pass", "");
        yml.addDefault("database.port", 3306);
        yml.addDefault("database.ssl", false);

        yml.addDefault(ConfigPath.GENERAL_CONFIGURATION_BUNGEE_OPTION_MESSAGING_PROTOCOL, "socket");
        yml.addDefault(ConfigPath.GENERAL_CONFIGURATION_BUNGEE_OPTION_SOCKET_PORT, 2023);
        yml.addDefault(ConfigPath.GENERAL_CONFIGURATION_BUNGEE_OPTION_REDIS_HOST, "localhost");
        yml.addDefault(ConfigPath.GENERAL_CONFIGURATION_BUNGEE_OPTION_REDIS_PORT, 6379);
        yml.addDefault(ConfigPath.GENERAL_CONFIGURATION_BUNGEE_OPTION_REDIS_PASSWORD, "StrongRedisPassword1");
        yml.addDefault(ConfigPath.GENERAL_CONFIGURATION_BUNGEE_OPTION_REDIS_CHANNEL, "bw2023");

        yml.options().copyDefaults(true);

        yml.addDefault(ConfigPath.LEVEL_CONFIGURATION_DEFAULT_NAME, "&7[{number}✩] ");
        yml.addDefault(ConfigPath.LEVEL_CONFIGURATION_DEFAULT_LEVEL_UP_REQUIREMENT, 1000);
        yml.addDefault(ConfigPath.LEVEL_CONFIGURATION_BAR_SYMBOL, "■");
        yml.addDefault(ConfigPath.LEVEL_CONFIGURATION_BAR_UNLOCK_COLOR, "&b");
        yml.addDefault(ConfigPath.LEVEL_CONFIGURATION_BAR_LOCK_COLOR, "&7");
        yml.addDefault(ConfigPath.LEVEL_CONFIGURATION_PROGRESS_BAR_FORMAT, "&8 [{progress}&8]");
        yml.addDefault(ConfigPath.GENERAL_ENABLE_PARTY_CMD, true);

        yml.addDefault(ConfigPath.GENERAL_CONFIGURATION_DISABLED_LANGUAGES, Collections.singletonList("your language iso here"));

        yml.addDefault(ConfigPath.GENERAL_CONFIGURATION_ARENA_SELECTOR_SETTINGS_SIZE, 45);
        yml.addDefault(ConfigPath.GENERAL_CONFIGURATION_ARENA_SELECTOR_SETTINGS_SHOW_PLAYING, true);
        yml.addDefault(ConfigPath.GENERAL_CONFIGURATION_ARENA_SELECTOR_SETTINGS_USE_SLOTS, "10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34");
        yml.addDefault(ConfigPath.GENERAL_CONFIGURATION_ARENA_SELECTOR_STATUS_MATERIAL.replace("%path%", "waiting"), "DIAMOND_BLOCK");
        yml.addDefault(ConfigPath.GENERAL_CONFIGURATION_ARENA_SELECTOR_STATUS_DATA.replace("%path%", "waiting"), 0);
        yml.addDefault(ConfigPath.GENERAL_CONFIGURATION_ARENA_SELECTOR_STATUS_ENCHANTED.replace("%path%", "waiting"), false);
        yml.addDefault(ConfigPath.GENERAL_CONFIGURATION_ARENA_SELECTOR_STATUS_MATERIAL.replace("%path%", "starting"), "GOLD_BLOCK");
        yml.addDefault(ConfigPath.GENERAL_CONFIGURATION_ARENA_SELECTOR_STATUS_DATA.replace("%path%", "starting"), 0);
        yml.addDefault(ConfigPath.GENERAL_CONFIGURATION_ARENA_SELECTOR_STATUS_ENCHANTED.replace("%path%", "starting"), true);
        yml.addDefault(ConfigPath.GENERAL_CONFIGURATION_ARENA_SELECTOR_STATUS_MATERIAL.replace("%path%", "playing"), "REDSTONE_BLOCK");
        yml.addDefault(ConfigPath.GENERAL_CONFIGURATION_ARENA_SELECTOR_STATUS_DATA.replace("%path%", "playing"), 0);
        yml.addDefault(ConfigPath.GENERAL_CONFIGURATION_ARENA_SELECTOR_STATUS_ENCHANTED.replace("%path%", "playing"), false);
        yml.addDefault(ConfigPath.GENERAL_CONFIGURATION_ARENA_SELECTOR_STATUS_MATERIAL.replace("%path%", "skipped-slot"), String.valueOf(BedWarsProxy.getMaterialAdapter().getForCurrent("STAINED_GLASS_PANE", "STAINED_GLASS_PANE", "BLACK_STAINED_GLASS_PANE")));
        yml.addDefault(ConfigPath.GENERAL_CONFIGURATION_ARENA_SELECTOR_STATUS_DATA.replace("%path%", "skipped-slot"), 7);
        yml.addDefault(ConfigPath.GENERAL_CONFIGURATION_ARENA_SELECTOR_STATUS_ENCHANTED.replace("%path%", "skipped-slot"), false);

        save();

        //set default server language
        String whatLang = "en";
        new English();
        File[] langs = new File("plugins/" + BedWarsProxy.getPlugin().getDescription().getName() + "/Languages").listFiles();
        if (langs != null) {
            for (File f : langs) {
                if (f.isFile()) {
                    if (f.getName().startsWith("messages_") && f.getName().endsWith(".yml")) {
                        String lang = f.getName().replace("messages_", "").replace(".yml", "");
                        if (lang.equalsIgnoreCase(yml.getString("language"))) {
                            whatLang = f.getName().replace("messages_", "").replace(".yml", "");
                        }
                        if (LanguageManager.get().getLang(lang) == null) new Language(BedWarsProxy.getPlugin(), lang);
                    }
                }
            }
        }
        com.tomkeuper.bedwars.proxy.api.Language def = LanguageManager.get().getLang(whatLang);

        if (def == null) throw new IllegalStateException("Could not find default language: " + whatLang);
        LanguageManager.get().setDefaultLanguage(def);

        BedWarsProxy.setDebug(yml.getBoolean(ConfigPath.GENERAL_CONFIGURATION_DEBUG_MODE));

        //remove languages if disabled
        //server language can t be disabled
        for (String iso : yml.getStringList(ConfigPath.GENERAL_CONFIGURATION_DISABLED_LANGUAGES)) {
            com.tomkeuper.bedwars.proxy.api.Language l = LanguageManager.get().getLang(iso);
            if (l != null) {
                if (l != def) LanguageManager.get().getLanguages().remove(l);
            }
        }
        //
    }
}
