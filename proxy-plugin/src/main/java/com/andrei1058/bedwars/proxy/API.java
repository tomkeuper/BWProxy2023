package com.andrei1058.bedwars.proxy;

import com.andrei1058.bedwars.proxy.api.BedWars;
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

    @Override
    public Level getLevelsUtil() {
        return BedWarsProxy.getLevelManager();
    }

    @Override
    public Party getPartyUtil() {
        return BedWarsProxy.getParty();
    }

    @Override
    public void setPartyAdapter(Party partyAdapter) throws IllegalAccessError {
        if (partyAdapter == null) return;
        if (partyAdapter.equals(BedWarsProxy.getParty())) return;
        BedWarsProxy.setParty(partyAdapter);
        BedWarsProxy.getPlugin().getLogger().log(java.util.logging.Level.WARNING,  "One of your plugins changed the Party adapter to: " + partyAdapter.getClass().getName());
    }
}
