package com.tomkeuper.bedwars.proxy.database;

import com.tomkeuper.bedwars.proxy.api.database.Database;

import java.util.UUID;

public class NoDatabase implements Database {
    @Override
    public void init() {

    }

    @Override
    public void updateLocalCache(UUID uuid) {

    }

    @Override
    public void close() {

    }

    @Override
    public Object[] getLevelData(UUID player) {
        return new Object[0];
    }

    @Override
    public void setLevelData(UUID player, int level, int xp, String displayName, int nextCost) {

    }

    @Override
    public void setLanguage(UUID player, String iso) {

    }

    @Override
    public String getLanguage(UUID player) {
        return null;
    }
}
