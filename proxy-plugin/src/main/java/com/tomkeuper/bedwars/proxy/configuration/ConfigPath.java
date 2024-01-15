package com.tomkeuper.bedwars.proxy.configuration;

public class ConfigPath {

    public static final String GENERAL_CONFIGURATION_DISABLED_LANGUAGES = "disabled-languages";
    public static final String GENERAL_CONFIGURATION_DEBUG_MODE = "debug";
    public static final String GENERAL_ENABLE_PARTY_CMD = "enable-party-cmd";

    public static final String GENERAL_CONFIGURATION_ALLOW_PARTIES = "allow-parties";
    public static final String GENERAL_CONFIG_PLACEHOLDERS_REPLACEMENTS_SERVER_IP = "server-ip";
    public static final String GENERAL_CONFIGURATION_RANDOMARENAS = "random-selection";

    public static final String GENERAL_CONFIGURATION_ARENA_SELECTOR_PATH = "arena-gui";
    public static final String GENERAL_CONFIGURATION_ARENA_SELECTOR_SETTINGS_SIZE = GENERAL_CONFIGURATION_ARENA_SELECTOR_PATH + ".settings.inv-size";
    public static final String GENERAL_CONFIGURATION_ARENA_SELECTOR_SETTINGS_SHOW_PLAYING = GENERAL_CONFIGURATION_ARENA_SELECTOR_PATH + ".settings.show-playing";
    public static final String GENERAL_CONFIGURATION_ARENA_SELECTOR_SETTINGS_USE_SLOTS = GENERAL_CONFIGURATION_ARENA_SELECTOR_PATH + ".settings.use-slots";
    public static final String GENERAL_CONFIGURATION_ARENA_SELECTOR_STATUS_MATERIAL = GENERAL_CONFIGURATION_ARENA_SELECTOR_PATH + ".%path%.material";
    public static final String GENERAL_CONFIGURATION_ARENA_SELECTOR_STATUS_DATA = GENERAL_CONFIGURATION_ARENA_SELECTOR_PATH + ".%path%.data";
    public static final String GENERAL_CONFIGURATION_ARENA_SELECTOR_STATUS_ENCHANTED = GENERAL_CONFIGURATION_ARENA_SELECTOR_PATH + ".%path%.enchanted";

    public static final String SIGNS_LIST_PATH = "signs-list";
    public static final String SIGNS_SETTINGS_STATIC_SHOW_PLAYING = "signs-settings.static-signs.show-playing";

    // Bungeecord connection configuration
    public static final String GENERAL_CONFIGURATION_BUNGEE_OPTION_REDIS_HOST = "bungeecord-settings.redis-configuration.host";
    public static final String GENERAL_CONFIGURATION_BUNGEE_OPTION_REDIS_PORT = "bungeecord-settings.redis-configuration.port";
    public static final String GENERAL_CONFIGURATION_BUNGEE_OPTION_REDIS_PASSWORD = "bungeecord-settings.redis-configuration.password";
    public static final String GENERAL_CONFIGURATION_BUNGEE_OPTION_REDIS_CHANNEL = "bungeecord-settings.redis-configuration.channel";

    // Level configuration
    public static final String LEVEL_CONFIGURATION_PROGRESS_BAR_FORMAT = "level-settings.progress-bar-format";
    public static final String LEVEL_CONFIGURATION_BAR_UNLOCK_COLOR = "level-settings.progress-bar-unlocked-color";
    public static final String LEVEL_CONFIGURATION_BAR_LOCK_COLOR = "level-settings.progress-bar-locked-color";
    public static final String LEVEL_CONFIGURATION_BAR_SYMBOL = "level-settings.progress-bar-symbol";
    public static final String LEVEL_CONFIGURATION_DEFAULT_NAME = "level-settings.default-name";
    public static final String LEVEL_CONFIGURATION_DEFAULT_LEVEL_UP_REQUIREMENT= "level-settings.default-level-up-requirement";
}
