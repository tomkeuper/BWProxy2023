package com.tomkeuper.bedwars.proxy.utils;

import com.tomkeuper.bedwars.proxy.BedWarsProxy;
import org.bukkit.Material;

public class MaterialUtil {
    public static Material getForCurrentVersion(String v1_8, String v1_12, String v1_13) {
        int version = VersionUtil.getMajorMinecraftVersion();

        String materialName;
        if (version <= 112) {
            materialName = (version <= 108) ? v1_8 : v1_12;
        } else {
            materialName = v1_13;
        }

        try {
            return Material.valueOf(materialName);
        } catch (IllegalArgumentException e) {
            BedWarsProxy.getPlugin().getLogger().warning("Invalid material name for version " + version + ": " + materialName);
            return null;
        }
    }
}
