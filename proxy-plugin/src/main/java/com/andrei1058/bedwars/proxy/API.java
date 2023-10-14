package com.andrei1058.bedwars.proxy;

import com.andrei1058.bedwars.proxy.api.BedWars;
import com.andrei1058.bedwars.proxy.api.database.Database;
import com.andrei1058.bedwars.proxy.api.level.Level;
import com.andrei1058.bedwars.proxy.api.party.Party;
import com.andrei1058.bedwars.proxy.arenamanager.ArenaManager;
import com.andrei1058.bedwars.proxy.language.LanguageManager;

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
}
