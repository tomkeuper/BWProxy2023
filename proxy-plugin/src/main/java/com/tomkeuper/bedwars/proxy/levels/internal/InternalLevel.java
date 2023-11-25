package com.tomkeuper.bedwars.proxy.levels.internal;

import com.tomkeuper.bedwars.proxy.BedWarsProxy;
import com.tomkeuper.bedwars.proxy.api.level.Level;
import com.tomkeuper.bedwars.proxy.configuration.ConfigPath;
import org.bukkit.entity.Player;

public class InternalLevel implements Level {

    @Override
    public String getLevel(Player p) {
        return PlayerLevel.getLevelByPlayer(p.getUniqueId()).getLevelName().isEmpty() ?
                BedWarsProxy.config.getString(ConfigPath.LEVEL_CONFIGURATION_DEFAULT_NAME).replace("{number}", String.valueOf(1)) :
                PlayerLevel.getLevelByPlayer(p.getUniqueId()).getLevelName();
    }

    @Override
    public int getPlayerLevel(Player p) {
        return PlayerLevel.getLevelByPlayer(p.getUniqueId()).getPlayerLevel();
    }

    @Override
    public String getRequiredXpFormatted(Player p) {
        return PlayerLevel.getLevelByPlayer(p.getUniqueId()).getFormattedRequiredXp();
    }

    @Override
    public String getProgressBar(Player p) {
        return PlayerLevel.getLevelByPlayer(p.getUniqueId()).getProgress();
    }

    @Override
    public int getCurrentXp(Player p) {
        return PlayerLevel.getLevelByPlayer(p.getUniqueId()).getCurrentXp();
    }

    @Override
    public String getCurrentXpFormatted(Player p) {
        return PlayerLevel.getLevelByPlayer(p.getUniqueId()).getFormattedCurrentXp();
    }

    @Override
    public int getRequiredXp(Player p) {
        return PlayerLevel.getLevelByPlayer(p.getUniqueId()).getNextLevelCost();
    }
}
