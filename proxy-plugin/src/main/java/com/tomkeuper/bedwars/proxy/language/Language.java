package com.tomkeuper.bedwars.proxy.language;

import com.tomkeuper.bedwars.proxy.configuration.PluginConfig;
import com.iridium.iridiumcolorapi.IridiumColorAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.stream.Collectors;

public class Language extends PluginConfig implements com.tomkeuper.bedwars.proxy.api.Language {

    private final String iso;
    private String prefix = "";

    public Language(Plugin plugin, String iso) {
        super(plugin, "messages_" + iso, "plugins/" + plugin.getName() + "/Languages");
        this.iso = iso;
        LanguageManager.get().addLanguage(this);
    }

    public String getLangName() {
        return getYml().getString("name");
    }

    public boolean exists(String path) {
        return getYml().get(path) != null;
    }

    public String getMsg(String path) {
        return IridiumColorAPI.process(Objects.requireNonNull(getYml().getString(path)).replace("{prefix}", prefix));
    }

    public List<String> getList(String path) {
        return getYml().getStringList(path).stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toList());
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getIso() {
        return iso;
    }

    public static String getMsg(Player p, String m) {
        return LanguageManager.get().getMsg(p, m);
    }
}
