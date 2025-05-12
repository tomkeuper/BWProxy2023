package com.tomkeuper.bedwars.proxy.utils;

import org.bukkit.Bukkit;

public class VersionUtil {
    public static int getMajorMinecraftVersion() {
        String version = Bukkit.getBukkitVersion(); // returns something like "1.16.5-R0.1-SNAPSHOT"
        String[] parts = version.split("\\.");
        try {
            int major = Integer.parseInt(parts[0]);
            int minor = Integer.parseInt(parts[1]);
            return major * 100 + minor; // e.g., 1.8 → 108, 1.13 → 113
        } catch (Exception e) {
            return 0; // fallback
        }
    }
}
