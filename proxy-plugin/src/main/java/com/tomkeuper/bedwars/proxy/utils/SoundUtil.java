package com.tomkeuper.bedwars.proxy.utils;

import com.tomkeuper.bedwars.proxy.BedWarsProxy;
import org.bukkit.Sound;

public class SoundUtil {
    public static Sound getForCurrentVersion(String v1_8, String v1_12, String v1_13) {
        int version = VersionUtil.getMajorMinecraftVersion();

        String soundName;
        if (version <= 112) {
            soundName = (version <= 108) ? v1_8 : v1_12;
        } else {
            soundName = v1_13;
        }

        try {
            return Sound.valueOf(soundName);
        } catch (IllegalArgumentException e) {
            BedWarsProxy.getPlugin().getLogger().warning("Invalid sound name for version " + version + ": " + soundName);
            return null;
        }
    }
}
