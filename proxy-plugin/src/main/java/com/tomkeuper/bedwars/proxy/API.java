package com.tomkeuper.bedwars.proxy;

import com.tomkeuper.bedwars.proxy.api.BedWars;
import com.tomkeuper.bedwars.proxy.api.addon.IAddonManager;
import com.tomkeuper.bedwars.proxy.api.communication.IRedisClient;
import com.tomkeuper.bedwars.proxy.api.database.Database;
import com.tomkeuper.bedwars.proxy.api.level.Level;
import com.tomkeuper.bedwars.proxy.api.party.Party;
import com.tomkeuper.bedwars.proxy.arenamanager.ArenaManager;
import com.tomkeuper.bedwars.proxy.language.LanguageManager;

public class API implements BedWars {
    /**
     * Get language util.
     *
     * @return utils.
     */
    @Override
    public LanguageUtil getLanguageUtil() {
        return LanguageManager.get();
    }

    /**
     * Get arena util.
     *
     * @return utils.
     */
    @Override
    public ArenaUtil getArenaUtil() {
        return ArenaManager.getInstance();
    }

    /**
     * Get levels methods.
     * @return utils
     */
    @Override
    public Level getLevelsUtil() {
        return BedWarsProxy.getLevelManager();
    }

    @Override
    public IAddonManager getAddonsUtil() {
        return BedWarsProxy.addonManager;
    }

    /**
     * Change the level interface.
     * @param level custom level class
     */
    @Override
    public void setLevelAdapter(Level level) {
        BedWarsProxy.setLevel(level);
    }

    /**
     * Get party methods.
     * @return utils
     */
    @Override
    public Party getPartyUtil() {
        return BedWarsProxy.getParty();
    }

    /**
     * Change the party interface.
     * You man need to unregister /party command yourself.
     * @param partyAdapter custom party class
     */
    @Override
    public void setPartyAdapter(Party partyAdapter) throws IllegalAccessError {
        if (partyAdapter == null) return;
        if (partyAdapter.equals(BedWarsProxy.getParty())) return;
        BedWarsProxy.setParty(partyAdapter);
        BedWarsProxy.getPlugin().getLogger().log(java.util.logging.Level.WARNING,  "One of your plugins changed the Party adapter to: " + partyAdapter.getClass().getName());
    }

    /**
     * Get database methods.
     * @return utils
     */
    @Override
    public Database getRemoteDatabase() {
        return BedWarsProxy.getRemoteDatabase();
    }

    /**
     * Change the database interface.
     * @param database custom database class
     */
    @Override
    public void setRemoteDatabase(Database database) {
        BedWarsProxy.setRemoteDatabase(database);
    }

    @SuppressWarnings("unused")
    @Override
    public IRedisClient getRedisClient() {
        return BedWarsProxy.getRedisConnection();
    }
}
